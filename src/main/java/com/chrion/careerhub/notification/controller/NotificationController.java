package com.chrion.careerhub.notification.controller;

import com.chrion.careerhub.notification.model.Notification;
import com.chrion.careerhub.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Notification> getNotifications() {


        return notificationService.getNotifications();
    }

    @GetMapping("/unread")
    @ResponseStatus(HttpStatus.OK)
    public List<Notification> getUnreadNotifications() {

        return notificationService.getUnreadNotifications();
    }

    @GetMapping("/unread/count")
    @ResponseStatus(HttpStatus.OK)
    public Long getUnreadCount() {

        return notificationService.getUnreadCount();
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(@PathVariable UUID id) {

        notificationService.markAsRead(id);

    }

    @PatchMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllAsRead() {

        notificationService.markAllAsRead();
    }
}