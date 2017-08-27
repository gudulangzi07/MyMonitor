package com.mymonitor.request;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.mymonitor.activity.MainActivity;
import com.mymonitor.utils.AppLog;
import com.mymonitor.utils.PreferenceManager;
import com.mymonitor.utils.VerifyCheck;
import com.vilyever.socketclient.helper.SocketClientAddress;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Response;

public class PushMessCache {

    public final static int UNKNOWN_INDEX = 0;
    public final static int TITLE_INDEX = 1;
    public final static int TIME_INDEX = 2;
    public final static int TEXT_INDEX = 3;

    public class MessageData {
        public String packageName;
        public String timeText;
        public String title;
        public String message;
        public Date date;
        public boolean isChina;
        public String docID;
        public int index;

        public MessageData() {
            date = new Date();
            message = "";
            timeText = "";
            title = "";
            packageName = "";
            isChina = false;
            docID = "";
            index = 1;
        }

        public void print() {
            StringBuffer str = new StringBuffer(packageName);
            if (title.length() > 0) {
                str.append(".title:");
                str.append(title);
            }
            if (timeText.length() > 0) {
                str.append(".timeText:");
                str.append(timeText);
            }
            str.append(".message:");
            str.append(message);

            str.append(".Date:");
            str.append(date.toLocaleString());

            AppLog.i(str.toString());


        }
    }

    public Vector<MessageData> messVec = new Vector<MessageData>();

    public void addMess(int label_Id, MessageData data, String txt) {
        switch (data.index++) {
            case UNKNOWN_INDEX:

                break;
            case TITLE_INDEX:
                data.title = txt;
                break;
            case TIME_INDEX:
                data.timeText = txt;
                break;
            case TEXT_INDEX:
                data.message = txt;
                break;
            default:
                break;
        }
//        int id_i = PackName.checkID(data.packageName, label_Id);
//
//        switch (id_i) {
//            case UNKNOWN_INDEX:
//
//                break;
//            case TITLE_INDEX:
//                data.title = txt;
//                break;
//            case TIME_INDEX:
//                data.timeText = txt;
//                break;
//            case TEXT_INDEX:
//                data.message = txt;
//                break;
//            default:
//                break;
//        }
    }

    private boolean news_exist(MessageData data) {
        boolean bIn = false;
        for (MessageData item : messVec) {
            if (item.packageName.equals(data.packageName)) {
                if (item.message.equals(data.message)) {
                    bIn = true;
                    break;
                }
            }
        }
        if (!bIn) {
            if (messVec.size() >= 100) {
                AppLog.w("messVec size 100 clear.");
                messVec.clear();
            }
            messVec.add(data);
        }
        return bIn;
    }

    public boolean sendMess(final FragmentActivity fragmentActivity, MessageData data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", data.title);
        jsonObject.put("time", data.timeText);
        jsonObject.put("message", data.message);
        jsonObject.put("packageName", data.packageName);

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("info", jsonObject.toJSONString());
        //发送到你指定的地方
        if (!TextUtils.isEmpty(PreferenceManager.getInstance().getSettingUrlNotification())) {
            if ("0".equals(PreferenceManager.getInstance().getSettingMethod())) {
                OkHttpUtils
                        .get()
                        .url(PreferenceManager.getInstance().getSettingUrlNotification())
                        .params(hashMap)
                        .build()
                        .execute(new Callback() {
                            @Override
                            public Object parseNetworkResponse(final Response response) throws Exception {
                                return true;
                            }

                            @Override
                            public void onError(Call call, Exception e) {

                            }

                            @Override
                            public void onResponse(Object response) {

                            }
                        });
            } else if ("1".equals(PreferenceManager.getInstance().getSettingMethod())) {
                String[] strings = PreferenceManager.getInstance().getSettingUrlNotification().split(":");
                if (VerifyCheck.isIPVerify(strings[0])) {

                    SocketClientAddress socketClientAddress = ((MainActivity)fragmentActivity).getSocketClient().getAddress();
                    socketClientAddress.setRemoteIP(strings[0]);
                    socketClientAddress.setRemotePort(strings[1]);
                    socketClientAddress.setConnectionTimeout(15 * 1000);

                    if (((MainActivity)fragmentActivity).getSocketClient().isDisconnecting()){
                        ((MainActivity)fragmentActivity).getSocketClient().connect();
                    }

                    if (sendMessageListener != null)
                        sendMessageListener.sendMessage( hashMap.toString());

                } else {
                    fragmentActivity.runOnUiThread(() -> Toast.makeText(fragmentActivity, "IP不正常", Toast.LENGTH_SHORT).show());
                    return false;
                }

            }
        }

        return false;
    }

    private SendMessageListener sendMessageListener;

    public interface SendMessageListener{
        void sendMessage(String message);
    }

    public void setSendMessageListener(SendMessageListener sendMessageListener) {
        this.sendMessageListener = sendMessageListener;
    }

}
