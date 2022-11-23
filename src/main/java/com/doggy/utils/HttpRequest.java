package com.doggy.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * @ClassName:HttpUtil
 * @Auther: yyj
 * @Description: 网络请求工具类
 * @Date: 30/09/2022 09:01
 * @Version: v1.0
 */

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);
    public static String getRemoteIp(HttpServletRequest request){
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    /**
     * get请求，参数拼接在地址上
     * @param url 请求地址加参数
     * @return 响应
     */
    public static String getwx(String url) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new ClientProtocolException("Response contains no content");
                }
                // 读取返回内容
                ContentType contentType = ContentType.getOrDefault(entity);
                Charset charset = contentType.getCharset();
                Charset needCharset = charset == null ? charset: Charset.forName("utf-8");
                String s = EntityUtils.toString(entity ,Charset.forName("utf-8"));
                return s;
            }

        }catch (Exception e){
            new RuntimeException("HttpClient: Get请求出错");
            e.printStackTrace();
        } finally {

        }
        return null;
    }



    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }



    public static String get(String url) throws Exception {
        String result = "";
        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();

            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            // 建立实际的连接
            connection.connect();

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            return result;

        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * post请求
     */
    public static String postwx(String url,String data){
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            // 设置请求头为json
            post.setHeader("Accept", "application/json");
            post.setHeader(HTTP.CONTENT_TYPE, "application/json");
            // 设置请求参数
            StringEntity entity = new StringEntity(data,"utf-8");
            post.setEntity(entity);
            // 执行，处理结果
            HttpResponse execute = client.execute(post);
            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                return EntityUtils.toString(execute.getEntity());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String post(String url, String json) throws Exception {
        return post(url,json, ContentType.APPLICATION_JSON);
    }

    public static String post(String url, String data, ContentType contentType) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString =null;

        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(data, contentType);
            httpPost.setEntity(entity);

            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");

            return resultString;
        }
        finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
