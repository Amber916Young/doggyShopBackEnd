package com.doggy.service;

import com.doggy.entity.Category;
import com.doggy.entity.CustomerAddress;
import com.doggy.entity.CustomerInfo;
import com.doggy.entity.OrderMaster;
import com.doggy.mapper.*;
import com.doggy.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

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
    public void updateCustomerPoints(CustomerInfo customerInfo) {
        customerMapper.updateCustomerPoints(customerInfo);

    }

    public int insertCustomerInfo(CustomerInfo customerInfo) {
        return customerMapper.insertCustomerInfo(customerInfo);
    }

    public CustomerInfo queryCustomerByid(int id) {
        return customerMapper.queryCustomerByid(id);
    }

    public List<CustomerInfo> pageQueryUserData(Page page) {
        return customerMapper.pageQueryUserData(page);
    }

    public int pageQueryUserCount(Page page) {
        return customerMapper.pageQueryUserCount(page);
    }


    public List<CustomerAddress> pageQueryAddressData(Page page) {
        return customerMapper.pageQueryAddressData(page);
    }

    public int pageQueryAddressCount(Page page) {
        return customerMapper.pageQueryAddressCount(page);
    }


}
