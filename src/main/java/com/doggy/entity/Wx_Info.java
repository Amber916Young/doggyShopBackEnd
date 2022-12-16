package com.doggy.entity;

import lombok.Data;

/**
 * @ClassName:Wx_Info
 * @Auther: yyj
 * @Description:
 * @Date: 16/12/2022 14:52
 * @Version: v1.0
 */
@Data
public class Wx_Info {
    private int wx_id;
    private String appid;
    private String appSecret;
    private String name;
    private String originalID;
}
