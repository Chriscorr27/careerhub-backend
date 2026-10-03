package com.chrion.careerhub.notification.dto;

import org.springframework.web.bind.annotation.RequestParam;

public record UpdatePreferencesRequest(
        boolean inAppEnabled,
        boolean emailEnabled
) { }
