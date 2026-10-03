package com.chrion.careerhub.redis;

import com.chrion.careerhub.integration.BaseIntegrationTest;
import com.chrion.careerhub.job.dto.CreateJobRequest;
import com.chrion.careerhub.job.dto.JobResponse;
import com.chrion.careerhub.job.model.EmploymentType;
import com.chrion.careerhub.job.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class RedisIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JobService jobService;

    @Autowired
    private CacheManager cacheManager;

    private UUID jobId;

    @BeforeEach
    void setUp(){
        CreateJobRequest createJobRequest = new CreateJobRequest(
                "Test Job",
                "Software Engineer",
                "Mumbai",
                "https://example.com/job/123",
                EmploymentType.FULL_TIME,
                "LinkedIn",
                new BigDecimal(250000),
                new BigDecimal(350000),
                "INR",
                "This is a test job description.",
                "These are test notes."
        );

        JobResponse jobResponse = jobService.createJob(createJobRequest);
        jobId = jobResponse.id();

    }

    @Test
    void shouldStoreAndRetrieveValue() {

        redisTemplate.opsForValue().set("careerhub:test", "hello");

        String value = redisTemplate.opsForValue().get("careerhub:test");

        assertEquals("hello", value);
    }

    @Test
    void shouldCacheJobWhenJobIsRetrieved() {
        Cache cache = cacheManager.getCache("jobs");
        assertNotNull(cache);
        // First GET → loads DB and puts value in Redis
        jobService.getJob(jobId);
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            // This will retry until it stops throwing an AssertionError,
            // or until the 2 seconds run out.
            assertNotNull(cache.get(jobId), "Job should be cached in Redis");
        });

    }

    @Test
    void shouldEvictJobWhenJobIsDeleted() {
        // First Delete → removes from DB and evicts from Redis
        jobService.deleteJob(jobId);
        Cache cache = cacheManager.getCache("jobs");
        assertNotNull(cache);
        // check if the job is removed from Redis cache
        assertNull(cache.get(jobId));

    }
}
