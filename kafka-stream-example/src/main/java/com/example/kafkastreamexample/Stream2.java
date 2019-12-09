package com.example.kafkastreamexample;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Stream2 extends AbstractProcessor<String, String> {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Stream2.class);

    @Override
    public void process(String key, String value) {
        logger.info("***Stream 2 get: " + value + "***");
        String newValue = value + new Date().toString();
        context().forward(key, newValue);
        context().commit();
    }
}

