package com.doggy.entity.EManage;

import lombok.Data;

@Data
public class Pms_category {
    private int id;
    private String title;
    private int pid;
    private String icon;
    private String href;
    private boolean spread;
    private boolean checked;
}
