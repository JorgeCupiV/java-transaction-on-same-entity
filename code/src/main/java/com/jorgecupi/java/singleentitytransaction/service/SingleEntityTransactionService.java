package com.jorgecupi.java.singleentitytransaction.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusTransactionContext;
import com.azure.messaging.servicebus.ServiceBusClientBuilder.ServiceBusReceiverClientBuilder;
import com.azure.messaging.servicebus.ServiceBusClientBuilder.ServiceBusSenderClientBuilder;
import com.azure.messaging.servicebus.models.CompleteOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SingleEntityTransactionService {

  @Autowired
  private ServiceBusSenderClientBuilder senderClientBuilder;

  @Autowired
  private ServiceBusReceiverClientBuilder receiverClientBuilder;

  private static final String FIRST_MESSAGE_TO_SEND = "This is the first message";
  private static final String SECOND_MESSAGE_TO_SEND = "This is the second message";

  public void singleTransactionHandler() {
      
    var firstMessage = createServiceBusMessage(FIRST_MESSAGE_TO_SEND);
    var secondMessage = createServiceBusMessage(SECOND_MESSAGE_TO_SEND);
    var senderClient = senderClientBuilder.buildClient();
    var receiverClient = receiverClientBuilder.buildClient();

    senderClient.sendMessage(firstMessage);
    var receivedMessage = receiverClient.receiveMessages(1, Duration.ofMillis(15000)).iterator().next();
    ServiceBusTransactionContext transactionId = senderClient.createTransaction();
    senderClient.sendMessage(secondMessage);
    receiverClient.complete(receivedMessage, new CompleteOptions().setTransactionContext(transactionId));
    senderClient.commitTransaction(transactionId);    
  }

  private ServiceBusMessage createServiceBusMessage(String messageToSend) {
    var serviceBusMessage = new ServiceBusMessage(
        BinaryData.fromBytes(messageToSend.getBytes(StandardCharsets.UTF_8)));
    serviceBusMessage.setContentType("application/json");
    return serviceBusMessage; 
  }
}
