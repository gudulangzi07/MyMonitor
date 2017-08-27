package com.mymonitor;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: SoftApplication
 * @author: 张京伟
 * @date: 2016/12/27 11:19
 * @Description:
 * @version: 1.0
 */
public class SoftApplication extends MultiDexApplication {
    public static List<Activity> unDestroyActivityList = new ArrayList<>();
    public static SoftApplication softApplication;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        softApplication = this;
    }



    /**
     * 退出应用
     */
    public void quit() {
        for (Activity activity : unDestroyActivityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        unDestroyActivityList.clear();
    }
}
