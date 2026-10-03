package com.chrion.careerhub.audit.repository;

import com.chrion.careerhub.audit.model.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AuditEventRepository extends MongoRepository<AuditEvent, String> {

    Page<AuditEvent> findByEntityId(String jobId, Pageable pageable);

    Page<AuditEvent> findByUserId(String userId, Pageable pageable);
}
