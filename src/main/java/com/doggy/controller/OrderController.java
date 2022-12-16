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


    @Autowired
    private SysCouponService couponService;
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
        /***
         * 'customer_id':myApp.customer.id,
         * 'memo': myApp.memo,
         * 'payment_method': myApp.payment_method,
         * 'address':myApp.address ,
         * 'cartList':myApp.cartList,
         * 'amount_price':myApp.amount_price, //商品总价格
         * 'amount_price_discount':myApp.amount_price_discount, //现价格
         * 'discount':myApp.discount, //优惠金额
         * 'couponId':myApp.couponId,
         * 'out_trade':out_trade // 订单编号
         */

        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        OrderMaster master = new OrderMaster();
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        int payment_method = Integer.parseInt(param.get("payment_method").toString());

        String memo = param.get("memo").toString();
        CustomerAddress address = JsonUtils.jsonToPojo(param.get("address").toString(), CustomerAddress.class);
        master.setUsername(address.getUsername());
        master.setProvince(address.getProvince());
        master.setCity(address.getCity());
        master.setAddress(address.getAddress());
        master.setDistrict(address.getDistrict());
        master.setPhone(address.getPhone());

        List<HashMap> cartList = JsonUtils.jsonToList(param.get("cartList").toString(), HashMap.class);

        if (cartList.size() == 0) {
            return HttpResult.error("一些未知错误");
        }

//        String order_sn = NumberUtil.customFormatDate("yyyyMMddHHmmssSSSSSSS");
        String order_sn = param.get("out_trade").toString();

        master.setOrder_sn(order_sn);
        master.setCustomer_id(customer_id);
        master.setMemo(memo);
        master.setPayment_method(payment_method);



        /***
         * 'customer_id':myApp.customer.id,
         * 'memo': myApp.memo,
         * 'payment_method': myApp.payment_method,
         * 'address':myApp.address ,
         * 'cartList':myApp.cartList,
         * 'amount_price':myApp.amount_price, //商品总价格
         * 'amount_price_discount':myApp.amount_price_discount, //现价格
         * 'discount':myApp.discount, //总共优惠金额
         * 'couponId':myApp.couponId,
         * 'out_trade':out_trade // 订单编号
         */
        List<OrderDetail> addOrderDetail = new ArrayList<>();
        BigDecimal amount_price_discount = new BigDecimal(param.get("amount_price_discount").toString());
        BigDecimal district_money = new BigDecimal(param.get("discount").toString());
        BigDecimal amount_price = new BigDecimal(param.get("amount_price").toString());
        BigDecimal shipping_money = new BigDecimal(param.get("shipping_money").toString());

        int order_point = (int) Math.floor(amount_price_discount.doubleValue());

        for (HashMap cart : cartList) {
            OrderDetail detail = new OrderDetail();
            int good_id = Integer.parseInt(cart.get("good_id").toString());
            BigDecimal good_price = new BigDecimal(cart.get("original_price").toString());
            BigDecimal average_cost = new BigDecimal(cart.get("price").toString());
            BigDecimal fee_money = good_price.divide(average_cost,2,BigDecimal.ROUND_HALF_UP);
            int good_amount = Integer.parseInt(cart.get("good_amount").toString());
            detail.setGood_id(good_id);
            detail.setAverage_cost(average_cost.doubleValue());
            detail.setGood_price(good_price.doubleValue());
            detail.setFee_money(fee_money.doubleValue());
            detail.setGood_amount(good_amount);
            addOrderDetail.add(detail);
        }
        master.setPayment_money(amount_price_discount.doubleValue());
        master.setOrder_money(amount_price.doubleValue());
        master.setDistrict_money(district_money.doubleValue());
        // 物流信息
        master.setShipping_money(shipping_money.doubleValue());
        master.setShipping_comp_name("");
        master.setShipping_sn("");
        master.setOrder_status(0);
        // 积分

        master.setOrder_point(order_point);
        //发票抬头
        master.setInvoice_time("");
        int id = orderService.insertOrderMaster(master);
        for (OrderDetail detail : addOrderDetail) {
            detail.setOrder_id(master.getOrder_id());
            orderService.insertOrderDetail(detail);
        }
        HashMap<String, Object> map = new HashMap<>();

        // TODO Goods 总库存

        for (OrderDetail detail : addOrderDetail) {
            map.put("customer_id", customer_id);
            map.put("good_id", detail.getGood_id());
            orderService.deleteOrderCart(map);
            Goods goods = goodsService.queryAllGoodsById(detail.getGood_id());
            map.put("id", detail.getGood_id());
            map.put("sale_number", goods.getSale_number() + detail.getGood_amount() );
            goodsService.updateGoods(map);
        }

        // TODO update userinfo
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setId(customer_id);
        customerInfo.setPoints(order_point);
        customerService.updateCustomerPoints(customerInfo);
        customerInfo = customerService.queryCustomerByid(customer_id);

        int batch_id= Integer.parseInt(param.get("batch_id").toString());
        if(batch_id != -1 && batch_id != -2){
            // TODO update coupon
            HashMap<String,Object> updateMap = new HashMap<>();
            updateMap.put("batch_id",batch_id);
            updateMap.put("order_id",order_sn);
            updateMap.put("customer_id",customer_id);
            updateMap.put("status",1); // 0-未使用,1-已使用,2-已过期,3-冻结
            updateMap.put("used_time", new Date());
            couponService.updateCouponPayment(updateMap);
            // TODO update coupon_batch
            updateMap = new HashMap<>();
            updateMap.put("batch_id",batch_id);
            updateMap.put("customer_id",customer_id);
            Coupon_batch coupon_batch = couponService.queryCouponBatch(updateMap);
            updateMap = new HashMap<>();
            updateMap.put("batch_id",batch_id);
            updateMap.put("customer_id",customer_id);
            updateMap.put("used_count",coupon_batch.getUsed_count() +1);
            couponService.updateCouponBatch(updateMap);
        }
        return HttpResult.ok("successfully", customerInfo);
    }



    @SneakyThrows
    @ResponseBody
    @PostMapping("/get/orderbyId")
    public HttpResult GetCurrentOrderById(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        OrderMaster orderMaster = orderService.queryOrderMaster(param);
        HashMap<String,Object> Map = new HashMap<>();
        List<OrderDetail> details = orderService.queryOrderDetailAll(orderMaster.getOrder_id());
        for(OrderDetail detail : details){
            Goods goods = goodsService.queryAllGoodsById(detail.getGood_id());
            HashMap<String,Object> goodsMap = new HashMap<>();
            goodsMap.put("title",goods.getTitle());
            goodsMap.put("img_url",goods.getImg_url());
            detail.setGoodMap(goodsMap);
        }
        Map.put("orderMaster",orderMaster);
        Map.put("orderDetailList",details);
        return  HttpResult.ok("successfully",Map);
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

        List<OrderMaster> lists = orderService.pageQueryOrderData(page);
        List<HashMap<String,Object>> result = new ArrayList<>();
        for(OrderMaster orderMaster : lists){
            HashMap<String,Object> resultMap = new HashMap<>();
            resultMap.put("order_sn",orderMaster.getOrder_sn());
            resultMap.put("order_id",orderMaster.getOrder_id());
            resultMap.put("order_money",orderMaster.getOrder_money());
            resultMap.put("memo",orderMaster.getMemo());
            resultMap.put("order_status",orderMaster.getOrder_status());
            resultMap.put("payment_money",orderMaster.getPayment_money());
            resultMap.put("create_time",orderMaster.getCreate_time());
            List<OrderDetail> details = orderService.queryOrderDetailAll(orderMaster.getOrder_id());
            HashMap<String,Object> map = new HashMap<>();
            for(OrderDetail detail : details){
                Goods goods = goodsService.queryAllGoodsById(detail.getGood_id());
                map.put("good_amount",detail.getGood_amount());
                map.put("good_id",goods.getId());
                map.put("title",goods.getTitle());
                map.put("order_detail_id",detail.getOrder_detail_id());
                map.put("original_price",goods.getOriginal_price());
                map.put("img_url",goods.getImg_url());
                map.put("fee_money",detail.getFee_money());
                break;
            }
            resultMap.put("orderDetailList",map);
            resultMap.put("size",details.size());
            result.add(resultMap);
        }
        return  HttpResult.ok("successfully",result);
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
                map.put("title",goodObj.getTitle());
                map.put("original_price",detail.getGood_price());
                map.put("id",goodObj.getId());
                map.put("average_cost",detail.getAverage_cost());
                map.put("good_amount",detail.getGood_amount());
                map.put("img_url",goodObj.getImg_url());
                map.put("pay_time",item.getPay_time());
                map.put("order_sn",item.getOrder_sn());
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
                objectHashMap.put("flag",true);
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

    public HashMap<String,Object> ReturnCart( List<OrderCart> cartList, boolean flag,int good_id) {
        HashMap<String, Object> param = new HashMap<>();
        List<HashMap<String, Object>> res = new ArrayList<>();
        for (OrderCart orderCart : cartList) {
            int id = orderCart.getGood_id();
            Goods goods =getGoodsAmountImages(id,orderCart);
            String content = EmojiParser.parseToUnicode(goods.getDescription());
            String title = EmojiParser.parseToUnicode(goods.getTitle());

            goods.setDescription(content);
            goods.setTitle(title);
            orderCart.setGoods(goods);
            HashMap<String, Object> objectHashMap = new HashMap<>();
            objectHashMap.put("good_id", orderCart.getGood_id());
            objectHashMap.put("cart_id", orderCart.getCart_id());
            objectHashMap.put("good_amount", orderCart.getGood_amount());
            objectHashMap.put("price", goods.getPrice());
            objectHashMap.put("original_price", goods.getOriginal_price());
            objectHashMap.put("title", goods.getTitle());
            objectHashMap.put("img_url", goods.getImg_url());
            if(good_id == goods.getId()){
                objectHashMap.put("flag", flag);
            }else {
                objectHashMap.put("flag", true);
            }
//                if(goods.getTitle().length()>15){
//                    objectHashMap.put("title",goods.getTitle().substring(0,15)+"...");
//                }
            objectHashMap.put("specification", goods.getSpecification().substring(0, 25) + "...");
            res.add(objectHashMap);
        }
        param.put("cartList", res);
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

    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/cart/payment")
    public HttpResult CartGetAll_payment(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        List<OrderCart> cartList= new ArrayList<>();
        String[] ids = param.get("ids").toString().split(",");
        HashMap<String, Object> map = new HashMap<>();
        for(String id : ids){
            if (!"".equals(id)) {
                map.put("cart_id",id);
                OrderCart cart = orderService.queryOrderCart(map);
                cartList.add(cart);
            }
        }
        return HttpResult.ok("successfully",ReturnCartNumAndPrice(cartList,1));
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/good/payment")
    public HttpResult SingleGood_payment(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);

        int ids = Integer.parseInt(param.get("ids").toString());
        Goods goods =  goodsService.queryAllGoodsById(ids);
        BigDecimal Original_price = new BigDecimal(Double.toString(goods.getOriginal_price()));
        param = new HashMap<>();
        List<HashMap<String,Object>> res =new ArrayList<>();
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("good_id",ids);
        objectHashMap.put("cart_id",-1);
        objectHashMap.put("good_amount",1);
        objectHashMap.put("price",goods.getPrice());
        objectHashMap.put("original_price",goods.getOriginal_price());
        objectHashMap.put("title",goods.getTitle());
        objectHashMap.put("img_url",goods.getImg_url());
        objectHashMap.put("flag",true);
        objectHashMap.put("specification",goods.getSpecification().substring(0,25)+"...");
        res.add(objectHashMap);
        param.put("cartList",res);
        param.put("amount_price",new DecimalFormat("0.00").format(Original_price));

        return HttpResult.ok("successfully",param);
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



    @SneakyThrows
    @ResponseBody
    @PostMapping("/change/number/return/cartList")
    public HttpResult GoodsChangenNumberReturn(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        modifyOrder_cart(param);
        List<OrderCart> cartList= orderService.queryOrderCartList(param);
        int good_id = Integer.parseInt(param.get("good_id").toString());
        boolean flag = true;
        if(param.containsKey("flag")){
            flag =  (boolean) param.get("flag");
        }
        return HttpResult.ok("successfully", ReturnCart(cartList,flag,good_id));
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/addCart")
    public HttpResult addCart(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        OrderCart orderCart = orderService.queryOrderCart(param);
        HashMap<String,Object> res = new HashMap<>();
        res.put("num",-1);
        if(orderCart == null){
            param.put("add_time",new Date());
            orderService.insertOrderCart(param);
        }else {
            res.put("msg","您已添加进购物车中");
            return HttpResult.ok("successfully", res);
        }
        List<OrderCart> cartList= orderService.queryOrderCartList(param);
        res.put("num",cartList.size() );
        res.put("msg","添加成功");
        return HttpResult.ok("successfully", res);
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
