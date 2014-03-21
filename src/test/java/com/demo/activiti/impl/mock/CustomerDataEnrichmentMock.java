package com.demo.activiti.impl.mock;

import com.demo.activiti.CustomerDataEnrichment;
import java.util.concurrent.atomic.AtomicInteger;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerDataEnrichmentMock implements CustomerDataEnrichment {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private AtomicInteger callNum = new AtomicInteger(0);

    @Override
    public void enrich(DelegateExecution execution) {
        Validate.notNull(execution);

        int numOfCalls = callNum.incrementAndGet();

        logger.info("[Mock] # {} --- Variables: {}", numOfCalls, execution.getVariables());
        logger.info("[Mock] # {} --- Enrichment done.", numOfCalls);
    }

    public int getCallNum() {
        return callNum.get();
    }
}
