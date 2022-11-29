package com.doggy.entity;

import lombok.Data;

import java.util.List;

/**
 * @ClassName:Goods
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 13:17
 * @Version: v1.0
 */
@Data
public class Goods {
    private int id;
    private String title;
    private String description;
    private int label_id; //标签【hot/折】这类的标签
    private double price;
    private double original_price;
    private List<ImageRepo> imageList;
    private String img_url ; // 此字段在数据库中不存在
    private int total_number; //总库存
    private int sale_number;
    private int amount = 0;
    private int category_id;  //类别
    private String create_time;
    private String modify_time;
    private int flag; // 0 下架 1 上新
    private String specification; // 0 下架 1 上新

}


