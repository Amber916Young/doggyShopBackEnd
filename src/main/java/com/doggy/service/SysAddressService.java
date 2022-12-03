package com.doggy.service;

import com.doggy.entity.CustomerAddress;
import com.doggy.mapper.SysAddressMapper;
import com.doggy.mapper.SysGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:SysAddressService
 * @Auther: yyj
 * @Description:
 * @Date: 23/11/2022 15:38
 * @Version: v1.0
 */
@Service
public class SysAddressService {

    @Autowired
    SysAddressMapper addressMapper;

    public List<CustomerAddress> queryAllAddress(HashMap<String, Object> param) {
        return addressMapper.queryAllAddress(param);
    }

    public void insertAddress(HashMap<String, Object> param) {
        addressMapper.insertAddress(param);
    }

    public void deleteAddress(HashMap<String, Object> param) {
        addressMapper.deleteAddress(param);
    }

    public void updateAddress(HashMap<String, Object> param) {
        addressMapper.updateAddress(param);
    }

    public CustomerAddress queryAddressByid(int customer_addr_id) {
        return addressMapper.queryAddressByid(customer_addr_id);
    }

    public CustomerAddress queryAddressByParam(HashMap<String, Object> param) {
        return addressMapper.queryAddressByParam(param);
    }

    public void updateIsDefault(HashMap<String, Object> param) {
        addressMapper.updateIsDefault(param);
    }
}
