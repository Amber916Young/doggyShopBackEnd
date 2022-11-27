package com.doggy.mapper;


import com.doggy.entity.CustomerAddress;
import com.doggy.entity.CustomerInfo;

import java.util.HashMap;
import java.util.List;

public interface CustomerMapper {


    CustomerInfo queryCustomerInfo(HashMap<String, Object> map);

    void updateCustomerInfo(CustomerInfo customerInfo);

    int insertCustomerInfo(CustomerInfo customerInfo);

    CustomerInfo queryCustomerByid(int id);
}
