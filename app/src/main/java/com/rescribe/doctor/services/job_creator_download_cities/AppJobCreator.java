package com.rescribe.doctor.services.job_creator_download_cities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class AppJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case CitySyncJob.TAG:
                return new CitySyncJob();
            default:
                return null;
        }
    }
}
