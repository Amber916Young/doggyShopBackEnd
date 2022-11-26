package com.doggy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.*;
import com.doggy.service.SysAddressService;
import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
import com.doggy.utils.HttpResult;
import com.doggy.utils.NumberUtil;
import com.doggy.utils.Page;
import com.doggy.utils.TokenUtils;
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
    @Autowired
    private SysAddressService addressService;

    @SneakyThrows
    @ResponseBody
    @PostMapping("/payment/cart")
    synchronized public HttpResult MockPayment(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);

        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        int payment_method = Integer.parseInt(param.get("payment_method").toString());
        int customer_addr_id = Integer.parseInt(param.get("customer_addr_id").toString());
        String memo = param.get("memo").toString();


        // get all cart
        List<OrderCart> cartList = orderService.queryOrderCartList(param);
        String order_sn = NumberUtil.customFormatDate("yyyyMMddHHmmssSSSSSSS");
        OrderMaster master = new OrderMaster();
        master.setOrder_sn(order_sn);
        master.setCustomer_id(customer_id);
        master.setCustomer_addr_id(customer_addr_id);
        master.setMemo(memo);
        master.setPayment_method(payment_method);

        double order_money = 0.0;  //订单金额
        double district_money = 0.0; //优惠金额
        double shipping_money = 0.0; //运费金额
        int order_point = 0;
        double payment_money = 0.0;
        List<OrderDetail> addOrderDetail = new ArrayList<>();
        for(OrderCart cart : cartList){
            OrderDetail detail = new OrderDetail();
            int good_id = cart.getGood_id();
            double price = cart.getPrice();
            int amount = cart.getGood_amount();
            order_point += amount * price;
            order_money += (amount * 1.0) * price;
            payment_money += (amount * 1.0) * price;
            detail.setGood_id(good_id);
            detail.setAverage_cost(price);
            detail.setGood_price(price);
            detail.setFee_money(district_money);
            detail.setGood_amount(amount);
            detail.setWeight(0);
            addOrderDetail.add(detail);
        }
        master.setPayment_money(payment_money);
        master.setOrder_money(order_money);
        master.setDistrict_money(district_money);
        // 物流信息
        master.setShipping_money(shipping_money);
        master.setShipping_comp_name("");
        master.setShipping_sn("");
        master.setOrder_status(0);
        // 积分
        master.setOrder_point(order_point);
        //发票抬头
        master.setInvoice_time("");
        int id =  orderService.insertOrderMaster(master);
        for(OrderDetail detail : addOrderDetail){
            detail.setOrder_id(master.getOrder_id());
            orderService.insertOrderDetail(detail);
        }
        HashMap<String,Object> map = new HashMap<>();
        for(OrderDetail detail : addOrderDetail){
            map.put("customer_id",customer_id);
            map.put("good_id",detail.getGood_id());
            orderService.deleteOrderCart(map);
        }



        return HttpResult.ok("successfully");
    }



    @SneakyThrows
    @ResponseBody
    @PostMapping("/get/orderbyId")
    public HttpResult GetCurrentOrderById(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        OrderMaster orderMaster = orderService.queryOrderMaster(param);
        List<OrderDetail> details = orderService.queryOrderDetailAll(orderMaster.getOrder_id());
        for(OrderDetail detail : details){
            Goods goods = goodsService.queryAllGoodsById(detail.getGood_id());
            detail.setGoodObj(goods);
        }
        orderMaster.setOrderDetailList(details);
        CustomerAddress customerAddress = addressService.queryAddressByid(orderMaster.getCustomer_addr_id());
        orderMaster.setAddress(customerAddress);
        return  HttpResult.ok("successfully",orderMaster);
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/order")
    public HttpResult GetCurrentOrderHistory(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        Page page = new Page();
        int limit = 20;
        int curr = Integer.parseInt(param.get("currNo").toString());
        int id = Integer.parseInt(param.get("customer_id").toString());
        limit = Integer.parseInt(param.get("limit").toString());
        page.setKeyWord(null);
        if(param.containsKey("keyWord")){
            page.setKeyWord(param.get("keyWord").toString());
        }
        int order_status = Integer.parseInt(param.get("order_status").toString());
        HashMap<String,Object> data = new HashMap<>();
        data.put("order_status",order_status);
        page.setData(data);
        page.setRows(limit);
        page.setPage(curr);
        int start = page.getStart();
        page.setStart(start);
        page.setId(id);

        int totals=orderService.pageQueryOrderCount(page);
        List<OrderMaster> lists = orderService.pageQueryOrderData(page);
        for(OrderMaster orderMaster : lists){
            List<OrderDetail> details = orderService.queryOrderDetailAll(orderMaster.getOrder_id());
            for(OrderDetail detail : details){
                Goods goods = goodsService.queryAllGoodsById(detail.getGood_id());
                detail.setGoodObj(goods);
            }
            orderMaster.setOrderDetailList(details);


        }
        HashMap<String, Object> res = new HashMap<>();
        res.put("orderList",lists);
        res.put("count",totals);
        return  HttpResult.ok("successfully",res);
    }








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
