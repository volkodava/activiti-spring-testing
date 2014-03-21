package com.demo.activiti.diagram;

import com.demo.activiti.impl.mock.CustomerDataEnrichmentMock;
import com.demo.activiti.impl.mock.DataStoreServiceMock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.AssertionFailedError;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:activiti.cfg.xml")
public class ReviewSalesLeadTest {

    @Autowired(required = true)
    @Rule
    public ActivitiRule activitiRule;

    @Autowired(required = true)
    private RuntimeService runtimeService;

    @Autowired(required = true)
    private TaskService taskService;

    @Autowired(required = true)
    private DataStoreServiceMock dataStoreServiceMock;

    @Autowired(required = true)
    private CustomerDataEnrichmentMock customerDataEnrichmentMock;

    @Before
    public void setUp() throws Exception {
        // Normally the UI will do this automatically for us
        Authentication.setAuthenticatedUserId("kermit");
    }

    @After
    public void tearDown() throws Exception {
        Authentication.setAuthenticatedUserId(null);
    }

    @Test
    @Deployment(resources = {"diagrams/reviewSalesLead.bpmn"})
    public void testReviewSalesLeadSpringProcess() {

        // After starting the process, a task should be assigned to the 'initiator' (normally set by GUI)
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("details", "very interesting");
        variables.put("customerName", "Storebrand");
        String procId = runtimeService.startProcessInstanceByKey("reviewSalesLead", variables).getId();
        Task task = taskService.createTaskQuery().processInstanceId(procId).taskAssignee("kermit").singleResult();
        assertThat(task.getName(), equalTo("Provide new sales lead"));

        // After completing the task, the review subprocess will be active
        taskService.complete(task.getId());
        Task ratingTask = taskService.createTaskQuery().processInstanceId(procId).taskCandidateGroup("accountancy").singleResult();
        assertThat(ratingTask.getName(), equalTo("Review customer rating"));
        Task profitabilityTask = taskService.createTaskQuery().processInstanceId(procId).taskCandidateGroup("management").singleResult();
        assertThat(profitabilityTask.getName(), equalTo("Review profitability"));

        // Complete the management task by stating that not enough info was provided
        // This should throw the error event, which closes the subprocess
        variables = new HashMap<String, Object>();
        variables.put("notEnoughInformation", true);
        taskService.complete(profitabilityTask.getId(), variables);

        // The 'provide additional details' task should now be active
        Task provideDetailsTask = taskService.createTaskQuery().processInstanceId(procId).taskAssignee("kermit").singleResult();
        assertThat(provideDetailsTask.getName(), equalTo("Provide additional details"));

        // Providing more details (ie. completing the task), will activate the subprocess again
        taskService.complete(provideDetailsTask.getId());
        List<Task> reviewTasks = taskService.createTaskQuery().processInstanceId(procId).orderByTaskName().asc().list();
        assertThat(reviewTasks.get(0).getName(), equalTo("Review customer rating"));
        assertThat(reviewTasks.get(1).getName(), equalTo("Review profitability"));

        // Completing both tasks normally ends the process
        taskService.complete(reviewTasks.get(0).getId());
        variables.put("notEnoughInformation", false);
        taskService.complete(reviewTasks.get(1).getId(), variables);

        assertProcessEnded(procId);

        assertThat(dataStoreServiceMock.getCallNum(), equalTo(1));
        assertThat(customerDataEnrichmentMock.getCallNum(), equalTo(2));
    }

    public void assertProcessEnded(final String processInstanceId) {
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (processInstance != null) {
            throw new AssertionFailedError("Expected finished process instance '" + processInstanceId + "' but it was still in the db");
        }
    }
}
