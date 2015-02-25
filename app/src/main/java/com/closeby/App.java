package com.closeby;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.PushService;

/**
 * Created by Sam on 6/28/2014.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "aCcHeP9uAmy6jYHI9t1wTTmasCfVPEA04WZO5ZZk", "CH1j2AftoEjePigm5a1Jb10uJ6Nx302qFD1XrG6r");
        ParseFacebookUtils.initialize("538133626309084");

        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseTwitterUtils.initialize("W9f08xVvdAFd6ieCF7sWBTWj9", "wGIY6YfGgsRlWHRxURr0PPWCWHGw074AGIesebIprem6yCUmfp");
    }
}
