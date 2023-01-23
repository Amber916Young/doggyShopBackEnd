package com.doggy.entity.EManage;

import lombok.Data;

@Data
public class Pms_product_attr_value {
    private int id;
    private int spu_id;
    private int attr_id;
    private String attr_name;
    private String attr_value;
    private int quick_show;
}
