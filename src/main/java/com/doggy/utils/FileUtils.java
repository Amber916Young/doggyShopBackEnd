package com.doggy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @ClassName:FileUtils
 * @Auther: yyj
 * @Description:
 * @Date: 25/11/2022 14:27
 * @Version: v1.0
 */
public class FileUtils {


    /**
     * @param strUrl web链接
     * @return url返回的页面或值
     */

    public static String getUrlContent(String strUrl) {

        try {
            String result = "";
            int k = 0;
            while (result.equals("") && k < 5) {
                URL url = new URL(strUrl);


                URLConnection rulConnection = url.openConnection();// 此处的urlConnection对象实际上是根据URL的
                // 请求协议(此处是http)生成的URLConnection类
                // 的子类HttpURLConnection,故此处最好将其转化
                // 为HttpURLConnection类型的对象,以便用到
                // HttpURLConnection更多的API.如下:

                HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
                // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
                // http正文内，因此需要设为true, 默认情况下是false;
                httpUrlConnection.setDoOutput(true);

                // 设置是否从httpUrlConnection读入，默认情况下是true;
                httpUrlConnection.setDoInput(true);
                httpUrlConnection.setConnectTimeout(5 * 1000);
                // Post 请求不能使用缓存
                httpUrlConnection.setUseCaches(false);

                // 设定传送的内容类型是可序列化的java对象
                // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
                httpUrlConnection.setRequestProperty("Content-type", "application/x-javascript; charset=UTF-8; jingweijiexun");
                //httpUrlConnection.setRequestProperty("Content-Type", "application/x-javascript; charset=UTF-8");
                // 设定请求的方法为"POST"，默认是GET
                //httpUrlConnection.setRequestMethod("POST");
                // 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，
                try {
                    httpUrlConnection.connect();
                    // 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，
                    // 所以在开发中不调用上述的connect()也可以)。
                    //OutputStream outStrm = httpUrlConnection.getOutputStream();


                    BufferedReader br = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream(), "UTF-8"));

                    //.openStream()));

                    String s = "";

                    StringBuffer sb = new StringBuffer("");

                    while ((s = br.readLine()) != null) {

                        sb.append(s);//new String(s.getBytes(),"GBK")

                    }

                    br.close();
                    result = sb.toString();
                    k = 6;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    k++;
                }
            }
            return result;

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
            return "";

        }

    }


}
