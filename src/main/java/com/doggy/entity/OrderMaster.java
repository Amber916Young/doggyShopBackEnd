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
public class OrderMaster {
    private int order_id; //订单ID
    private long order_sn; // 订单编号 yyyymmddnnnnnnnn
    private int customer_id; //下单人ID
    private String shipping_user;

    private int province;
    private int city;
    private int address;
    private int payment_method;
    private double order_money;
    private double district_money;
    private double shipping_money;
    private double payment_money;
    private String shipping_comp_name;
    private String shipping_sn; //快递单号
    private String create_time;
    private String shipping_time;
    private String pay_time;
    private String receive_time;
    private int order_status; //订单状态

    private int order_point; //订单积分
    private String invoice_time; //发票抬头
    private String modified_time; //订单状态

}
