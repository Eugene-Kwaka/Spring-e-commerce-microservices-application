package com.eugene.order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaOrderTopicConfig {

    /**
     * NewTopic is a class in Apache Kafka used to define a new topic configuration.
     * In the context of your order-service application, it is used to create a new Kafka topic named "order-topic".
     * This topic will be used to publish and consume messages related to orders.*/
    @Bean
    public NewTopic orderTopic(){
        //  TopicBuilder.name("order-topic").build() creates a new NewTopic instance with the name "order-topic".
        return TopicBuilder
                .name("order-topic")
                .build();
    }
}
