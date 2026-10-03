package com.chrion.careerhub.resume.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    private String name;

    private String category;

    private String proficiency;
}
