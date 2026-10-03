package com.chrion.careerhub.user.repository;

import com.chrion.careerhub.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    @Transactional
    long deleteByRoleAndEmailNotIgnoreCase(User.Role role, String email);
}