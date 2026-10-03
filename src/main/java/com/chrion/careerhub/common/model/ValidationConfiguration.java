package com.chrion.careerhub.common.model;

import java.util.Set;

public record ValidationConfiguration(
        long MAX_FILE_SIZE,
        Set<String> ALLOWED_CONTENT_TYPES,
        Set<String> ALLOWED_EXTENSIONS,
        String FILE_SIZE_ERROR
){}
