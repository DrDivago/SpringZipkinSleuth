package com.example.restservice;

import brave.sampler.Sampler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.logging.Logger;


@RestController
class RestService{

    private static final Logger logger = Logger.getLogger(RestService.class.getName());

    @Autowired
    RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();

    }
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }


    @GetMapping(value="/random")
    public String randomGenerator() {
        logger.info("***Inside random service...");
        logger.info("Waiting 5 sec...");
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        logger.info("Generated random value");
        return UUID.randomUUID().toString();
    }
}
