package com.doggy.entity;

import lombok.Data;

/**
 * @author birdyyoung
 * @ClassName:Coupon
 * @Auther: yyj
 * @Description:
 * @Date: 02/12/2022 20:06
 * @Version: v1.0
 */
@Data
public class Coupon {
    private int coupon_id;
    private int customer_id;
    private int batch_id; //批次ID
    private int status; //0-未使用,1-已使用,2-已过期,3-冻结
    private int order_id; //对应订单ID,优惠卷未使用之前,对应订单ID为NULL
    private String received_time; //领取时间
    private String validate_time; //有效日期
    private String used_time;  //使用时间
    private String modified_time;
    private String create_time;
}
