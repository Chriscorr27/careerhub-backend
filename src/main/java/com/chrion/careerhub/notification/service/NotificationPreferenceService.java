package com.chrion.careerhub.notification.service;

import com.chrion.careerhub.auth.service.AuthService;
import com.chrion.careerhub.notification.model.NotificationPreference;
import com.chrion.careerhub.notification.repository.NotificationPreferenceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository repository;

    private final AuthService authService;

    public NotificationPreference getOrCreate(){
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return getOrCreate(userId);
    }

    @Transactional
    public NotificationPreference getOrCreate(UUID userId) {
        return repository.findByUserId(userId)
                .orElseGet(() ->
                        repository.save(
                                NotificationPreference.builder()
                                        .userId(userId)
                                        .inAppEnabled(true)
                                        .emailEnabled(true)
                                        .build()
                        )
                );
    }

    public NotificationPreference update(boolean inAppEnabled, boolean emailEnabled) {
        UUID userId = authService.getCurrentAuthenticatedUserId();
        return update(inAppEnabled, emailEnabled, userId);
    }


    @Transactional
    private NotificationPreference update(boolean inAppEnabled, boolean emailEnabled, UUID userId) {
        NotificationPreference preference = getOrCreate(userId);

        preference.setInAppEnabled(inAppEnabled);
        preference.setEmailEnabled(emailEnabled);

        repository.save(preference);

        return preference;
    }
}
