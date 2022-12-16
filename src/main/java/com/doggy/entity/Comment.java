package com.doggy.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:Comment
 * @Auther: yyj
 * @Description:
 * @Date: 30/11/2022 12:25
 * @Version: v1.0
 */

@Data
public class Comment {
    private int comment_id;
    private int good_id;
    private String content="";
    private String reply_content;
    private String create_time;
    private String modified_time;
    private int customer_id;
    private int order_detail_id;
    private int status = 1;
    private double rate = 5;
    private HashMap<String,Object> customer_info = new HashMap<>();
    private List<String> commentImage;
}
