package com.thiagosena.service.email.application.configs.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrap}")
    private String bootstrap;

    @Value(value = "${kafka.topic}")
    private String topic;

    @Value(value = "${kafka.partitions}")
    private int partitions;

    @Value(value = "${kafka.replications}")
    private short replications;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic reportsTopic() {
        return new NewTopic(this.topic, partitions, replications);
    }
}
