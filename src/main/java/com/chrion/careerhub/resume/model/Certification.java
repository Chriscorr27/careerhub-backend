package com.chrion.careerhub.resume.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification {

    private String name;

    private String issuer;

    private String issueDate;

    private String credentialId;

    private String credentialUrl;
}
