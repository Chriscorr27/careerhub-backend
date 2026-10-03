package com.chrion.careerhub.audit.repository;

import com.chrion.careerhub.audit.model.JobStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JobStatusHistoryRepository extends MongoRepository<JobStatusHistory, String> {

    List<JobStatusHistory> findByJobIdOrderByChangedAtAsc(String jobId);

    Page<JobStatusHistory> findByUserId(String userId, Pageable pageable);
}