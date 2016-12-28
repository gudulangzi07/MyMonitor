package com.mymonitor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LinearLayout rootLayout;
    private Button accesscBt;
    private Button accesscStartNo;
    private Button ClearViews;
    private PushMessCache pushIns;
    private Intent upservice;
    private static MainActivity activity = null;
    private TextView textView1;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = this;
        pushIns = new PushMessCache();

        setContentView(R.layout.activity_main);

        upservice = new Intent(this, NeNotificationService.class);

        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        textView1 = (TextView) findViewById(R.id.textView1);
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
                registerBroadcast();
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

    public void setTextView1(String str) {
        textView1.setText(str);
    }

    private void updateServiceStatus(boolean start) {
        boolean bRunning = util.isServiceRunning(this, "com.mymonitor.NeNotificationService");

        if (start && !bRunning) {
            this.startService(upservice);
        } else if (!start && bRunning) {
            this.stopService(upservice);
        }
        bRunning = util.isServiceRunning(this, "com.mymonitor.NeNotificationService");

        AppLog.i("updateServiceStatus ctrl[ " + start + "] result running:" + bRunning);

    }

    private NotifyDataReceiver receiver = null;

    private void registerBroadcast() {
        receiver = new NotifyDataReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(".NeNotificationService");
        Intent it = this.registerReceiver(receiver, filter);

        AppLog.i("Broadcast registered.........:" + it);
    }

    private void EnumGroupViews(View v1, PushMessCache.MessageData data) {
        if (v1 instanceof ViewGroup) {
            //Log.i(TAG, "FrameLayout in");
            ViewGroup lav = (ViewGroup) v1;
            int lcCnt = lav.getChildCount();
            for (int i = 0; i < lcCnt; i++) {
                View c1 = lav.getChildAt(i);
                if (c1 instanceof ViewGroup)
                    EnumGroupViews(c1, data);
                else if (c1 instanceof TextView) {
                    TextView txt = (TextView) c1;
                    String str = txt.getText().toString().trim();
                    if (str.length() > 0) {
                        pushIns.addMess(txt.getId(), data, str);
                    }

                    AppLog.i("TextView id:" + txt.getId() + ".text:" + str);
                } else {
                    AppLog.w("2 other layout:" + c1.toString());
                }
            }
        } else {
            AppLog.w("1 other layout:" + v1.toString());
        }
    }

    private void addToUi(RemoteViews remoteView, String packName) {
//        rootLayout.addView(remoteView);
        try {
            View v1 = remoteView.apply(this, rootLayout);
            //AppLog.i("remoteview:" + v1.toString());
            final PushMessCache.MessageData data = pushIns.new MessageData();

            data.packageName = packName;
            EnumGroupViews(v1, data);
            if (rootLayout.getChildCount() > 100) {
                AppLog.i("remove 50 views in child!");
                rootLayout.removeViews(0, 50);
            }

            rootLayout.addView(v1);
            data.isChina = PackName.isChina(packName);
            pushIns.sendMess(url, this, data);
        } catch (Exception e) {
            AppLog.e("addToUi excep", e);
        }
    }

    private void addToUi(String packName, Notification notification) {
        try {
            final PushMessCache.MessageData data = pushIns.new MessageData();

            data.packageName = packName;
            data.isChina = PackName.isChina(packName);
            data.title = notification.tickerText.toString().split(":")[0];
            data.message = notification.tickerText.toString().split(":")[1];
            Toast.makeText(this, "发送信息到服务器", Toast.LENGTH_SHORT).show();
            pushIns.sendMess(url, this, data);
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
//            RemoteViews remoteV = notification.contentView;
//            if (remoteV == null) {
//                AppLog.e("remoteView is: null");
//                //自己创建一个RemoteViews
//                RemoteViews remoteViews = new RemoteViews(packageName, R.layout.remote_view);
//                String[] str = notification.tickerText.toString().split(":");
//                remoteViews.setImageViewIcon(R.id.iv_view, notification.getSmallIcon());
//                remoteViews.setTextViewText(R.id.tv_nickName, str[0]);
//                remoteViews.setTextViewText(R.id.tv_content, str[1]);
//                if (activity != null)
//                    activity.addToUi(remoteViews, packageName);
//                else
//                    AppLog.e("MainActivity is null");
//            } else {
//                if (activity != null)
//                    activity.addToUi(remoteV, packageName);
//                else
//                    AppLog.e("MainActivity is null");
//            }
        }
    }

    public class NotifyDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //AppLog.i("Receiver got msg in onReceive()...");

            Parcelable notifyParcelable = intent.getParcelableExtra("NotifyData");
            String packageName = intent.getStringExtra("packageName");
            AppLog.i("onReceive packageName: " + packageName);
            if (notifyParcelable != null) {

                Notification notification = (Notification) notifyParcelable;
                //Log.i("tickerText: " + notification.tickerText);

                RemoteViews remoteV = notification.contentView;
                PendingIntent nit = notification.contentIntent;

                if (remoteV == null) {
                    AppLog.e("remoteView is: null");
                } else {
                    //showNotify("remoteView is: not null" );

                    addToUi(remoteV, packageName);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("url".equals(item.getTitle().toString().toLowerCase())) {
            DialogUtils.dialogCenterEditText(this, new DialogUtils.OnDialogCallback() {
                @Override
                public void dialogCallback(String dialogStr) {
                    url = dialogStr;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
//        if(receiver != null)
//        	this.unregisterReceiver(receiver);
        super.onDestroy();
        updateServiceStatus(false);
    }

}
