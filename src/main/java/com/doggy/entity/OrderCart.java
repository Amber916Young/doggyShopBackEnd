package com.doggy.entity;

import lombok.Data;

/**
 * @ClassName:OrderCart
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 19:51
 * @Version: v1.0
 */
@Data
public class OrderCart {
    private int cart_id;
    private int customer_id;
    private int good_id;
    private int good_amount;
    private double price;
    private String add_time;
    private String modified_time;
    private Goods goods;
}
