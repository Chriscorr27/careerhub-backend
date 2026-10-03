package com.chrion.careerhub.resume.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience {

    private String company;

    private String role;

    private String location;

    private String startDate;

    private String endDate;

    @Builder.Default
    private Boolean current = false;

    @Builder.Default
    private List<String> responsibilities = new ArrayList<>();

    @Builder.Default
    private List<String> technologies = new ArrayList<>();
}
