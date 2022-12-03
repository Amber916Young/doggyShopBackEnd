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
    private int type;  //优惠卷类型, 0-满减, 1-折扣
    private HashMap<String,Object> rule_content;
    private String modified_time;
    private String create_time;
}
