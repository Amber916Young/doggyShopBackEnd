package com.doggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Goods;
import com.doggy.entity.OrderCart;
import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
import com.doggy.utils.HttpResult;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.*;

/**
 * @ClassName:OrderController
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 23:14
 * @Version: v1.0
 */
@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private SysOrderService orderService;
    @Autowired
    private SysGoodsService goodsService;



    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/cart/number")
    public HttpResult CartGetAllNumber(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        List<OrderCart> cartList= orderService.queryOrderCartList(param);
        return HttpResult.ok("successfully",cartList.size());
    }


    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/cart")
    public HttpResult CartGetAll(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        List<OrderCart> cartList= orderService.queryOrderCartList(param);
        double amount_price =0.0;
        for( OrderCart orderCart : cartList){
            int id = orderCart.getGood_id();
            Goods goods = getGoodsAmountImages(customer_id,id);
            amount_price += goods.getPrice() * goods.getAmount();
            orderCart.setGoods(goods);
        }
        param = new HashMap<>();
        param.put("size",cartList.size());
        param.put("cartList",cartList);
        param.put("amount_price",amount_price);

        return HttpResult.ok("successfully",param);
    }

    private Goods getGoodsAmountImages(int customer_id, int good_id){
        HashMap<String, Object> map = new HashMap<>();
        map.put("customer_id", customer_id);
        map.put("good_id", good_id);
        OrderCart orderCart = orderService.queryOrderCart(map);
        Goods goods = goodsService.queryAllGoodsById(good_id);
        if(orderCart != null){
            goods.setAmount(orderCart.getGood_amount());
        }
        return goods;
    }

    private void  modifyOrder_cart( HashMap<String, Object> param ){
        int amount = Integer.parseInt(param.get("good_amount").toString());
        if(amount == 0){
            orderService.deleteOrderCart(param);
            return;
        }
        OrderCart orderCart = orderService.queryOrderCart(param);
        if(orderCart == null){
            param.put("add_time",new Date());
            orderService.insertOrderCart(param);
        }else {
            orderService.updateOrderCart(param);
        }

    }
    @SneakyThrows
    @ResponseBody
    @PostMapping("/change/number/return/cartList")
    public HttpResult GoodsChangenNumberReturn(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        modifyOrder_cart(param);
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        List<OrderCart> cartList= orderService.queryOrderCartList(param);
        for( OrderCart orderCart : cartList){
            int id = orderCart.getGood_id();
            Goods goods = getGoodsAmountImages(customer_id,id);
            orderCart.setGoods(goods);
        }
        param = new HashMap<>();
        param.put("size",cartList.size());
        param.put("cartList",cartList);
        return HttpResult.ok("successfully",param);
    }
    @SneakyThrows
    @ResponseBody
    @PostMapping("/change/number")
    public HttpResult GoodsChangenNumber(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        modifyOrder_cart(param);
        List<OrderCart> cartList= orderService.queryOrderCartList(param);
        return HttpResult.ok("successfully",cartList.size());
    }
    @SneakyThrows
    @ResponseBody
    @PostMapping("/change/detail/number")
    public HttpResult GoodsChangenDetailNumber(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        modifyOrder_cart(param);
        OrderCart orderCart = orderService.queryOrderCart(param);
        return HttpResult.ok("successfully",orderCart);
    }
}
