package com.doggy.entity;

import lombok.Data;

import java.util.HashMap;

/**
 * @ClassName:OrderDetail
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 21:10
 * @Version: v1.0
 */
@Data
public class OrderDetail {
    private int order_id;
    private int order_detail_id;
    private int good_id;
    private int good_amount;
    private int is_comment;
    private double good_price;
    private double average_cost; //平均成本价格
    private float weight;
    private double fee_money; //优惠分摊金额
    private String modified_time;
    private Goods goodObj; // 备注
    private HashMap<String,Object> goodMap = new HashMap<>(); // 备注


}
