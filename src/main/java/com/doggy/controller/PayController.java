package com.doggy.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.doggy.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName:PayController
 * @Auther: yyj
 * @Description:
 * @Date: 23/11/2022 11:17
 * @Version: v1.0
 */
@Controller
@RequestMapping("alipay")
public class PayController {
    @Autowired
    AlipayService alipayService;
    //appid
    @Value("${Alipay.NOTIFY_URL}")
    private String NOTIFY_URL;
    //支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
    @Value("${Alipay.RETURN_URL}")
    private String RETURN_URL ;
    @Value("${Alipay.GATEWAY_URL}")
    private String GATEWAY_URL ;
    @Value("${Alipay.APP_ID}")
    private String APP_ID ;
    @Value("${Alipay.APP_PRIVATE_KEY}")
    private String APP_PRIVATE_KEY ;
    @Value("${Alipay.FORMAT}")
    private String FORMAT ;
    @Value("${Alipay.CHARSET}")
    private String CHARSET ;
    @Value("${Alipay.ALIPAY_PUBLIC_KEY}")
    private String ALIPAY_PUBLIC_KEY ;
    @Value("${Alipay.SIGN_TYPE}")
    private String SIGN_TYPE ;


    //必须加ResponseBody注解，否则spring会寻找thymeleaf页面
    @ResponseBody
    @RequestMapping("/pay/alipay")
    public String alipay(HttpSession session, Model model,
                         @RequestParam("dona_money") float dona_money,
                         @RequestParam("dona_id") int dona_id) throws AlipayApiException {
        //把dona_id项目id 放在session中
        session.setAttribute("dona_id",dona_id);

        //生成订单号（支付宝的要求？）
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String user = UUID.randomUUID().toString().replace("-","").toUpperCase();

        String OrderNum = time+user;

        //调用封装好的方法（给支付宝接口发送请求）
        return sendRequestToAlipay(OrderNum,dona_money,"ghjk");
    }
    /*
参数1：订单号
参数2：订单金额
参数3：订单名称
 */
    //支付宝官方提供的接口
    private String sendRequestToAlipay(String outTradeNo,Float totalAmount,String subject) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL,APP_ID,APP_PRIVATE_KEY,FORMAT,CHARSET,ALIPAY_PUBLIC_KEY,SIGN_TYPE);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(RETURN_URL);
        alipayRequest.setNotifyUrl(NOTIFY_URL);

        //商品描述（可空）
        String body="";
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + totalAmount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        return result;
    }


    @RequestMapping("/returnUrl")
    public String returnUrlMethod(HttpServletRequest request, HttpSession session, Model model) throws AlipayApiException, UnsupportedEncodingException {
        System.out.println("=================================同步回调=====================================");

        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        System.out.println(params);//查看参数都有哪些
        //验证签名（支付宝公钥）
        boolean signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, CHARSET, SIGN_TYPE); // 调用SDK验证签名
        //验证签名通过
        if(signVerified){
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 支付宝交易流水号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 付款金额
            float money = Float.parseFloat(new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8"));

            System.out.println("商户订单号="+out_trade_no);
            System.out.println("支付宝交易号="+trade_no);
            System.out.println("付款金额="+money);

            //在这里编写自己的业务代码（对数据库的操作）
			/*
			################################
			*/
            //跳转到提示页面（成功或者失败的提示页面）
            model.addAttribute("flag",1);
            model.addAttribute("msg","支持");
            return "common/payTips";
        }else{
            //跳转到提示页面（成功或者失败的提示页面）
            model.addAttribute("flag",0);
            model.addAttribute("msg","支持");
            return "common/payTips";
        }
    }






}
