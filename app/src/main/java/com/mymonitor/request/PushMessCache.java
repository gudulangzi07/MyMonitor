package com.mymonitor.request;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.mymonitor.utils.AppLog;
import com.mymonitor.utils.PreferenceManager;
import com.mymonitor.utils.VerifyCheck;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
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
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("title", data.title);
        hashMap.put("time", data.timeText);
        hashMap.put("message", data.message);
        hashMap.put("packageName", data.packageName);
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
                                fragmentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                            Toast.makeText(fragmentActivity, "发送的服务器成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Socket socket = null;
                            try {
                                socket = new Socket(strings[0], Integer.parseInt(strings[1]));
                            } catch (IOException e) {
                                fragmentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(fragmentActivity, "网络端口连接异常", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            }
                            try {
                                //2、获取输出流，向服务器端发送信息
                                OutputStream os = socket.getOutputStream();//字节输出流
                                PrintWriter pw = new PrintWriter(os);//将输出流包装成打印流
                                pw.write(hashMap.toString());
                                pw.flush();
                                socket.shutdownOutput();
                                //3、获取输入流，并读取服务器端的响应信息
                                InputStream is = socket.getInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                String info = null;
                                while ((info = br.readLine()) != null) {
                                    System.out.println("客户端返回信息" + info);
                                }
                                //4、关闭资源
                                br.close();
                                is.close();
                                pw.close();
                                os.close();
                                socket.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } else {
                    fragmentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(fragmentActivity, "IP不正常", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return false;
                }

            }
        }
        return false;
    }


}
