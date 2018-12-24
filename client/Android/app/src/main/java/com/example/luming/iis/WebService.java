package com.example.luming.iis;

/**
 * Created by jazzyin on 2016/3/25.
 */

import android.content.SharedPreferences;
import android.provider.ContactsContract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WebService {
    private static String IP = "192.168.43.111:8080";
    private static DatabaseOperator db;
    //把TOMCATURL改为你的服务地址

    /**
     * 通过Get方式获取HTTP服务器数据
     *
     * @return
     */
    public static String executeHttpGet(String url,String username, String password ) {
        String path = "http://" + IP + "/HelloWeb/";
        path = path + url + "?username=" + username + "&password=" + password ;
        return connect(path);
    }
    public static String getWebRecTime(String userId)
    {
        String path = "http://" + IP + "/HelloWeb/RecLet?id="+userId;
        return connect(path);
    }
    public static void upLoad(String id, String jsonArray)  {

        String path = null;
        try {
            path = "http://" + IP + "/HelloWeb/SyncLet?id="+id+"&data="+URLEncoder.encode(jsonArray,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(path);
    }
    public static String pull(String id,String time)
    {
        String path = null;
        path = "http://" + IP + "/HelloWeb/PullLet?id="+id+"&time="+time;
        return connect(path);
    }
    private static String connect(String path)
    {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            // 用户名 密码
            // URL 地址
            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(3000); // 设置超时时间
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式

            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                return parseInfo(is);
            }
            return null;

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "NULL";
    }
    // 将输入流转化为 String 型
    private static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        // 转化为字符串
        return new String(data, "UTF-8");
    }

    // 将输入流转化为byte型
    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }

}
