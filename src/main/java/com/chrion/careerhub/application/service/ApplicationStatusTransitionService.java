package com.chrion.careerhub.application.service;

import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class ApplicationStatusTransitionService {

    private final Map<ApplicationStatus, Set<ApplicationStatus>> allowedTransitions = new EnumMap<>(ApplicationStatus.class);

    public ApplicationStatusTransitionService() {
        log.info("Initializing job status transition service");

        // SAVED --> APPLIED/WITHDRAWN
        allowedTransitions.put(
                ApplicationStatus.SAVED,
                EnumSet.of(
                        ApplicationStatus.APPLIED,
                        ApplicationStatus.WITHDRAWN
                )
        );

        // APPLIED --> SCREENING/REJECTED/WITHDRAWN
        allowedTransitions.put(
                ApplicationStatus.APPLIED,
                EnumSet.of(
                        ApplicationStatus.SCREENING,
                        ApplicationStatus.REJECTED,
                        ApplicationStatus.WITHDRAWN
                )
        );

        // SCREENING --> INTERVIEW/REJECTED/WITHDRAWN
        allowedTransitions.put(
                ApplicationStatus.SCREENING,
                EnumSet.of(
                        ApplicationStatus.INTERVIEW,
                        ApplicationStatus.REJECTED,
                        ApplicationStatus.WITHDRAWN
                )
        );

        // INTERVIEW --> OFFER/REJECTED/WITHDRAWN
        allowedTransitions.put(
                ApplicationStatus.INTERVIEW,
                EnumSet.of(
                        ApplicationStatus.OFFER,
                        ApplicationStatus.REJECTED,
                        ApplicationStatus.WITHDRAWN
                )
        );

        // OFFER --> ACCEPTED/REJECTED/WITHDRAWN
        allowedTransitions.put(
                ApplicationStatus.OFFER,
                EnumSet.of(
                        ApplicationStatus.ACCEPTED,
                        ApplicationStatus.REJECTED,
                        ApplicationStatus.WITHDRAWN
                )
        );

        // ACCEPTED --> None
        allowedTransitions.put(
                ApplicationStatus.ACCEPTED,
                EnumSet.noneOf(ApplicationStatus.class)
        );

        // REJECTED --> None
        allowedTransitions.put(
                ApplicationStatus.REJECTED,
                EnumSet.noneOf(ApplicationStatus.class)
        );

        // WITHDRAWN --> None
        allowedTransitions.put(
                ApplicationStatus.WITHDRAWN,
                EnumSet.noneOf(ApplicationStatus.class)
        );
    }

    public void validate(ApplicationStatus currentStatus, ApplicationStatus newStatus) {
        log.info("Validating job status transition from {} to {}", currentStatus, newStatus);
        if (currentStatus == newStatus) {
            throw new CustomException("Application is already in " + currentStatus + " status", HttpStatus.BAD_REQUEST);
        }

        Set<ApplicationStatus> allowed = allowedTransitions.get(currentStatus);

        if (allowed == null || !allowed.contains(newStatus)) {
            throw new CustomException("Invalid status transition: " + currentStatus + " --> " + newStatus, HttpStatus.BAD_REQUEST);
        }
    }
}