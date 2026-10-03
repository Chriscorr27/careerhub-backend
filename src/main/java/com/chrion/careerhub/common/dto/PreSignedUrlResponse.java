package com.chrion.careerhub.common.dto;

import java.time.Duration;

public record PreSignedUrlResponse(
        String url,
        Duration expiration
) { }
