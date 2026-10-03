package com.chrion.careerhub.resume.controller;

import com.chrion.careerhub.resume.dto.CreateResumeRequest;
import com.chrion.careerhub.resume.dto.ResumeResponse;
import com.chrion.careerhub.resume.dto.UpdateResumeRequest;
import com.chrion.careerhub.resume.service.ResumeService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResumeResponse createResume(@Valid @RequestBody CreateResumeRequest request) {

        return resumeService.createResume(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResumeResponse> getMyResumes() {
        return resumeService.getMyResumes();
    }

    @GetMapping("/{resumeId}")
    @ResponseStatus(HttpStatus.OK)
    public ResumeResponse getResume(@PathVariable String resumeId) {

        return resumeService.getResume(resumeId);
    }

    @PutMapping("/{resumeId}")
    @ResponseStatus(HttpStatus.OK)
    public ResumeResponse updateResume(@PathVariable String resumeId, @Valid @RequestBody UpdateResumeRequest request) {
        return resumeService.updateResume(resumeId, request);
    }

    @DeleteMapping("/{resumeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResume(@PathVariable String resumeId) {

        resumeService.deleteResume(resumeId);
    }
}
