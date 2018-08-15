package com.zlzhang.timer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;

public class NFDFlightDataTimerTask  extends TimerTask {
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void run() {
        try {
            //在这里写你要执行的内容

            System.out.println("执行当前时间"+formatter.format(Calendar.getInstance().getTime()));
        } catch (Exception e) {
            System.out.println("-------------解析信息发生异常--------------");
        }
    }

    public static String getHttpURLConnection(String urlString,String params){

        String result = null;

        try {

            URL url = new URL(urlString+"?"+params);

            HttpURLConnection connection=(HttpURLConnection) url.openConnection();

            connection.setDoInput(true);//设置可输入

            connection.setDoOutput(true);//设置可以输出


            connection.setRequestProperty("contentType", "GBK");

            connection.setRequestMethod("GET");//设置请求方式//必须是大写

            connection.setConnectTimeout(50000);//设置连接超时时间。

            connection.setReadTimeout(50000);//设置读数据的超时时间

            // Post 请求不能使用缓存

            connection.setUseCaches(true);

            connection.connect();//连接服务器
            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));

            String data;

            StringBuilder builder=new StringBuilder();

            while((data=reader.readLine())!=null){

                builder.append(data);

            }

            result=builder.toString();

        }catch(Exception e) {

            e.printStackTrace();

        }

        return result;

    }


}
