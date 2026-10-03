package com.chrion.careerhub.user.repository;

import com.chrion.careerhub.user.model.UserProfile;
import com.chrion.careerhub.user.model.UserProfileFileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileFileMetaDataRepository extends JpaRepository<UserProfileFileMetaData, UUID> {
}
