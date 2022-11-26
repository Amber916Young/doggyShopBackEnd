package com.doggy.entity;

import lombok.Data;

/**
 * @ClassName:CustomerInfo
 * @Auther: yyj
 * @Description:
 * @Date: 25/11/2022 15:08
 * @Version: v1.0
 */
@Data
public class CustomerInfo {
    private int id;
    private int points;
    private String uuid;
    private String openid;
    private String username = "微信用户";
    private String phone;
    private String gender = "0";
    private String Token;
    private String avatarUrl;
}
