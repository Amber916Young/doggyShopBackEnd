package com.doggy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.*;
import com.doggy.service.*;
import com.doggy.utils.*;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
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

    @Autowired
    CustomerService customerService;

    @SneakyThrows
    @ResponseBody
    @PostMapping("/clear/cart")
    synchronized public HttpResult Clear(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        orderService.deleteOrderCartAll(customer_id);
        return HttpResult.ok("successfully");
    }

        @SneakyThrows
    @ResponseBody
    @PostMapping("/payment/cart")
    synchronized public HttpResult MockPayment(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        OrderMaster master = new OrderMaster();

        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        int payment_method = Integer.parseInt(param.get("payment_method").toString());
//        int customer_addr_id = Integer.parseInt(param.get("customer_addr_id").toString());
        String memo = param.get("memo").toString();
        CustomerAddress address = JsonUtils.jsonToPojo(param.get("address").toString(),CustomerAddress.class);
        master.setUsername(address.getUsername());
        master.setProvince(address.getProvince());
        master.setCity(address.getCity());
        master.setAddress(address.getAddress());
        master.setDistrict(address.getDistrict());
        master.setPhone(address.getPhone());

        // get all cart
        List<OrderCart> cartList = orderService.queryOrderCartList(param);
        if(cartList.size() == 0) {
            return HttpResult.error("一些未知错误");
        }

        String order_sn = NumberUtil.customFormatDate("yyyyMMddHHmmssSSSSSSS");
        master.setOrder_sn(order_sn);
        master.setCustomer_id(customer_id);
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
       // TODO update userinfo
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setId(customer_id);
        customerInfo.setPoints(order_point);
        customerService.updateCustomerPoints(customerInfo);
        customerInfo = customerService.queryCustomerByid(customer_id);
        return HttpResult.ok("successfully",customerInfo);
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
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
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
        page.setId(customer_id);

//        int totals=orderService.pageQueryOrderCount(page);
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
//        res.put("count",totals);
        return  HttpResult.ok("successfully",res);
    }

    @Autowired
    private SysCommentService commentService;
    /**
     * get all order which need to comment
     * @param jsonData
     * @return
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping("/get/goods/comment")
    public HttpResult GetOrder_needComment(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        int curr = Integer.parseInt(param.get("currNo").toString());
        int type = Integer.parseInt(param.get("type").toString());
        param.put("order_status",4); // 已签收
        Page page = new Page();
        int limit = 20;
        page.setData(param);
        page.setRows(limit);
        page.setPage(curr);
        int start = page.getStart();
        page.setStart(start);
        page.setId(customer_id);
        List<OrderMaster> masters = orderService.pageQueryOrderData(page);
        List<HashMap<String, Object>> list = new ArrayList<>();
        for( OrderMaster item : masters){
            int order_id = item.getOrder_id();
            HashMap<String, Object> query = new HashMap<>();
            query.put("order_id",order_id);
            query.put("is_comment",type);
            List<OrderDetail> details = orderService.queryOrderDetailMap(query);
            for(OrderDetail detail : details){
                Goods goodObj = goodsService.queryAllGoodsById(detail.getGood_id());
                HashMap<String,Object> map = new HashMap<>();
                map.put("order_detail_id",detail.getOrder_detail_id());
                map.put("id",goodObj.getId());
                map.put("title",goodObj.getTitle());
                map.put("description",goodObj.getDescription());
                map.put("img_url",goodObj.getImg_url());
                list.add(map);
            }
        }
        return  HttpResult.ok("查询成功",list);
    }




    public HashMap<String,Object> ReturnCartNumAndPrice( List<OrderCart> cartList, int type){
        BigDecimal amount_price = new BigDecimal("0");
        for( OrderCart orderCart : cartList){
            int id = orderCart.getGood_id();
            Goods goods =  getGoodsAmountImages(id,orderCart);
            BigDecimal Original_price = new BigDecimal(Double.toString(goods.getOriginal_price()));
            BigDecimal count = new BigDecimal(goods.getAmount());
            BigDecimal decimal = Original_price.multiply(count);
            if(type == 1){
                String  content = EmojiParser.parseToUnicode( goods.getDescription());
                String title = EmojiParser.parseToUnicode(goods.getTitle());
                goods.setDescription(content);
                goods.setTitle(title);
                orderCart.setGoods(goods);
            }
            amount_price = amount_price.add(decimal);
        }
        HashMap<String, Object>  param = new HashMap<>();
        if(type == 1){
            List<HashMap<String,Object>> res =new ArrayList<>();
            for( OrderCart orderCart : cartList){
                Goods goods = orderCart.getGoods();
                HashMap<String,Object> objectHashMap = new HashMap<>();
                objectHashMap.put("good_id",orderCart.getGood_id());
                objectHashMap.put("cart_id",orderCart.getCart_id());
                objectHashMap.put("good_amount",orderCart.getGood_amount());
                objectHashMap.put("price",goods.getPrice());
                objectHashMap.put("original_price",goods.getOriginal_price());
                objectHashMap.put("title",goods.getTitle());
                objectHashMap.put("img_url",goods.getImg_url());
//                if(goods.getTitle().length()>15){
//                    objectHashMap.put("title",goods.getTitle().substring(0,15)+"...");
//                }
                objectHashMap.put("specification",goods.getSpecification().substring(0,25)+"...");
                res.add(objectHashMap);
            }
            param.put("cartList",res);
        }
        if(type == 0){
            param.put("size",cartList.size());
        }
        param.put("amount_price",new DecimalFormat("0.00").format(amount_price));
        return param;
    }
    @SneakyThrows
    @ResponseBody
    @PostMapping("/change/number")
    public HttpResult GoodsChangenNumber(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        modifyOrder_cart(param);
        List<OrderCart> cartList= orderService.queryOrderCartList(param);
        return HttpResult.ok("successfully", ReturnCartNumAndPrice(cartList,0));
    }
    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/cart/number")
    public HttpResult CartGetAllNumber(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        List<OrderCart> cartList= orderService.queryOrderCartList(param);
        return HttpResult.ok("successfully", ReturnCartNumAndPrice(cartList,0));

    }


    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/cart")
    public HttpResult CartGetAll(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        List<OrderCart> cartList= orderService.queryOrderCartList(param);
        return HttpResult.ok("successfully",ReturnCartNumAndPrice(cartList,1));
    }

    private Goods getGoodsAmountImages(int good_id, OrderCart orderCart){
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

    Goods transferUnicode( Goods goods){
        String title = EmojiParser.parseToUnicode(goods.getTitle());
        goods.setSpecification(goods.getSpecification().substring(0,15)+"...");
        goods.setTitle(title);
        return goods;
    }
    @SneakyThrows
    @ResponseBody
    @PostMapping("/change/number/return/cartList")
    public HttpResult GoodsChangenNumberReturn(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        modifyOrder_cart(param);
        List<OrderCart> cartList= orderService.queryOrderCartList(param);
        return HttpResult.ok("successfully", ReturnCartNumAndPrice(cartList,1));


    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/change/detail/number")
    public HttpResult GoodsChangenDetailNumber(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        modifyOrder_cart(param);
//        OrderCart orderCart = orderService.queryOrderCart(param);
        return HttpResult.ok("successfully");
    }
}
