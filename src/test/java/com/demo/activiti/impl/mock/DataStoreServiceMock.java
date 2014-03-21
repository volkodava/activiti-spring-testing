package com.demo.activiti.impl.mock;

import com.demo.activiti.DataStoreService;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStoreServiceMock implements DataStoreService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private AtomicInteger callNum = new AtomicInteger(0);

    @Override
    public void store(String customerName, String details) {
        Validate.notBlank(customerName);
        Validate.notBlank(details);

        int numOfCalls = callNum.incrementAndGet();

        logger.info("[Mock] # {} --- Data for {} persisted.", numOfCalls, customerName);
    }

    public int getCallNum() {
        return callNum.get();
    }
}
