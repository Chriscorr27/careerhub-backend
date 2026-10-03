package com.chrion.careerhub.kafka;

import com.chrion.careerhub.constant.EventVersions;
import com.chrion.careerhub.event.model.JobCreatedEvent;
import com.chrion.careerhub.event.model.JobStatusChangedEvent;
import com.chrion.careerhub.integration.BaseIntegrationTest;
import com.chrion.careerhub.job.model.JobStatus;
import com.chrion.careerhub.event.producer.EventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class KafkaIntegrationTest extends BaseIntegrationTest {

    static KafkaContainer kafka = new KafkaContainer("apache/kafka:4.2.1");

    @Autowired
    private EventProducer producer;

    static {
        kafka.start();
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    void shouldPublishJobStatusChangedEvent() {
        JobStatusChangedEvent event =
                new JobStatusChangedEvent(
                        "test-event-1",
                        EventVersions.JOB_STATUS_CHANGED,
                        "job-123",
                        "user-123",
                        JobStatus.OPEN,
                        JobStatus.CLOSED,
                        "Job position Closed",
                        Instant.now(),
                        "user-123"
                );

        producer.publishJobStatusChangeEvent(event);

        assertNotNull(event.eventId());
    }

    @Test
    void shouldPublishJobCreateEvent() {
        JobCreatedEvent event =
                new JobCreatedEvent(
                        "test-event-1",
                        EventVersions.JOB_CREATED,
                        "job-123",
                        "user-123",
                        "Job Created Test",
                        Instant.now()
                );

        producer.publishJobCreatedEvent(event);

        assertNotNull(event.eventId());
    }
}