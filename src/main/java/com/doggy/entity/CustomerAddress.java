package com.doggy.entity;

import lombok.Data;

/**
 * @ClassName:CustomerAddress
 * @Auther: yyj
 * @Description:
 * @Date: 23/11/2022 15:36
 * @Version: v1.0
 */
@Data
public class CustomerAddress {
    private int customer_addr_id;
    private int customer_id;
    private int zip;
    private String username;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String address;
    private int is_default;
}
