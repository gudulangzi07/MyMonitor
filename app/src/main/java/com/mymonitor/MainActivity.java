package com.mymonitor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mymonitor.XRecyclerView.ProgressStyle;
import com.mymonitor.XRecyclerView.XRecyclerView;
import com.mymonitor.XRecyclerView.decoration.DividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: MainActivity
 * @author: 张京伟
 * @date: 2016/12/29 14:22
 * @Description:
 * @version: 1.0
 */
public class MainActivity extends AppCompatActivity {
    private Button accesscBt;
    private Button accesscStartNo;
    private Button ClearViews;
    private XRecyclerView xRecyclerView;
    private MyAdapter myAdapter;
    private PushMessCache pushIns;
    private Intent upservice;
    private static MainActivity activity = null;
    private List<NotificationBean> notificationBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = this;
        pushIns = new PushMessCache();
        PreferenceManager.init(this);
        myAdapter = new MyAdapter();
        notificationBeans = new ArrayList<>();

        setContentView(R.layout.activity_main);

        upservice = new Intent(this, NeNotificationService.class);

        xRecyclerView = (XRecyclerView) findViewById(R.id.xRecyclerView);
        accesscBt = (Button) findViewById(R.id.buttonAssesc);
        accesscBt.setOnClickListener(view -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, 0);
        });
        accesscStartNo = (Button) findViewById(R.id.buttonStartNofi);
        accesscStartNo.setOnClickListener(view -> updateServiceStatus(true));

        ClearViews = (Button) findViewById(R.id.buttonClearView);
        ClearViews.setOnClickListener(view -> {
            notificationBeans.clear();
            myAdapter.setNotificationBeans(notificationBeans);
            myAdapter.notifyDataSetChanged();
            NotificationManager nm = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
        });
        updateServiceStatus(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLayoutManager(linearLayoutManager);

        xRecyclerView.setPullRefreshEnabled(false);
        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        //目前有问题
        xRecyclerView.setAdapter(myAdapter);
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
            Toast.makeText(this, R.string.toast_service_status, Toast.LENGTH_SHORT).show();

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

            NotificationBean notificationBean = new NotificationBean();
            notificationBean.title = notification.tickerText.toString().split(":")[0];
            notificationBean.message = notification.tickerText.toString().split(":")[1];
            notificationBean.timeText = simpleDateFormat.format(new Date(notification.when));

//            notificationBean.drawable = notification.getLargeIcon().loadDrawable(this);
            if (notificationBeans.size() == 30){
                notificationBeans.remove(29);
                notificationBeans.add(0, notificationBean);
            }else {
                notificationBeans.add(0, notificationBean);
            }

            myAdapter.setNotificationBeans(notificationBeans);
            myAdapter.notifyDataSetChanged();

            pushIns.sendMess(this, data);
        } catch (Exception e) {
            Toast.makeText(this, R.string.toast_service_fail, Toast.LENGTH_SHORT).show();
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
                DialogUtils.dialogCenterEditText(this, dialogStr -> PreferenceManager.getInstance().setSettingUrlNotification(dialogStr));
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
