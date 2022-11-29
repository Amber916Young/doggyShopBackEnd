package com.doggy.mapper;


import com.doggy.entity.Category;
import com.doggy.entity.CustomerAddress;
import com.doggy.entity.CustomerInfo;
import com.doggy.utils.Page;

import java.util.HashMap;
import java.util.List;

public interface CustomerMapper {


    CustomerInfo queryCustomerInfo(HashMap<String, Object> map);

    void updateCustomerInfo(CustomerInfo customerInfo);

    int insertCustomerInfo(CustomerInfo customerInfo);

    CustomerInfo queryCustomerByid(int id);

    List<CustomerInfo> pageQueryUserData(Page page);

    int pageQueryUserCount(Page page);

    int pageQueryAddressCount(Page page);

    List<CustomerAddress> pageQueryAddressData(Page page);
}
