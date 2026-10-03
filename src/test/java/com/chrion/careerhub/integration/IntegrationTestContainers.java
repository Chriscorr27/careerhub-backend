package com.chrion.careerhub.integration;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.MongoDBContainer;

import java.util.List;
import java.util.UUID;


public abstract class IntegrationTestContainers {


    protected static final MySQLContainer<?> MYSQL = new MySQLContainer("mysql:8.0")
                    .withDatabaseName("career_hub")
                    .withUsername("test_user")
                    .withPassword("test_password");


    protected static final MongoDBContainer MONGO = new MongoDBContainer("mongo:6.0");



    protected static final KafkaContainer KAFKA = new KafkaContainer("apache/kafka:4.2.1");

    protected static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);


    static{
        MYSQL.start();
        MONGO.start();
        KAFKA.start();
        REDIS.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {

        // JWT
        registry.add("app.jwt.secret",()->"78AaiBmqIz45bsXngI6PznPdJgYov78Y");

        // MySQL
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);

        registry.add("spring.datasource.username", MYSQL::getUsername);

        registry.add("spring.datasource.password", MYSQL::getPassword);

        // MongoDB
        registry.add("spring.data.mongodb.uri", ()->MONGO.getReplicaSetUrl()+"?uuidRepresentation=STANDARD");

        // Kafka
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.consumer.group-id",()->"careerhub");

        // Redis
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", ()->REDIS.getMappedPort(6379));
    }
}