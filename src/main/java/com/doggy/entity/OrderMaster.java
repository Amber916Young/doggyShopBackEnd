package com.doggy.entity;

import lombok.Data;

import java.util.List;

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
    private String order_sn; // 订单编号 yyyymmddnnnnnnnn
    private int customer_id; //下单人ID
    private int payment_method; //支付方式：
    // 1现金，2余额，3网银，4支付宝，5微信
    private double order_money; //订单金额
    private double district_money = 0.0; //优惠金额
    private double shipping_money = 0.0; //运费金额
    private double payment_money; // 支付金额
    private String shipping_comp_name; //快递公司名称
    private String shipping_sn; //快递单号
    private String create_time; //下单时间
    private String shipping_time; //发货时间
    private String pay_time; //支付时间
    private String receive_time;//收货时间
    private int order_status; //订单状态 0未发货 1 已发货 2已完成

    private int order_point; //订单积分
    private String invoice_time; //发票抬头
    private String modified_time; //订单状态

    private List<OrderDetail> OrderDetailList; //订单详细ID
    private String memo; // 备注


    // 不能用 地址id，因为地址可能会被删除
    private String province; //省
    private String city; //市
    private String district; //区
    private String address; // 地址
    private String username; // 收货人
    private String phone; //电话

}
