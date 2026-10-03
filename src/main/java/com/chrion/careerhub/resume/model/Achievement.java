package com.chrion.careerhub.resume.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    private String title;

    private String description;

    private String date;
}