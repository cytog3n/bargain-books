package com.cyto.bargainbooks;

import android.app.Application;

public class BBooksApplication extends Application {

    private static BBooksApplication instance;

    public BBooksApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
