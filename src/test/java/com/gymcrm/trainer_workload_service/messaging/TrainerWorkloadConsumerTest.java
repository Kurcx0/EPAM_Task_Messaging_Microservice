package com.gymcrm.trainer_workload_service.messaging;

import com.gymcrm.trainer_workload_service.dto.messaging.TrainerWorkloadRequest;
import com.gymcrm.trainer_workload_service.dto.messaging.TrainerWorkloadResponse;
import com.gymcrm.trainer_workload_service.service.TrainerWorkloadService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadConsumerTest {

    @Mock
    private TrainerWorkloadService workloadService;

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private Message message;

    @InjectMocks
    private TrainerWorkloadConsumer consumer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(consumer, "deadLetterQueue", "test-dlq");
    }

    @Test
    void testReceiveWorkloadRequest() throws JMSException {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerId(1L);

        TrainerWorkloadResponse response = new TrainerWorkloadResponse();
        response.setTrainerId(1L);
        response.setTotalHours(10);

        when(message.getJMSMessageID()).thenReturn("test-message-id");
        when(workloadService.calculateWorkload(any(TrainerWorkloadRequest.class))).thenReturn(response);

        consumer.receiveWorkloadRequest(request, message);

        verify(workloadService, times(1)).calculateWorkload(eq(request));
        verify(message, times(1)).acknowledge();
        verify(jmsTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void testReceiveWorkloadRequest_InvalidRequest() throws JMSException {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        when(message.getJMSMessageID()).thenReturn("test-message-id");

        consumer.receiveWorkloadRequest(request, message);

        verify(workloadService, never()).calculateWorkload(any());
        verify(message, never()).acknowledge();
        verify(jmsTemplate, times(1)).convertAndSend(eq("test-dlq"), eq(message));
    }

    @Test
    void testReceiveWorkloadRequest_ServiceException() throws JMSException {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerId(1L);

        when(message.getJMSMessageID()).thenReturn("test-message-id");
        when(workloadService.calculateWorkload(any())).thenThrow(new RuntimeException("Test exception"));

        consumer.receiveWorkloadRequest(request, message);

        verify(workloadService, times(1)).calculateWorkload(eq(request));
        verify(message, never()).acknowledge();
        verify(jmsTemplate, times(1)).convertAndSend(eq("test-dlq"), eq(message));
    }
}