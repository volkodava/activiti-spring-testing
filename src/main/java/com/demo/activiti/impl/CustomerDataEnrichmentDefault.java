package com.demo.activiti.impl;

import com.demo.activiti.CustomerDataEnrichment;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerDataEnrichmentDefault implements CustomerDataEnrichment {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void enrich(DelegateExecution execution) {
        Validate.notNull(execution);

        // some REST call may goes here
        throw new IllegalStateException("This enrichment method should not be ever called!");
    }
}
