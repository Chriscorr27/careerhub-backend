package com.chrion.careerhub.resume.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    private String name;

    private String description;

    private String url;

    private String githubUrl;

    @Builder.Default
    private List<String> technologies = new ArrayList<>();

    @Builder.Default
    private List<String> highlights = new ArrayList<>();
}