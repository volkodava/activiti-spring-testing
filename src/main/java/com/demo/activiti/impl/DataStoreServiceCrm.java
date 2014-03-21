package com.demo.activiti.impl;

import com.demo.activiti.DataStoreService;
import org.apache.commons.lang3.Validate;

public class DataStoreServiceCrm implements DataStoreService {

    @Override
    public void store(String customerName, String details) {
        Validate.notBlank(customerName);
        Validate.notBlank(details);

        // some real store call may goes here
        throw new IllegalStateException("This store method should not be ever called!");
    }
}
