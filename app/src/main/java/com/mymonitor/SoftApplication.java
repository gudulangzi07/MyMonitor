package com.mymonitor;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import java.net.Socket;
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
    public static Socket socket;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        softApplication = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                connectServer();
            }
        }).start();
    }

    private void connectServer() {
        try{
            //客户端
            //1、创建客户端Socket，指定服务器地址和端口
            socket = new Socket("192.168.1.112", 8080);
        }catch (Exception e){
            e.printStackTrace();
        }
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
