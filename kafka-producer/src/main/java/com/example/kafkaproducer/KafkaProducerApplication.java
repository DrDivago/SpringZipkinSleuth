package com.example.kafkaproducer;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class KafkaProducerApplication implements CommandLineRunner {

    @Value("${kafka.bootstrap.servers}")
    private String kafkaBootstrapServers;

    @Value("${zookeeper.host}")
    String zookeeperHost;

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String topic;
        if (args.length == 0) {
            topic = "users";
        }
        else {
            topic = args[0];
        }

        Properties producerProperties = getProperties();
        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties);

        createTopic(producerProperties, topic);

        for (int index = 0; index < 100000; index++) {
            producer.send(new ProducerRecord<>("users", UUID.randomUUID().toString()));
        }
        producer.flush();
    }

    private Properties getProperties() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", kafkaBootstrapServers);
        producerProperties.put("acks", "all");
        producerProperties.put("retries", 0);
        producerProperties.put("batch.size", 16384);
        producerProperties.put("linger.ms", 1);
        producerProperties.put("buffer.memory", 33554432);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return producerProperties;
    }

    private void createTopic(Properties properties, String topic) {

        AdminClient adminClient = AdminClient.create(properties);
        ListTopicsResult topicsResult = adminClient.listTopics();
        try {
            if (!topicsResult.names().get().contains(topic)) {
                NewTopic newTopic = new NewTopic(topic, 1, (short)3);

                List<NewTopic> newTopics = new ArrayList<>();
                newTopics.add(newTopic);

                adminClient.createTopics(newTopics);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            adminClient.close();
        }
    }
}
