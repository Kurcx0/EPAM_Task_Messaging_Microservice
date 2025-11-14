package com.gymcrm.trainer_workload_service.messaging;

import com.gymcrm.trainer_workload_service.dto.messaging.TrainerWorkloadRequest;
import com.gymcrm.trainer_workload_service.dto.messaging.TrainerWorkloadResponse;
import com.gymcrm.trainer_workload_service.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import jakarta.jms.JMSException;
import jakarta.jms.Message;

@Component
public class TrainerWorkloadConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadConsumer.class);

    @Autowired
    private TrainerWorkloadService workloadService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${gym.jms.queue.trainer-workload-dlq}")
    private String deadLetterQueue;

    @JmsListener(destination = "${gym.jms.queue.trainer-workload}", containerFactory = "jmsListenerContainerFactory")
    public void receiveWorkloadRequest(TrainerWorkloadRequest request, Message message) throws JMSException {
        String transactionId = message.getJMSMessageID();
        logger.info("Transaction [{}] - Received trainer workload request for trainerId: {}",
                transactionId, request.getTrainerId());

        try {
            if (request.getTrainerId() == null) {
                logger.error("Transaction [{}] - Invalid request: trainerId is required", transactionId);
                sendToDLQ(message, "Missing trainerId");
                return;
            }

            TrainerWorkloadResponse response = workloadService.calculateWorkload(request);
            logger.info("Transaction [{}] - Processed workload request, total hours: {}",
                    transactionId, response.getTotalHours());

            message.acknowledge();
        } catch (Exception e) {
            logger.error("Transaction [{}] - Error processing workload request", transactionId, e);
            sendToDLQ(message, e.getMessage());
        }
    }

    @JmsListener(destination = "${gym.jms.queue.trainer-workload-dlq}", containerFactory = "dlqJmsListenerContainerFactory")
    public void processDLQ(Message message) throws JMSException {
        logger.warn("Processing dead letter: {}", message.getJMSMessageID());
    }

    private void sendToDLQ(Message message, String reason) throws JMSException {
        message.setStringProperty("error.reason", reason);
        jmsTemplate.convertAndSend(deadLetterQueue, message);
    }
}