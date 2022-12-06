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
