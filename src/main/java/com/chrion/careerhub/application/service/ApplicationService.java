package com.chrion.careerhub.application.service;

import com.chrion.careerhub.application.dto.ApplicationResponse;
import com.chrion.careerhub.application.dto.ApplicationSearchRequest;
import com.chrion.careerhub.application.dto.CreateApplicationRequest;
import com.chrion.careerhub.application.model.Application;
import com.chrion.careerhub.application.model.ApplicationStatus;
import com.chrion.careerhub.application.repository.ApplicationRepository;
import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.common.dto.PageResponse;
import com.chrion.careerhub.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {
    private final AuthService authService;
    private final ApplicationDBService applicationDBService;
    private final ApplicationRepository applicationRepository;


    public ApplicationResponse createApplication(CreateApplicationRequest request) {
        log.info("Creating Application");
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return applicationDBService.createApplication(userId, request);
    }

    public PageResponse<ApplicationResponse> searchApplications(ApplicationSearchRequest request, Pageable pageable){
        log.info("Searching Application");
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return applicationDBService.searchApplications(userId, request, pageable);
    }

    public Application getApplication(UUID applicationId){
        log.info("Getting Application");
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return applicationDBService.getApplication(userId, applicationId);
    }

    public ApplicationResponse getApplicationResponse(UUID applicationId){
        log.info("Getting Application Response");
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return applicationDBService.getApplicationResponse(userId, applicationId);
    }


    public ApplicationResponse changeApplicationStatusByUser(UUID applicationId, ApplicationStatus newStatus){
        log.info("Changing Application status to {}", newStatus);
        Application application = getApplication(applicationId);
        UUID userId = authService.getCurrentAuthenticatedUserId();
        if(newStatus!=ApplicationStatus.APPLIED && newStatus!=ApplicationStatus.WITHDRAWN){
            throw  new CustomException("Invalid Status", HttpStatus.BAD_REQUEST);
        }

        return applicationDBService.changeApplicationStatusByUserId(userId, application, newStatus);
    }

    public ApplicationResponse changeApplicationStatusByHR(UUID applicationId, ApplicationStatus newStatus){
        log.info("Changing Application status by HR to {}", newStatus);
        Application application = getApplication(applicationId);
        UUID userId = authService.getCurrentAuthenticatedUserId();
        if(newStatus==ApplicationStatus.APPLIED || newStatus==ApplicationStatus.WITHDRAWN){
            throw  new CustomException("Invalid Status", HttpStatus.BAD_REQUEST);
        }

        return applicationDBService.changeApplicationStatusByUserId(userId, application, newStatus);
    }
}
