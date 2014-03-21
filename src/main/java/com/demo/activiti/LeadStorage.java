package com.demo.activiti;

import org.activiti.engine.delegate.DelegateExecution;

public interface LeadStorage {

    void store(DelegateExecution execution);
}
