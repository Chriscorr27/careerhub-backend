package com.chrion.careerhub.job.specification;

import com.chrion.careerhub.job.dto.JobSearchRequest;
import com.chrion.careerhub.job.model.Job;
import com.chrion.careerhub.job.model.JobStatus;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class JobSpecification {

    private JobSpecification() {}

    public static Specification<Job> filter(JobSearchRequest request) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            /*
             * Keyword search
             */
            if (request.keyword() != null && !request.keyword().isBlank()) {

                String keyword = "%" + request.keyword().trim().toLowerCase() + "%";

                Predicate company = criteriaBuilder.like(criteriaBuilder.lower(root.get("company")), keyword);

                Predicate jobTitle = criteriaBuilder.like(criteriaBuilder.lower(root.get("jobTitle")), keyword);

                Predicate description = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), keyword);

                predicates.add(criteriaBuilder.or(company, jobTitle, description));
            }

            /*
             * Company
             */
            if (request.company() != null && !request.company().isBlank()) {
                String companyKeywordPattern = "%" + request.company().trim().toLowerCase() + "%";
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("company")), companyKeywordPattern
                        )
                );
            }

            /*
             * Location
             */
            if (request.location() != null && !request.location().isBlank()) {
                String locationKeywordPattern = "%" + request.location().trim().toLowerCase() + "%";
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("location")),locationKeywordPattern
                        )
                );
            }

            /*
             * Status
             */
            if (request.status() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("status"), request.status())
                );
            }

            /*
             * Employment type
             */
            if (request.employmentType() != null) {

                predicates.add(
                        criteriaBuilder.equal(root.get("employmentType"), request.employmentType())
                );
            }

            /*
             * Minimum salary
             */
            if (request.salaryMin() != null) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.isNull(root.get("salaryMax")),
                                criteriaBuilder.greaterThanOrEqualTo(
                                        root.get("salaryMax"), request.salaryMin()
                                )
                        )
                );
            }

            /*
             * Maximum salary
             */
            if (request.salaryMax() != null) {

                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.isNull(root.get("salaryMin")),
                                criteriaBuilder.lessThanOrEqualTo(
                                        root.get("salaryMin"),
                                        request.salaryMax()
                                )
                        )
                );
            }

            /*
             * Created from date
             */
            if (request.fromDate() != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.fromDate())
                );
            }

            /*
             * Created until date
             */
            if (request.toDate() != null) {

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.toDate())
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}