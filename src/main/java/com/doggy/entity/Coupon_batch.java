package com.doggy.entity;

import lombok.Data;

import java.util.HashMap;

/**
 * @author birdyyoung
 * @ClassName:Coupon_batch
 * @Author: yyj
 * @Description:
 * @Date: 02/12/2022 20:26
 * @Version: v1.0
 */
@Data
public class Coupon_batch {
    private int batch_id; //批次ID
    private int rule_id; //规则,外键
    private int total_count; //总数量
    private int assign_count; //已发放劵数量
    private int used_count;  //已使用劵数量
    private String batch_name; //批次名称
    private String coupon_name; //劵名称
    private String used_time;  //使用时间
    private String create_time;
    private String modified_time;

    private HashMap<String,Object> ruleMap;
    private Rule rule;
}
