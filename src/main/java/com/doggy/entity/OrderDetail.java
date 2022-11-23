package com.doggy.entity;

import lombok.Data;

/**
 * @ClassName:OrderDetail
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 21:10
 * @Version: v1.0
 */
@Data
public class OrderDetail {
    private int order_detail_id;
    private int order_id;
    private int good_id;
    private int good_name;
    private int good_cnt;
    private double good_price;
    private double average_cost; //平均成本价格
    private float weight;
    private double fee_money; //优惠分摊金额
    private int w_id; //仓库ID
    private String modified_time;

}
