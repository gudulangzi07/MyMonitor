package com.mymonitor.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: VerifyCheck
 * @author: 张京伟
 * @date: 2017/1/5 20:03
 * @Description:
 * @version: 1.0
 */
public class VerifyCheck {
    /**
     * 验证是否是一个url
     * @param urlString
     * @return
     */
    public static boolean isIPVerify(String urlString) {
        if (urlString == null || "".equals(urlString.trim())) {
            return false;
        } else {
            String urlTrim = urlString.trim();
            Pattern patternURLname = Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");// 是否是URL
            Matcher matcherURLname = patternURLname.matcher(urlTrim);
            return matcherURLname.matches();
        }
    }

    /**
     * 验证是否是一个url
     * @param host
     * @param port
     * @return
     */
    public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
