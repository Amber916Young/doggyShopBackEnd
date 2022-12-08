package com.doggy.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:Category
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 13:04
 * @Version: v1.0
 */
@Data
public class Category {
    private int id;
    private String title;
    private String description;
    private String create_time;
    private String img_url;
    private String modify_time;
    private int order_id; //顺序
    private int flag; //0下架 1 上架
    private List<HashMap<String,Object>> goodsList;
}
