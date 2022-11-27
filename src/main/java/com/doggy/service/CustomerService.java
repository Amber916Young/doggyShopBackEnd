package com.doggy.service;

import com.doggy.entity.CustomerInfo;
import com.doggy.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @ClassName:CustomerService
 * @Auther: yyj
 * @Description:
 * @Date: 25/11/2022 14:59
 * @Version: v1.0
 */
@Service
public class CustomerService {
    @Autowired
    CustomerMapper customerMapper;

    public CustomerInfo queryCustomerInfo(HashMap<String, Object> map) {
        return customerMapper.queryCustomerInfo(map);
    }

    public void updateCustomerInfo(CustomerInfo customerInfo) {
        customerMapper.updateCustomerInfo(customerInfo);
    }

    public int insertCustomerInfo(CustomerInfo customerInfo) {
        return customerMapper.insertCustomerInfo(customerInfo);
    }

    public CustomerInfo queryCustomerByid(int id) {
        return customerMapper.queryCustomerByid(id);
    }
}
