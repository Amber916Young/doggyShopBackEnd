package com.doggy.entity.EManage;

import lombok.Data;

@Data
public class Pms_sku_info {
    private int sku_id;
    private int spu_id;
    private int catalog_id;
    private int brand_id;
    private String sku_name ;
    private String sku_desc ;
    private String sku_default_img ;
    private String sku_title ;
    private String sku_subtitle ;
    private double price ;
    private int sale_count;
}
