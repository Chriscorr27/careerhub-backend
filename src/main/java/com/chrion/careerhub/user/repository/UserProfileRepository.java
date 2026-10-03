package com.chrion.careerhub.user.repository;

import com.chrion.careerhub.user.model.UserProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    @EntityGraph(attributePaths = {"profileMetaData"})
    Optional<UserProfile> findByUserId(UUID userId);
}
