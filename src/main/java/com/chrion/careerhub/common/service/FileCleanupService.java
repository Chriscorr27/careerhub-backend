package com.chrion.careerhub.common.service;

import com.chrion.careerhub.common.model.FileMetadata;
import com.chrion.careerhub.common.model.UploadStatus;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileCleanupService {
    private final MongoTemplate mongoTemplate;

    @Scheduled(cron = "0 * * * * *")
    public void markExpiredPresignedUrls() {

        Query query = new Query(
                Criteria.where("expireAt").lt(Instant.now())
                        .and("uploadStatus").ne(UploadStatus.EXPIRED)
        );

        Update update = new Update()
                .set("uploadStatus", UploadStatus.EXPIRED)
                .set("preSignedUrl", null);

        UpdateResult result = mongoTemplate.updateMulti(query, update, FileMetadata.class);

        if (result.getModifiedCount() > 0) {
            log.info("Successfully updated {} file records to EXPIRED status.", result.getModifiedCount());
        }
    }
}
