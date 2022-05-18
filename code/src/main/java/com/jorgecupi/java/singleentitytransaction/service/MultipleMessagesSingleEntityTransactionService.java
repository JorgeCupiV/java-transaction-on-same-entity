package com.jorgecupi.java.singleentitytransaction.service;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusClientBuilder.ServiceBusSenderClientBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

  
@Service
public class MultipleMessagesSingleEntityTransactionService {
  
    @Autowired
    private ServiceBusSenderClientBuilder senderClientBuilder;
  
    private static final String EXPECTED_MESSAGES_TO_SEND = "The service is supposed to send %d messages";
    private static final String TRANSACTION_STARTED = "Transaction for sender started";
    private static final String MESSAGE_TO_SEND = "This is the message # %d";
    private static final String MESSAGE_SENT= "Message sent: ";
    private static final String SERVICE_STOPPED = "The service stopped as the random number for interruption (%d) is greater than the interruption number set to %s";
    private static final String TRANSACTION_COMMITTED = "Transaction committed for sender! All %d messages have been sent to Service Bus";
    private static final String TRANSACTION_NOT_COMMITTED = "Since not all of the messages have been sent. The sender has not completed the transaction and all the messages will not be sent to service bus";
    
    private int ceilingForInterruptionNumber = 11;
    private int interruptionNumber = 8;
    
    @Scheduled(fixedDelay = 10000)
    public void singleTransactionHandler() throws InterruptedException {
        System.out.println("--------------------");
        
        var senderClient = senderClientBuilder.buildClient();
    
        int randomNumber = new Random().nextInt(11);
        System.out.println(String.format(EXPECTED_MESSAGES_TO_SEND, randomNumber));
        int i = 1;

        boolean interruptionOnPurposeHappened = false;
        var transactionForSender = senderClient.createTransaction();
        System.out.println(TRANSACTION_STARTED);
        while(i <= randomNumber) {
            String messageToSendText = String.format(MESSAGE_TO_SEND, i);
            var messageToSend = createServiceBusMessage(messageToSendText);
            System.out.println(MESSAGE_SENT + messageToSendText);
            senderClient.sendMessage(messageToSend, transactionForSender);
            i++;

            int randomNumberForInterruption = new Random().nextInt(ceilingForInterruptionNumber);
            if(randomNumberForInterruption > interruptionNumber) {
                System.out.println(String.format(SERVICE_STOPPED, randomNumberForInterruption, interruptionNumber));
                interruptionOnPurposeHappened = true;
                break;
            }
        }

        if(!interruptionOnPurposeHappened) {
            senderClient.commitTransaction(transactionForSender); 

            System.out.println(String.format(TRANSACTION_COMMITTED, randomNumber));
        } else {
            System.out.println(TRANSACTION_NOT_COMMITTED);
        }
    }

    private ServiceBusMessage createServiceBusMessage(String messageToSend) {
        var serviceBusMessage = new ServiceBusMessage(
            BinaryData.fromBytes(messageToSend.getBytes(StandardCharsets.UTF_8)));
        serviceBusMessage.setContentType("application/json");
        return serviceBusMessage; 
    }
}