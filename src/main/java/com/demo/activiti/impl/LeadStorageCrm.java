package com.demo.activiti.impl;

import com.demo.activiti.LeadStorage;
import com.demo.activiti.DataStoreService;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class LeadStorageCrm implements LeadStorage, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private DataStoreService dataStoreService;

    @Override
    public void store(DelegateExecution execution) {
        Validate.notNull(execution);

        String customerName = (String) execution.getVariable("customerName");
        String details = (String) execution.getVariable("details");

        dataStoreService.store(customerName, details);
    }

    public DataStoreService getDataStoreService() {
        return dataStoreService;
    }

    public void setDataStoreService(DataStoreService dataStoreService) {
        this.dataStoreService = dataStoreService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(dataStoreService);
    }
}
