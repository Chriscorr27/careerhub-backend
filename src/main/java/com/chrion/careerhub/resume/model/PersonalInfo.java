package com.chrion.careerhub.resume.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInfo {

    private String fullName;

    private String email;

    private String phone;

    private String location;

    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;
}
