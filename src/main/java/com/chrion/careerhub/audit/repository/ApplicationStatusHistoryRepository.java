package com.chrion.careerhub.audit.repository;

import com.chrion.careerhub.audit.model.ApplicationStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApplicationStatusHistoryRepository extends MongoRepository<ApplicationStatusHistory, String> {

    List<ApplicationStatusHistory> findByApplicationIdOrderByChangedAtAsc(String application);

    Page<ApplicationStatusHistory> findByUserId(String userId, Pageable pageable);
}