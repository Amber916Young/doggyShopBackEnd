package com.doggy.mapper;


import com.doggy.entity.CustomerAddress;

import java.util.HashMap;
import java.util.List;

public interface SysAddressMapper {

    List<CustomerAddress> queryAllAddress(HashMap<String, Object> param);

    void insertAddress(HashMap<String, Object> param);

    void deleteAddress(HashMap<String, Object> param);

    void updateAddress(HashMap<String, Object> param);

    CustomerAddress queryAddressByid(int customer_addr_id);

    CustomerAddress queryAddressByParam(HashMap<String, Object> param);
}
