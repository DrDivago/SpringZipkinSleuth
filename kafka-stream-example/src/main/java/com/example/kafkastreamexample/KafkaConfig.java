package com.example.kafkastreamexample;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import brave.kafka.streams.KafkaStreamsTracing;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KafkaConfig {

    @Autowired
    Tracing tracer;

    @org.springframework.context.annotation.Bean
    public KafkaStreams kafkaStreams(@Value("${spring.application.name}") String appName, org.springframework.web.client.RestTemplate restTemplate) {
        final java.util.Properties props = new java.util.Properties();

        KafkaStreamsTracing tracing = KafkaStreamsTracing.create(KafkaTracing.create(tracer));

        Topology topology = new Topology();
        topology.addSource("SOURCE", "users")
                .addProcessor("PROCESS", tracing.processor("stream1", () -> new Stream1(restTemplate)), "SOURCE")
                .addProcessor("PROCESS2", tracing.processor("stream2",Stream2::new), "PROCESS")
                .addSink("SINK", "output", "PROCESS2");

        // stream config centric ones
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final KafkaStreams kafkaStreams = tracing.kafkaStreams(topology, props);
        kafkaStreams.start();

        return kafkaStreams;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
