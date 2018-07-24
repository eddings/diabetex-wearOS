package com.muhendis.diabetwatch;

import android.app.Application;

import com.evernote.android.job.JobManager;
import com.muhendis.diabetwatch.jobs.AppJobCreator;
import com.muhendis.diabetwatch.jobs.FcmJobSync;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new AppJobCreator());
        FcmJobSync.schedulePeriodicJob();
    }
}
