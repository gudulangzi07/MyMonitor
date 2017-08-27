package com.mymonitor.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mymonitor.R;
import com.mymonitor.adapter.MyAdapter;
import com.mymonitor.bean.NotificationBean;
import com.mymonitor.bean.PackName;
import com.mymonitor.request.PushMessCache;
import com.mymonitor.service.NeNotificationService;
import com.mymonitor.utils.AppLog;
import com.mymonitor.utils.DialogUtils;
import com.mymonitor.utils.PreferenceManager;
import com.mymonitor.utils.util;
import com.mymonitor.widget.XRecyclerView.ProgressStyle;
import com.mymonitor.widget.XRecyclerView.XRecyclerView;
import com.mymonitor.widget.XRecyclerView.decoration.DividerItemDecoration;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.vilyever.socketclient.util.CharsetUtil;

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
    private SocketClient socketClient;

    public SocketClient getSocketClient() {
        return socketClient;
    }

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

        xRecyclerView.setAdapter(myAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (socketClient == null || !socketClient.isConnecting()){
            socketClient = new SocketClient();
            socketClient.setCharsetName(CharsetUtil.UTF_8);

            socketClient.getHeartBeatHelper().setDefaultSendData(CharsetUtil.stringToData("HeartBeat", CharsetUtil.UTF_8));
            socketClient.getHeartBeatHelper().setDefaultReceiveData(CharsetUtil.stringToData("HeartBeat", CharsetUtil.UTF_8));
            socketClient.getHeartBeatHelper().setHeartBeatInterval(60 * 1000);// 设置自动发送心跳包的间隔时长，单位毫秒
            socketClient.getHeartBeatHelper().setSendHeartBeatEnabled(true);// 设置允许自动发送心跳包，此值默认为 false

            //实现自动重连
            socketClient.connect();

//            SocketHeartBeatHelper.SendDataBuilder sendDataBuilder = new SocketHeartBeatHelper.SendDataBuilder() {
//                @Override
//                public byte[] obtainSendHeartBeatData(SocketHeartBeatHelper helper) {
//                    /**
//                     * 使用当前日期作为心跳包
//                     */
//                    byte[] heartBeatPrefix = new byte[]{0x1F, 0x1F};
//                    byte[] heartBeatSuffix = new byte[]{0x1F, 0x1F};
//
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                    byte[] heartBeatInfo = CharsetUtil.stringToData(sdf.format(new Date()), CharsetUtil.UTF_8);
//
//                    byte[] data = new byte[heartBeatPrefix.length + heartBeatSuffix.length + heartBeatInfo.length];
//                    System.arraycopy(heartBeatPrefix, 0, data, 0, heartBeatPrefix.length);
//                    System.arraycopy(heartBeatInfo, 0, data, heartBeatPrefix.length, heartBeatInfo.length);
//                    System.arraycopy(heartBeatSuffix, 0, data, heartBeatPrefix.length + heartBeatInfo.length, heartBeatSuffix.length);
//
//                    return data;
//                }
//            };
//            socketClient.getHeartBeatHelper().setSendDataBuilder(sendDataBuilder);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (socketClient.isConnecting())
            socketClient.disconnect();
    }

    private void updateServiceStatus(boolean start) {
        boolean bRunning = util.isServiceRunning(this, "com.mymonitor.service.NeNotificationService");

        if (start && !bRunning) {
            this.startService(upservice);
        } else if (!start && bRunning) {
            this.stopService(upservice);
        }
        bRunning = util.isServiceRunning(this, "com.mymonitor.service.NeNotificationService");

        if (bRunning)
            Toast.makeText(this, R.string.toast_service_status, Toast.LENGTH_SHORT).show();

        AppLog.i("updateServiceStatus ctrl[ " + start + "] result running:" + bRunning);

    }

    @SuppressLint("NewApi")
    private void addToUi(String packName, Notification notification) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final PushMessCache.MessageData data = pushIns.new MessageData();

            if (notification.contentView != null) {
                LinearLayout linearLayout = new LinearLayout(this);
                View view = notification.contentView.apply(this, linearLayout);

                EnumGroupViews(view, data);
            } else {
                data.title = notification.tickerText.toString().split(":")[0];
                data.message = notification.tickerText.toString().split(":")[1];
                data.timeText = simpleDateFormat.format(new Date(notification.when));
            }

            data.packageName = packName;
            data.isChina = PackName.isChina(packName);
            data.date = new Date(notification.when);

            NotificationBean notificationBean = new NotificationBean();
            notificationBean.title = data.title;
            notificationBean.message = data.message;
            notificationBean.timeText = data.timeText;

//            notificationBean.drawable = notification.getLargeIcon().loadDrawable(this);
            if (notificationBeans.size() == 30) {
                notificationBeans.remove(29);
                notificationBeans.add(0, notificationBean);
            } else {
                notificationBeans.add(0, notificationBean);
            }

            myAdapter.setNotificationBeans(notificationBeans);
            myAdapter.notifyDataSetChanged();

            pushIns.setSendMessageListener(message -> socketClient.registerSocketClientDelegate(new SocketClientDelegate() {
                @Override
                public void onConnected(SocketClient client) {
                    // 发送String消息，使用默认编码
                    socketClient.sendString(message);
                }

                @Override
                public void onDisconnected(SocketClient client) {
                    socketClient.connect();
                }

                @Override
                public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {
                    if (client.isDisconnecting())
                        client.connect();
                }
            }));
            pushIns.sendMess(this, data);

        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(activity, R.string.toast_service_fail, Toast.LENGTH_SHORT).show());
            AppLog.e("addToUi excep", e);
        }
    }

    private void EnumGroupViews(View v1, PushMessCache.MessageData data) {
        if (v1 instanceof ViewGroup) {
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

    @SuppressLint("NewApi")
    public static void notifyReceive(String packageName, Notification notification) {
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 0);
                break;
            case R.id.action_url:
                PreferenceManager.getInstance().setSettingMethod("0");
                DialogUtils.dialogCenterEditText(this, dialogStr -> PreferenceManager.getInstance().setSettingUrlNotification(dialogStr));
                break;
            case R.id.action_socket:
                PreferenceManager.getInstance().setSettingMethod("1");
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
