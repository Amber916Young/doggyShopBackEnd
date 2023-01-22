package com.doggy.utils;

import com.doggy.entity.Goods;
import com.vdurmont.emoji.EmojiParser;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:UnicodeUtils
 * @Auther: yyj
 * @Description:
 * @Date: 06/12/2022 13:00
 * @Version: v1.0
 */
public class UnicodeUtils {

    public static String getTextFromHtml(String htmlStr){
        //去除html标签
        htmlStr = delHtmlTags(htmlStr);
        //去除空格" "
        htmlStr = htmlStr.replaceAll(" ","");
        return htmlStr;
    }


    public static String delHtmlTags(String htmlStr) {
        //定义script的正则表达式，去除js可以防止注入
        String scriptRegex="<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式，去除style样式，防止css代码过多时只截取到css样式代码
        String styleRegex="<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式，去除标签，只提取文字内容
        String htmlRegex="<[^>]+>";
        //定义空格,回车,换行符,制表符
        String spaceRegex = "\\s*|\t|\r|\n";

        // 过滤script标签
        htmlStr = htmlStr.replaceAll(scriptRegex, "");
        // 过滤style标签
        htmlStr = htmlStr.replaceAll(styleRegex, "");
        // 过滤html标签
        htmlStr = htmlStr.replaceAll(htmlRegex, "");
        // 过滤空格等
        htmlStr = htmlStr.replaceAll(spaceRegex, "");
        return htmlStr.trim(); // 返回文本字符串

    }

    public static HashMap<String, Object> DECODEUnicode(HashMap<String, Object> param, List<String> list) {
        for (String s : list){
            param.put(s, EmojiParser.parseToHtmlHexadecimal(param.get(s).toString()));
        }
        return param;
    }

    public static Goods DECODEUnicode(Goods goods) {
        goods.setTitle( EmojiParser.parseToHtmlHexadecimal(goods.getTitle()));
        goods.setDescription(EmojiParser.parseToHtmlHexadecimal(goods.getDescription()));
        return goods;
    }

}
