package com.jorgecupi.java.singleentitytransaction.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusClientBuilder.ServiceBusReceiverClientBuilder;
import com.azure.messaging.servicebus.ServiceBusClientBuilder.ServiceBusSenderClientBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

  
@Service
public class SingleEntityTransactionService {
  
    @Autowired
    private ServiceBusSenderClientBuilder senderClientBuilder;
  
    @Autowired
    private ServiceBusReceiverClientBuilder receiverClientBuilder;
  
    private static final String FIRST_MESSAGE_TO_SEND = "This is the first message";
    
    @Scheduled(fixedDelay = 10000)
    public void singleTransactionHandler() {
        
        var firstMessage = createServiceBusMessage(FIRST_MESSAGE_TO_SEND);
        var senderClient = senderClientBuilder.buildClient();
        var receiverClient = receiverClientBuilder.buildClient();
    
        var transactionForSender = senderClient.createTransaction();
        System.out.println("Transaction for sender started");

        senderClient.sendMessage(firstMessage, transactionForSender);
        System.out.println("Message sent");

        senderClient.commitTransaction(transactionForSender);    
        System.out.println("Transaction committed for sender");

        var transactionForReceiver = receiverClient.createTransaction();
        System.out.println("Transaction for receiver started");

        var receivedMessage = receiverClient.receiveMessages(1, Duration.ofMillis(5000)).iterator().next();
        System.out.println("Message received");

        receiverClient.complete(receivedMessage);
        System.out.println("Message receiver completed");

        receiverClient.commitTransaction(transactionForReceiver);
        System.out.println("Transaction committed for receiver");
    }

    private ServiceBusMessage createServiceBusMessage(String messageToSend) {
        var serviceBusMessage = new ServiceBusMessage(
            BinaryData.fromBytes(messageToSend.getBytes(StandardCharsets.UTF_8)));
        serviceBusMessage.setContentType("application/json");
        return serviceBusMessage; 
    }
}