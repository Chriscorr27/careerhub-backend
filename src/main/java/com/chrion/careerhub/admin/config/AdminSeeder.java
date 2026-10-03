package com.chrion.careerhub.admin.config;

import com.chrion.careerhub.user.model.User;
import com.chrion.careerhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String @NonNull ... args) {
        // delete all ADMIN users with different email
        long deleteCount = userRepository.deleteByRoleAndEmailNotIgnoreCase(User.Role.ADMIN, adminEmail);
        log.info("Deleted {} records", deleteCount);
        User admin = userRepository.findByEmailIgnoreCase(adminEmail)
                .orElse(null);

        if (admin == null) {
            admin = new User();
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole(User.Role.ADMIN);
        }

        userRepository.save(admin);
        log.info("Initialized System Admin account: {}", adminEmail);
    }
}
