package com.chrion.careerhub.constant;

public final class KafkaTopic {
    private KafkaTopic() {}

    public static final String JOB_STATUS_CHANGED = "careerhub.job-status-changed";
    public static final String JOB_STATUS_CHANGED_DLT = "careerhub.job-status-changed.DLT";
    public static final String JOB_CREATED = "careerhub.job-created";
    public static final String JOB_CREATED_DLT = "careerhub.job-created.DLT";
    public static final String JOB_UPDATED = "careerhub.job-updated";
    public static final String JOB_UPDATED_DLT = "careerhub.job-updated.DLT";
    public static final String JOB_DELETED = "careerhub.job-deleted";
    public static final String JOB_DELETED_DLT = "careerhub.job-deleted.DLT";
    public static final String EMAIL_NOTIFICATION = "careerhub.email-notification";
    public static final String APPLICATION_STATUS_CHANGED = "careerhub.application-status-changed";
    public static final String APPLICATION_STATUS_CHANGED_DLT = "careerhub.application-status-changed.DLT";
}
