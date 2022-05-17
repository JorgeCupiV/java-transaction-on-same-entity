package com.jorgecupi.java.singleentitytransaction.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusClientBuilder.ServiceBusReceiverClientBuilder;
import com.azure.messaging.servicebus.ServiceBusClientBuilder.ServiceBusSenderClientBuilder;

import org.springframework.context.annotation.Bean;

public class ServiceBusConfig {
  String connectionString = "connectionString";
  String topic = "topic";
  String subscription = "subscription";

  @Bean
  public ServiceBusSenderClientBuilder senderClientBuilder() {
      return new ServiceBusClientBuilder()
          .connectionString(connectionString)
          .sender()
          .topicName(topic);
  }

  @Bean
  public ServiceBusReceiverClientBuilder receiverClientBuilder() {
      return new ServiceBusClientBuilder()
          .connectionString(connectionString)
          .receiver()
          .topicName(topic)
          .subscriptionName(subscription);
  }
}
