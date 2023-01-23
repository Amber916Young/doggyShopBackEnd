package com.doggy.entity.EManage;

import lombok.Data;

@Data
public class Pms_sku_sale_attr_value {
    private int id;
    private int sku_id;
    private int attr_id;
    private String attr_name;
    private String attr_value;
}
