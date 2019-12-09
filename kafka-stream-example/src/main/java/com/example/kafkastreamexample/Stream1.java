package com.example.kafkastreamexample;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Stream1 extends AbstractProcessor<String, String> {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Stream1.class);

    private org.springframework.web.client.RestTemplate restTemplate;

    @Autowired
    public Stream1(org.springframework.web.client.RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void process(String key, String value) {
        logger.info("****Inside Stream1****");
        logger.info("key" + key + " " + value );
        String newValue = value + UUID.randomUUID().toString();

        logger.info("Call http service random");
        String response = restTemplate.exchange("http://localhost:8082/random", HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
        }).getBody();

        context().forward(key, newValue);
        context().commit();
    }
}
