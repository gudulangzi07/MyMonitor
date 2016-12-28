package com.mymonitor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private LinearLayout rootLayout;
    private Button accesscBt;
    private Button accesscStartNo;
    private Button ClearViews;
    private PushMessCache pushIns;
    private Intent upservice;
    private static MainActivity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = this;
        pushIns = new PushMessCache();
        PreferenceManager.init(this);

        setContentView(R.layout.activity_main);

        upservice = new Intent(this, NeNotificationService.class);

        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        accesscBt = (Button) findViewById(R.id.buttonAssesc);
        accesscBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 0);
            }
        });
        accesscStartNo = (Button) findViewById(R.id.buttonStartNofi);
        accesscStartNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateServiceStatus(true);
            }
        });

        ClearViews = (Button) findViewById(R.id.buttonClearView);
        ClearViews.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rootLayout.removeAllViews();
                NotificationManager nm = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
            }
        });
        updateServiceStatus(true);
    }

    private void updateServiceStatus(boolean start) {
        boolean bRunning = util.isServiceRunning(this, "com.mymonitor.NeNotificationService");

        if (start && !bRunning) {
            this.startService(upservice);
        } else if (!start && bRunning) {
            this.stopService(upservice);
        }
        bRunning = util.isServiceRunning(this, "com.mymonitor.NeNotificationService");

        if (bRunning)
            Toast.makeText(this, "服务已经开启", Toast.LENGTH_SHORT).show();

        AppLog.i("updateServiceStatus ctrl[ " + start + "] result running:" + bRunning);

    }

    @SuppressLint("NewApi")
    private void addToUi(String packName, Notification notification) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final PushMessCache.MessageData data = pushIns.new MessageData();

            data.packageName = packName;
            data.isChina = PackName.isChina(packName);
            data.title = notification.tickerText.toString().split(":")[0];
            data.message = notification.tickerText.toString().split(":")[1];
            data.timeText = simpleDateFormat.format(new Date(notification.when));
            data.date = new Date(notification.when);
            Toast.makeText(this, "发送信息到服务器", Toast.LENGTH_SHORT).show();
            pushIns.sendMess(this, data);
        } catch (Exception e) {
            Toast.makeText(this, "发送失败啦.....", Toast.LENGTH_SHORT).show();
            AppLog.e("addToUi excep", e);
        }
    }

    @SuppressLint("NewApi")
    public static void notifyReceive(String packageName, Notification notification) {
        PendingIntent nit = notification.contentIntent;

        AppLog.i("onReceive packageName: " + packageName);

        if (notification != null) {
            if (activity != null)
                activity.addToUi(packageName, notification);
            else
                AppLog.e("MainActivity is null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 0);
                break;
            case R.id.action_url:
                DialogUtils.dialogCenterEditText(this, new DialogUtils.OnDialogCallback() {
                    @Override
                    public void dialogCallback(String dialogStr) {
                        PreferenceManager.getInstance().setSettingUrlNotification(dialogStr);
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateServiceStatus(false);
    }

}
