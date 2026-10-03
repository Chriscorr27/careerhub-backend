package com.chrion.careerhub.notification.controller;

import com.chrion.careerhub.notification.dto.UpdatePreferencesRequest;
import com.chrion.careerhub.notification.model.NotificationPreference;
import com.chrion.careerhub.notification.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public NotificationPreference getPreferences() {

        return preferenceService.getOrCreate();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public NotificationPreference updatePreferences(@RequestBody UpdatePreferencesRequest request) {

        return preferenceService.update(
                request.inAppEnabled(),
                request.emailEnabled()
        );
    }
}
