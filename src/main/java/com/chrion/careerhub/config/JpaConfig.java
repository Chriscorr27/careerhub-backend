package com.chrion.careerhub.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.chrion.careerhub.application.repository",
                "com.chrion.careerhub.auth.repository",
                "com.chrion.careerhub.user.repository",
                "com.chrion.careerhub.job.repository",
                "com.chrion.careerhub.notification.repository"
        }
)
@EntityScan(
        basePackages = "com.chrion.careerhub"
)
public class JpaConfig {
}
