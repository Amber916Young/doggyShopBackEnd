package com.doggy.entity;

import lombok.Data;

import java.util.HashMap;

/**
 * @author birdyyoung
 * @ClassName:Rule
 * @Description:
 * @Date: 02/12/2022 20:30
 * @Version: v1.0
 */
@Data
public class Rule {
    private int rule_id;
    private String name; //规则名称
    private int type;  //优惠卷类型, 0-满减, 1-折扣 2 直减
    private String receive_started_at; //领取开始时间
    private String receive_ended_at; //领取结束时间
    private String use_started_at;//使用开始时间
    private String use_ended_at; //使用结束时间
    private double amount;  //优惠金额
    private double threshold; //门槛
    private int receive_count; //每个用户可以领取的数量
    private int use_range; //使用范围，0—全场，1—商品
    private String modified_time;
    private String create_time;
    private String goods_list;

}
