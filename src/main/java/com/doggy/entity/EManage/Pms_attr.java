package com.doggy.entity.EManage;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:Pms_attr_group
 * @Auther: yyj
 * @Description:
 * @Date: 21/01/2023 23:22
 * @Version: v1.0
 */
@Data
public class Pms_attr {
    int attr_id;
    String attr_name;
    int catelog_id;
    int search_type;
    int enable;
    int value_type; //值类型[0-为单个值，1-可以选择多个值]
    String value_select; //可选值列表[用逗号分隔]
    int attr_type; // 属性类型[0-销售属性，1-基本属性]
    int show_desc; //快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
    String[]  value_list;
}
