package com.doggy.entity.EManage;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Pms_spu_info {
    private int spu_id;
    private int catalog_id;
    private int brand_id;
    private int publish_status;
    private String spu_name;
    private String spu_description;
    private Timestamp create_time;
    private Timestamp modified_time;
}
