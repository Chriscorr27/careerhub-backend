package com.chrion.careerhub.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record JobApplicationTrendResponse(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        long count
) { }
