package com.doggy.entity.EManage;

import lombok.Data;

@Data
public class Pms_sku_images {
    private int id;
    private int sku_id;
    private String img_url;
    private int img_sort;
    private int default_img;
}
