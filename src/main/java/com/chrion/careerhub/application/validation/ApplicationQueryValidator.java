package com.chrion.careerhub.application.validation;

import com.chrion.careerhub.common.exception.CustomException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Component
public class ApplicationQueryValidator {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
                    "createdAt",
                    "updatedAt",
                    "status",
                    "appliedAt"
            );



    public void validateSortField(String sortBy) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new CustomException(
                    "Invalid sort field: " + sortBy,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public void validateSalaryRange(BigDecimal salaryMin, BigDecimal salaryMax) {

        if (salaryMin != null && salaryMin.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException("Minimum salary cannot be negative", HttpStatus.BAD_REQUEST);
        }

        if (salaryMax != null && salaryMax.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException("Maximum salary cannot be negative", HttpStatus.BAD_REQUEST);
        }

        if (salaryMin != null && salaryMax != null && salaryMin.compareTo(salaryMax) > 0) {

            throw new CustomException("Minimum salary cannot be greater than maximum salary", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateDates(Instant fromDate, Instant toDate) {

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new CustomException("fromDate cannot be after toDate", HttpStatus.BAD_REQUEST);
        }
    }
}