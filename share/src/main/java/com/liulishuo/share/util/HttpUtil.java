package com.liulishuo.share.util;

import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Jack Tony
 * @date 2015/7/21
 */
public class HttpUtil {

    private static final int TIMEOUT_IN_MILLIONS = 5000;

    public interface CallBack {

        void onRequestComplete(String result);

        void onError();
    }

    /**
     * 异步的Get请求
     */
    public static void doGetAsyn(final String urlStr, final CallBack callBack) {
        new Thread() {
            public void run() {
                String result = doGet(urlStr);
                if (callBack != null) {
                    if (result != null) {
                        callBack.onRequestComplete(result);
                    } else {
                        callBack.onError();
                    }
                }
            }
        }.start();
    }


    /**
     * Get请求，获得返回数据
     */
    public static @Nullable String doGet(String urlStr) {
        URL url;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else {
                throw new RuntimeException(" responseCode is not 200 ... ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignore) {
            }
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException ignore) {
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

}
