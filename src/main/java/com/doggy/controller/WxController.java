package com.doggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.CardUserInfo;
import com.doggy.entity.CustomerInfo;
import com.doggy.entity.Wx_Info;
import com.doggy.service.CustomerService;
import com.doggy.service.WxService;
import com.doggy.utils.*;
import com.fasterxml.uuid.Generators;
import lombok.SneakyThrows;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.*;

import static com.doggy.utils.WxApi.code2Session;

/**
 * @ClassName:WxController
 * @Auther: yyj
 * @Description:
 * @Date: 24/11/2022 23:16
 * @Version: v1.0
 */
@RestController
@RequestMapping("wx")
public class WxController {



//    @Value("${WeChat.appid}")
//    private String APP_ID ;
//    @Value("${WeChat.appSecret}")
//    private String SECRET ;

    @Autowired
    CustomerService customerService;

    @Autowired
    WxService wxService;

    @SneakyThrows
    @ResponseBody
    @PostMapping("/usual/login")
    public HttpResult UsualLogin(@RequestBody String jsonData) {
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        CustomerInfo customer = JSONObject.parseObject(param.get("customer").toString(),CustomerInfo.class);
        String token =TokenUtils.refreshToken(customer);
        customer.setToken(token);
        if(customer.getAvatarUrl()==null || customer.getAvatarUrl().length()==0
                || customer.getAvatarUrl().trim().equals("") || !customer.getAvatarUrl().contains("data")){
            customer.setAvatarUrl(null);
        }
        HashMap<String,Object> map = new HashMap<>();
        map.put("openid",customer.getOpenid());
        CustomerInfo isExist = customerService.queryCustomerInfo(map);
        if(isExist == null){
            customerService.insertCustomerInfo(customer);
            TimeParse timeParse = new TimeParse();
            String create_time = timeParse.convertDate2String(new Date(),"yyyy-MM-dd HH:mm:ss");
            customer.setCreate_time(create_time);
            return HttpResult.ok(customer);
        }else {
            isExist.setToken(token);
            isExist.setAvatarUrl(customer.getAvatarUrl());
            customerService.updateCustomerInfo(isExist);
            return HttpResult.ok(isExist);
        }
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/login")
    public HttpResult WxLogin(@RequestBody String jsonData){
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        String code = param.get("code").toString();
        String appid =param.get("appid").toString();

        String onlinePeople = getOnlinePeople(code,appid);
        JSONObject object= JSONObject.parseObject(onlinePeople);
        String openid = object.getString("openid");


        String session_key = object.getString("session_key");
        String encryptedData = param.get("encryptedData").toString();
        String signature = param.get("signature").toString();
        String avatarUrl = "";
        if(param.containsKey("avatarUrl")){
            avatarUrl = param.get("avatarUrl").toString();
        }
        String iv = param.get("iv").toString();
//        String phone = getPhoneNumber(encryptedData,session_key,iv);
        // 自动登录
        HashMap<String,Object> map = new HashMap<>();
        map.put("openid",openid);
        if(openid == null)   {
            return HttpResult.error("获取openid失败");
        }

        CustomerInfo customerInfo = customerService.queryCustomerInfo(map);
        if(customerInfo == null) {
            customerInfo = new CustomerInfo();
            customerInfo.setAvatarUrl(avatarUrl);
            customerInfo.setOpenid(openid);

            CustomerInfo tmp = null;

            while (tmp == null) {
                String uuid = NumberUtil.customUserUID();
                map = new HashMap<>();
                map.put("uuid", uuid);
                customerInfo.setUuid(uuid);
                tmp = customerService.queryCustomerInfo(map);
                if (tmp == null) {
                    break;
                }
            }

            int id = customerService.insertCustomerInfo(customerInfo);
            TimeParse timeParse = new TimeParse();
            String create_time = timeParse.convertDate2String(new Date(),"yyyy-MM-dd HH:mm:ss");
            customerInfo.setCreate_time(create_time);
        }
        customerInfo.setOpenid(openid);
        customerInfo.setAvatarUrl(avatarUrl);
        String token =TokenUtils.genToken(customerInfo);
        customerInfo.setToken(token);
        customerService.updateCustomerInfo(customerInfo);
        return HttpResult.ok(customerInfo);
    }

    public  String getOnlinePeople(String code,String appid) {
        try {
            HashMap<String,Object> map = new HashMap<>();
            map.put("appid",appid);
            Wx_Info wx_info = wxService.queryWxInfo(map);
            String baseUrl = code2Session.replace("APPID",wx_info.getAppid())
                    .replace("SECRET",wx_info.getAppSecret()).replace("JSCODE",code);
            String temp = FileUtils.getUrlContent(baseUrl);
            System.out.println("拿到GET结果" + temp.toString());
            return temp;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("失败!");
        }
        return null;
    }


    public static synchronized String getPhoneNumber(String encryptedData, String session_key, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(session_key);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                JSONObject resultJson =  JSONObject.parseObject(result);
                String phone = resultJson.getString("phoneNumber");
                return phone;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
