package com.chrion.careerhub.application.specification;

import com.chrion.careerhub.application.dto.ApplicationSearchRequest;
import com.chrion.careerhub.application.model.Application;
import com.chrion.careerhub.job.dto.JobSearchRequest;
import com.chrion.careerhub.job.model.Job;
import com.chrion.careerhub.job.model.JobStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ApplicationSpecification {

    private ApplicationSpecification() {}

    public static Specification<Application> filter(UUID userId, ApplicationSearchRequest request) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Join<Application, Job> jobJoin = root.join("job");;

            /*
             * ----------------------------------------------------
             * FILTERS ON THE JOB ENTITY (Using jobJoin)
             * ----------------------------------------------------
             */

            // Keyword search (searches Job fields)
            if (request.keyword() != null && !request.keyword().isBlank()) {
                String keyword = "%" + request.keyword().trim().toLowerCase() + "%";

                Predicate company = criteriaBuilder.like(criteriaBuilder.lower(jobJoin.get("company")), keyword);
                Predicate jobTitle = criteriaBuilder.like(criteriaBuilder.lower(jobJoin.get("jobTitle")), keyword);
                Predicate description = criteriaBuilder.like(criteriaBuilder.lower(jobJoin.get("description")), keyword);

                predicates.add(criteriaBuilder.or(company, jobTitle, description));
            }

            // Company
            if (request.company() != null && !request.company().isBlank()) {
                String companyKeywordPattern = "%" + request.company().trim().toLowerCase() + "%";
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(jobJoin.get("company")), companyKeywordPattern
                        )
                );
            }

            // Location
            if (request.location() != null && !request.location().isBlank()) {
                String locationKeywordPattern = "%" + request.location().trim().toLowerCase() + "%";
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(jobJoin.get("location")), locationKeywordPattern
                        )
                );
            }

            // Job Status
            if (request.jobStatus() != null) {
                predicates.add(
                        // Assuming the field in the Job entity is named "status"
                        criteriaBuilder.equal(jobJoin.get("status"), request.jobStatus())
                );
            }

            // Employment type
            if (request.employmentType() != null) {
                predicates.add(
                        criteriaBuilder.equal(jobJoin.get("employmentType"), request.employmentType())
                );
            }

            // Minimum salary
            if (request.salaryMin() != null) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.isNull(jobJoin.get("salaryMax")),
                                criteriaBuilder.greaterThanOrEqualTo(
                                        jobJoin.get("salaryMax"), request.salaryMin()
                                )
                        )
                );
            }

            // Maximum salary
            if (request.salaryMax() != null) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.isNull(jobJoin.get("salaryMin")),
                                criteriaBuilder.lessThanOrEqualTo(
                                        jobJoin.get("salaryMin"), request.salaryMax()
                                )
                        )
                );
            }

            /*
             * ----------------------------------------------------
             * FILTERS ON THE APPLICATION ENTITY (Using root)
             * ----------------------------------------------------
             */
            // only HR and OWN User can see its Applications
            predicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("userId"), userId),
                            criteriaBuilder.equal(root.get("hrUserId"), userId)
                    )
            );

            // Application Status
            if (request.applicationStatus() != null) {
                predicates.add(
                        // Assuming the field in the Application entity is named "status"
                        criteriaBuilder.equal(root.get("status"), request.applicationStatus())
                );
            }

            // Created from date (Application creation)
            if (request.fromDate() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.fromDate())
                );
            }

            // Created until date (Application creation)
            if (request.toDate() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.toDate())
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}