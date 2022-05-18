# java-transaction-on-same-entity

This repository has two samples that demonstrate the usage of Transactions in Service Bus with Java:

- [SingleEntityTransactionService.java](/code/src/main/java/com/jorgecupi/java/singleentitytransaction/service/SingleEntityTransactionService.java) - That sends and receives a single message through the same topic using Transactions for both the sender and receiver.
- [MultipleMessagesSingleEntityTransactionService.java](/code/src/main/java/com/jorgecupi/java/singleentitytransaction/service/MultipleMessagesSingleEntityTransactionService.java) - It is a service that, every 10 seconds, randomly tries sends 1-10 messages to Service Bus and can randomly interrupted. If the sending of the messages is not completed due to the random interruption then the sender does not commit the transaction and all of the messages are not sent into Service Bus.
