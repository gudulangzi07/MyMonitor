package com.mymonitor;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName: PreferenceManager
 * @author: 张京伟
 * @date: 2016/12/28 22:38
 * @Description: 弹出对话框工具类
 * @version: 1.0
 */
public class PreferenceManager {
    public static final String PREFERENCE_NAME = "saveInfo";
    private volatile static PreferenceManager mPreferencemManager;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor editor;
    private static String SHARED_KEY_SETTING_NOTIFICATION = "shared_key_setting_notification";

    public static synchronized PreferenceManager getInstance(){
        if (mPreferencemManager == null) {
            throw new RuntimeException("please init first!");
        }

        return mPreferencemManager;
    }

    public PreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt){
        if(mPreferencemManager == null){
            mPreferencemManager = new PreferenceManager(cxt);
        }
    }

    public void setSettingUrlNotification(String url) {
        editor.putString(SHARED_KEY_SETTING_NOTIFICATION, url);
        editor.apply();
    }

    public String getSettingUrlNotification(){
        return mSharedPreferences.getString(SHARED_KEY_SETTING_NOTIFICATION, null);
    }
}
