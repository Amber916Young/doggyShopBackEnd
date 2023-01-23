package com.doggy.entity.EManage;

import lombok.Data;

@Data
public class Pms_category_brand_relation {
    private int id;
    private int brand_id;
    private int catelog_id;
    private String brand_name;
    private String category_name;
}
