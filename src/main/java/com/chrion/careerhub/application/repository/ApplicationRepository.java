package com.chrion.careerhub.application.repository;

import com.chrion.careerhub.application.model.Application;
import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.dashboard.dto.ApplicationCountPerJobResponse;
import com.chrion.careerhub.dashboard.dto.ApplicationStatusCountResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID>, JpaSpecificationExecutor<Application> {
    @Query("""
        SELECT a
        FROM Application a
        WHERE (a.userId = :userId
          OR a.hrUserId = :userId)
          AND a.id = :id
        """)
    Optional<Application> findByIdAndUserId(UUID id, UUID userId);


    Optional<Application> findByUserIdAndJobIdAndStatusNot(UUID userId, UUID jobId, ApplicationStatus status);

    long countByUserId(UUID userId);

    long countByUserIdAndStatus(UUID userId, ApplicationStatus status);

    long countByHrUserIdAndJobId(UUID hrUserId, UUID jobId);

    long countByHrUserIdAndJobIdAndStatus(UUID userId, UUID jobId, ApplicationStatus status);

    @Query("""
        SELECT new com.chrion.careerhub.dashboard.dto.ApplicationStatusCountResponse(
            a.status,
            COUNT(a)
        )
        FROM Application a
        WHERE a.userId = :userId
        GROUP BY a.status
        ORDER BY a.status
        """)
    List<ApplicationStatusCountResponse> countApplicationByStatus(@Param("userId") UUID userId);

    @Query("""
        SELECT new com.chrion.careerhub.dashboard.dto.ApplicationCountPerJobResponse(
            j.id,
            j.company,
            j.jobTitle,
            j.status,
            COUNT(a.id)
        )
        FROM Application a
        JOIN a.job j
        WHERE a.hrUserId = :hrUserId
        GROUP BY j.id, j.company, j.jobTitle, j.status
        """)
    List<ApplicationCountPerJobResponse> countApplicationPerJob(@Param("hrUserId") UUID hrUserId);

    @Query("""
        SELECT FUNCTION('DATE', a.createdAt), COUNT(a)
        FROM Application a
        WHERE (a.userId = :userId OR a.hrUserId = :userId)
          AND a.createdAt >= :startDate
          AND a.createdAt < :endDate
        GROUP BY FUNCTION('DATE', a.createdAt)
        ORDER BY FUNCTION('DATE', a.createdAt)
        """)
    List<Object[]> findApplicationTrend(
            @Param("userId") UUID userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
        SELECT a
        FROM Application a
        WHERE (a.userId = :userId OR a.hrUserId = :userId)
        ORDER BY a.createdAt DESC
        LIMIT 10
        """)
    List<Application> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);

}
