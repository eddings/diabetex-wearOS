package com.muhendis.diabetwatch.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

public class AppJobCreator implements com.evernote.android.job.JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case FcmJobSync.TAG:
                return new FcmJobSync();
            default:
                return null;
        }
    }
}
