package com.chrion.careerhub.resume.repository;

import com.chrion.careerhub.resume.model.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository
        extends MongoRepository<Resume, String> {

    List<Resume> findAllByUserId(UUID userId);

    Optional<Resume> findByIdAndUserId(
            String id,
            UUID userId
    );
}
