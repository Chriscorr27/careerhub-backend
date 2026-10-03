package com.chrion.careerhub.event.model;

import java.util.UUID;

public record EmailNotificationEvent (
    String userId,
    String email,
    String subject,
    String body,
    int eventVersion
) {}
