package com.doggy.service;

import com.doggy.entity.OrderCart;
import com.doggy.mapper.SysGoodsMapper;
import com.doggy.mapper.SysOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:SysOrderService
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 17:27
 * @Version: v1.0
 */
@Service
public class SysOrderService {

    @Autowired
    SysOrderMapper orderMapper;

    public OrderCart queryOrderCart(HashMap<String, Object> param) {
        return orderMapper.queryOrderCart(param);
    }

    public void insertOrderCart(HashMap<String, Object> param) {
        orderMapper.insertOrderCart(param);
    }

    public void updateOrderCart(HashMap<String, Object> param) {
        orderMapper.updateOrderCart(param);
    }

    public List<OrderCart> queryOrderCartList(HashMap<String, Object> param) {
        return orderMapper.queryOrderCartList(param);
    }

    public void deleteOrderCart(HashMap<String, Object> param) {
        orderMapper.deleteOrderCart(param);
    }
}
