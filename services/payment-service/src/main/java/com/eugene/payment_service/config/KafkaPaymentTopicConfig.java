package com.eugene.payment_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaPaymentTopicConfig {

    @Bean
    public NewTopic paymentTopic(){
        //  TopicBuilder.name("payment-topic").build() creates a new NewTopic instance with the name "payment-topic".
        return TopicBuilder
                .name("payment-topic")
                .build();
    }
}
