package com.assignment.project.PaymentMs.config;

import com.assignment.project.PaymentMs.model.Bus;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Bus> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Bus> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Bus> consumerFactory() {
        JsonDeserializer<Bus> jsonDeserializer=new JsonDeserializer<>(Bus.class,false);
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),new StringDeserializer(),jsonDeserializer);
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "payment-consumer-group");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }
}

