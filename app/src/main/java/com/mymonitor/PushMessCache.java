package com.mymonitor;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

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

        public MessageData() {
            date = new Date();
            message = "";
            timeText = "";
            title = "";
            packageName = "";
            isChina = false;
            docID = "";
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
        int id_i = PackName.checkID(data.packageName, label_Id);

        switch (id_i) {
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
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("title", data.title);
        hashMap.put("time", data.timeText);
        hashMap.put("message", data.message);
        hashMap.put("packageName", data.packageName);
        //发送到你指定的地方
        if (!TextUtils.isEmpty(PreferenceManager.getInstance().getSettingUrlNotification())){
            OkHttpUtils
                    .get()
                    .url(PreferenceManager.getInstance().getSettingUrlNotification())
                    .params(hashMap)
                    .build()
                    .execute(new Callback() {
                @Override
                public Object parseNetworkResponse(final Response response) throws Exception {
                    fragmentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(fragmentActivity, "发送的服务器成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return null;
                }

                @Override
                public void onError(Call call, Exception e) {

                }

                @Override
                public void onResponse(Object response) {

                }
            });
        }
        return false;
    }


}
