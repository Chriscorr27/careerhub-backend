package com.chrion.careerhub.resume.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    private String institution;

    private String degree;

    private String fieldOfStudy;

    private String startDate;

    private String endDate;

    private String grade;
}