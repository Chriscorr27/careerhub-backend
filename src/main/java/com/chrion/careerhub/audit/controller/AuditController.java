package com.chrion.careerhub.audit.controller;

import com.chrion.careerhub.audit.service.AuditService;
import com.chrion.careerhub.common.dto.PageResponse;
import com.chrion.careerhub.common.validator.QueryValidator;
import com.chrion.careerhub.job.dto.AuditEventResponse;
import com.chrion.careerhub.job.dto.JobStatusHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditService auditService;
    private final QueryValidator queryValidator;

    @GetMapping("/job-status-history")
    public PageResponse<JobStatusHistoryResponse> getJobStatusHistory(
        @RequestParam(defaultValue = "0")
        int page,

        @RequestParam(defaultValue = "10")
        int size,

        @RequestParam(defaultValue = "createdAt")
        String sortBy,

        @RequestParam(defaultValue = "desc")
        String direction
    ) {
        queryValidator.validatePagination(page, size);
        queryValidator.validateDirection(direction);

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)? Sort.Direction.DESC:Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );
        return auditService.getJobStatusHistory(pageable);
    }

    @GetMapping("/{jobId}/job-status-history")
    public List<JobStatusHistoryResponse> getStatusHistory(@PathVariable UUID jobId) {
        return auditService.getJobStatusHistory(jobId);
    }

    @GetMapping("/{entityId}/audit-event")
    public PageResponse<AuditEventResponse> getEntityAuditEvent(
            @PathVariable
            String entityId,
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String direction
    ) {
        queryValidator.validatePagination(page, size);
        queryValidator.validateDirection(direction);

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)? Sort.Direction.DESC:Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );
        return auditService.getEntityAuditEvent(entityId,pageable);
    }
}
