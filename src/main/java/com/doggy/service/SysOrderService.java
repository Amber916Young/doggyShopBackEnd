package com.doggy.service;

import com.doggy.entity.OrderCart;
import com.doggy.entity.OrderDetail;
import com.doggy.entity.OrderMaster;
import com.doggy.mapper.SysGoodsMapper;
import com.doggy.mapper.SysOrderMapper;
import com.doggy.utils.Page;
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

    public int pageQueryOrderCount(Page page) {
        return orderMapper.pageQueryOrderCount(page);
    }

    public List<OrderMaster> pageQueryOrderData(Page page) {
        return orderMapper.pageQueryOrderData(page);
    }

    public OrderMaster queryOrderMaster(HashMap<String, Object> param) {
        return orderMapper.queryOrderMaster(param);
    }

    public List<OrderDetail> queryOrderDetailAll(int order_id) {
        return orderMapper.queryOrderDetailAll(order_id);
    }

    public int insertOrderMaster(OrderMaster master) {
        return orderMapper.insertOrderMaster(master);
    }

    public void insertOrderDetail(OrderDetail detail) {
        orderMapper.insertOrderDetail(detail);
    }

    public List<HashMap<String,Object>> pageQueryOrderDetailData(Page page) {
        return orderMapper.pageQueryOrderDetailData(page);
    }

    public int pageQueryDetailCount(Page page) {
        return orderMapper.pageQueryDetailCount(page);
    }


    public List<OrderDetail> queryOrderDetailMap(HashMap<String, Object> query) {
        return orderMapper.queryOrderDetailMap(query);
    }

    public void updateOrderDetail(HashMap<String, Object> map) {
         orderMapper.updateOrderDetail(map);
    }

}
