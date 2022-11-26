package com.doggy.utils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName:NumberUtil
 * @Auther: yyj
 * @Description:
 * @Date: 24/11/2022 15:13
 * @Version: v1.0
 */
public class NumberUtil {


    public static void main(String args[]) {
//        String strNumber = NumberUtil.customFormatDate("yyyyMMddHHmmssSSSSSSS");
//        System.out.println("编号：" + strNumber);

        String s = new SimpleDateFormat("yyMMdd").format(new Date());
        System.out.println(s);
        StringBuilder str=new StringBuilder();
        str.append(s);
        Random random=new Random();
        //随机生du成数字，并添加zhi到字符串
//        for(int i=0;i<8;i++){
//        str.append(random.nextInt(10));
//        }
        int min=10000000;
        int max=99999999;
        int sum  = random.nextInt(max)%(max-min+1) + min;
        str.append(sum);
        System.out.println(str);
    }
    public static String customUserUID() {
        String s = new SimpleDateFormat("yyMMdd").format(new Date());
        StringBuilder str=new StringBuilder();
        str.append(s);
        Random random=new Random();
        int min=10000000;
        int max=99999999;
        int sum  = random.nextInt(max)%(max-min+1) + min;
        str.append(sum);
        return str.toString();
    }

    public static String customFormatDate(String dateFormat) {
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        return str;
    }

}
