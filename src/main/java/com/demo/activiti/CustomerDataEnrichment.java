package com.demo.activiti;

import org.activiti.engine.delegate.DelegateExecution;

public interface CustomerDataEnrichment {

    void enrich(DelegateExecution execution);
}
