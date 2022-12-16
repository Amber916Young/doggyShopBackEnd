package com.doggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.*;
import com.doggy.service.SysCouponService;
import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
import com.doggy.utils.HttpResult;
import com.doggy.utils.Page;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @ClassName:CouponsController
 * @Auther: yyj
 * @Description:
 * @Date: 02/12/2022 14:52
 * @Version: v1.0
 */
@RestController
@RequestMapping("coupon")
public class CouponsController {

    @Autowired
    private SysCouponService couponService;
    @Autowired
    private SysOrderService orderService;
    @Autowired
    private SysGoodsService goodsService;

    @SneakyThrows
    @ResponseBody
    @PostMapping("/get/coupon/number")
    synchronized public HttpResult getCouponNnumber(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        param.put("status",0);

        List<Coupon> couponList = couponService.queryAllCouponCustomer(param);
        int count = 0;
        for(Coupon coupon : couponList){
            HashMap<String, Object> data = new HashMap<>();
            // 查询优惠券批次
            data.put("batch_id",coupon.getBatch_id());
            Coupon_batch batch= couponService.queryCouponBatch(data);
            // 查询规则
            data.put("rule_id",batch.getRule_id());
            // 是否过期 TODO 定时任务
            data.put("use_ended_at",new Date());
            Rule rule= couponService.queryRule(data);
            if(rule == null) {
                //过期了 更新Coupon表 没有使用但是过期了
                HashMap<String,Object> updateMap = new HashMap<>();
                updateMap.put("coupon_id",coupon.getCoupon_id());
                updateMap.put("status",2); // 更新已经过期
                couponService.updateCoupon(updateMap);
            }else {
                count++;
            }
        }
        return HttpResult.ok("优惠券数量查询成功",count);
    }



    @SneakyThrows
    @ResponseBody
    @PostMapping("/get/unique")
    synchronized public HttpResult getUniqueCoupon(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        Coupon_batch batch= couponService.queryCouponBatch(param);
        String type = param.get("type").toString();
        String[] ids = param.get("ids").toString().split(",");
        List<OrderCart> cartList = new ArrayList<>();

        if("cart".equals(type)){
            HashMap<String, Object> map = new HashMap<>();
            for(String id : ids){
                if (!"".equals(id)) {
                    map.put("cart_id",id);
                    OrderCart cart = orderService.queryOrderCart(map);
                    cartList.add(cart);
                }
            }
        }else {
            int id = Integer.parseInt(param.get("ids").toString());
            OrderCart tmp = new OrderCart();
            tmp.setGood_amount(1);
            tmp.setGood_id(id);
            cartList.add(tmp);
        }

        param.put("rule_id",batch.getRule_id());
        Rule rule= couponService.queryRule(param);
        HashMap<String,Object> Map = new HashMap<>();
        Map.put("discount",rule.getDiscount());
        Map.put("threshold",rule.getThreshold());
        Map.put("amount",rule.getAmount());
        Map.put("type",rule.getType());
        Map.put("use_range",rule.getUse_range());
        batch.setRuleMap(Map);
        HashMap<String, Object> result =  ReturnCartNumAndPrice(cartList,rule);

        Map = new HashMap<>();
        Map.put("batch_id",batch.getBatch_id());
        Map.put("batch_name",batch.getBatch_name());
        Map.put("coupon_name",batch.getCoupon_name());
        Map.put("rule_id",batch.getRule_id());
        Map.put("rule",batch.getRuleMap());
        result.put("batch",Map);



        return HttpResult.ok("优惠查询成功",result);
    }


    synchronized public HashMap<String,Object> ReturnCartNumAndPrice( List<OrderCart> cartList, Rule rule) {
        int rule_type = rule.getType(); //优惠卷类型, 0-满减, 1-折扣 2 直减
        int use_range = rule.getUse_range(); //使用范围，0—全场，1—商品
        Set<Integer> set = new HashSet<>();
        Set<Integer> setAll = new HashSet<>();
        Set<Integer> goodsIdSet = new HashSet<>();
        Map<Integer, Goods> goodsMap = new HashMap<>();
        if (use_range == 1) {
            String goods_list = rule.getGoods_list().replaceAll("\\[", "").replaceAll("\\]", "");
            String[] goods_ids = goods_list.split(",");
            for (String id : goods_ids) {
                id = id.trim();
                if (id != null || id.equals("") || id.length() > 0) {
                    set.add(Integer.parseInt(id));
                }
            }
        }
        BigDecimal match_amount_price = new BigDecimal("0");
        BigDecimal match_amount_price_all = new BigDecimal("0");

        Map<Integer, BigDecimal> countMap = new HashMap<>();

        for (OrderCart orderCart : cartList) {
            int id = orderCart.getGood_id();
            Goods goods = goodsService.queryAllGoodsById(id);
            setAll.add(id);
            goodsMap.put(id, goods);
            BigDecimal tmp = new BigDecimal(Double.toString(goods.getOriginal_price()));
            BigDecimal count = new BigDecimal(orderCart.getGood_amount());
            tmp = tmp.multiply(count);
            countMap.put(id, count);
            // 符合条件的
            if (!set.isEmpty()) {
                if (set.contains(id)) {
                    goodsIdSet.add(id);
                    match_amount_price = match_amount_price.add(tmp);
                }
            }
            match_amount_price_all = match_amount_price_all.add(tmp);
            orderCart.setGoods(goods);

        }

        BigDecimal amount = new BigDecimal(Double.toString(rule.getAmount()));
        BigDecimal discount = new BigDecimal(Double.toString(rule.getDiscount() / 10.0));
        BigDecimal threshold = new BigDecimal(Double.toString(rule.getThreshold()));
        boolean flag = false;
        if (rule_type == 1) { //优惠卷类型, 0-满减, 1-折扣 2 直减
            if (use_range == 0) {
                amount = match_amount_price_all.multiply(discount);
            }else{
                amount = match_amount_price.multiply(discount);
            }
        }
        if (use_range == 1) {
            for (int gid : goodsIdSet) {
                Goods goods = goodsMap.get(gid);
                BigDecimal count = countMap.get(gid);
                BigDecimal org = new BigDecimal(Double.toString(goods.getOriginal_price()));
                BigDecimal total_original_price = org.multiply(count);
                BigDecimal ratio = total_original_price.divide(match_amount_price, 8, BigDecimal.ROUND_HALF_UP);
                BigDecimal disMoney = ratio.multiply(amount);
                BigDecimal div = total_original_price.subtract(disMoney);
                BigDecimal newPrice = div.divide(count, 2, BigDecimal.ROUND_HALF_UP);
                //优惠卷类型, 0-满减, 1-折扣 2 直减
                if (rule_type == 0) {
                    if (match_amount_price.compareTo(threshold) < 0) {
                        flag =true;
                        break;
                    } else {
                        goods.setPrice(newPrice.doubleValue());
                    }
                } else if (rule_type == 1) {
                    goods.setPrice(org.subtract(newPrice).doubleValue());
                } else if (rule_type == 2) {
                    goods.setPrice(newPrice.doubleValue());
                }
            }
        } else {
            for (int gid : setAll) {
                Goods goods = goodsMap.get(gid);
                BigDecimal count = countMap.get(gid);
                BigDecimal org = new BigDecimal(Double.toString(goods.getOriginal_price()));
                BigDecimal total_original_price = org.multiply(count);

                BigDecimal ratio = total_original_price
                        .divide(match_amount_price_all, 8, BigDecimal.ROUND_HALF_UP);
                BigDecimal disMoney = ratio.multiply(amount);
                BigDecimal div = total_original_price.subtract(disMoney);
                BigDecimal newPrice = div.divide(count, 2, BigDecimal.ROUND_HALF_UP);

                //优惠卷类型, 0-满减, 1-折扣 2 直减
                if (rule_type == 0) {
                    if (match_amount_price_all.compareTo(threshold) < 0) {
                        flag =true;
                        break;
                    } else {
                        goods.setPrice(newPrice.doubleValue());
                    }
                } else if (rule_type == 1) {
                    goods.setPrice(org.subtract(newPrice).doubleValue());
                } else if (rule_type == 2) {
                    goods.setPrice(newPrice.doubleValue());

                }
            }
        }


        BigDecimal Discount = new BigDecimal("0");
        BigDecimal amount_price = new BigDecimal("0");
        BigDecimal amount_price_discount = new BigDecimal("0");

        List<HashMap<String, Object>> res = new ArrayList<>();
        for (OrderCart orderCart : cartList) {
            Goods goods = orderCart.getGoods();
            HashMap<String, Object> objectHashMap = new HashMap<>();
            objectHashMap.put("good_id", orderCart.getGood_id());
            objectHashMap.put("good_amount", orderCart.getGood_amount());
            objectHashMap.put("price", goods.getPrice());
            objectHashMap.put("original_price", goods.getOriginal_price());
            BigDecimal count = new BigDecimal(orderCart.getGood_amount());
            BigDecimal org = new BigDecimal(Double.toString(goods.getOriginal_price()));
            BigDecimal price = new BigDecimal(Double.toString(goods.getPrice()));
            BigDecimal total_original_price = org.multiply(count).setScale(2, BigDecimal.ROUND_HALF_UP);
            objectHashMap.put("total_original_price", total_original_price);
            BigDecimal total_price = price.multiply(count).setScale(2, BigDecimal.ROUND_HALF_UP);
            objectHashMap.put("total_price", total_price);
            amount_price = amount_price.add(total_original_price);
            amount_price_discount = amount_price_discount.add(total_price);
            Discount = Discount.add(total_original_price.subtract(total_price));
            res.add(objectHashMap);
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("priceList", res);
        if(flag){
            param.put("flag", "未满足优惠最低金额");
        }
        param.put("amount_price", amount_price.setScale(2, RoundingMode.HALF_UP));
        param.put("amount_price_discount", amount_price_discount.setScale(2, RoundingMode.HALF_UP));
        param.put("discount", Discount.setScale(2, RoundingMode.HALF_UP));
        return param;
    }


    /**
     * 查询当前订单可以使用的优惠券
     * @param jsonData get/unique
     * @return
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/collection/canuse")
    public HttpResult getAllCouponsWhichCanuseInCurrentOrder(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        param.put("status",0);
        String type = param.get("type").toString();
        List<OrderCart> orderCarts = new ArrayList<>();
        String[] ids = param.get("ids").toString().split(",");

        if("cart".equals(type)){
            HashMap<String, Object> map = new HashMap<>();
            for(String id : ids){
                if (!"".equals(id)) {
                    map.put("cart_id",id);
                    OrderCart cart = orderService.queryOrderCart(map);
                    orderCarts.add(cart);
                }
            }
        }else {
            int id = Integer.parseInt(param.get("ids").toString());
            OrderCart tmp = new OrderCart();
            tmp.setGood_amount(1);
            tmp.setGood_id(id);
            orderCarts.add(tmp);
        }

        List<Coupon> couponList = couponService.queryAllCouponCustomer(param);
        List<Coupon> res = new ArrayList<>();
        for(Coupon coupon : couponList){
            HashMap<String, Object> data = new HashMap<>();

            // 查询优惠券批次
            data.put("batch_id",coupon.getBatch_id());
            Coupon_batch batch= couponService.queryCouponBatch(data);
            // 查询规则
            data.put("rule_id",batch.getRule_id());
            // 是否过期 TODO 定时任务
            data.put("use_ended_at",new Date());
            Rule rule= couponService.queryRule(data);
            if(rule == null) {
                //过期了 更新Coupon表 没有使用但是过期了
                HashMap<String,Object> updateMap = new HashMap<>();
                updateMap.put("coupon_id",coupon.getCoupon_id());
                updateMap.put("status",2); // 更新已经过期
                couponService.updateCoupon(updateMap);
            }else {
                String  use_started_at = rule.getUse_started_at().substring(0,10);
                String  use_ended_at = rule.getUse_ended_at().substring(0,10);
                rule.setUse_ended_at(use_ended_at);
                rule.setUse_started_at(use_started_at);
                DecimalFormat format = new DecimalFormat("#.00");
                String str = format.format(rule.getAmount());
                String str2 = format.format(rule.getDiscount());
                rule.setAmount(Double.parseDouble(str));
                rule.setDiscount(Double.parseDouble(str2));
                coupon.setRule(rule);
                coupon.setCoupon_batch(batch);
                if(rule.getUse_range() == 0){
                    res.add(coupon);
                    continue;
                }
                // 验证当前coupon是否符合
                if(isMacth(rule,orderCarts)){
                    res.add(coupon);
                }
            }
        }
        return HttpResult.ok("可使用优惠券查询成功",res);
    }

    private boolean isMacth(Rule rule, List<OrderCart> orderCarts) {
        String goods_list = rule.getGoods_list().replaceAll("\\[","").replaceAll("\\]","");
        String[] goods_ids = goods_list.split(",");
        Set<Integer> set = new HashSet<>();
        for(String id : goods_ids){
            id = id.trim();
            if(id != null || id.equals("") || id.length()>0){
                set.add(Integer.parseInt(id));
            }
        }
        for(OrderCart cart : orderCarts){
            if(set.contains(cart.getGood_id())){
                return true;
            }
        }
        return false;
    }


    /**
     * 查询可以领取的优惠券
     * 注意如果此优惠券已经被领取过，那么从集合中删除
     * @param jsonData
     * @return
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping("/getOne")
    public HttpResult getOneCoupon(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        Page page = new Page();
        int limit = 5;
        HashMap<String,Object> data = new HashMap<>();
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        page.setData(data);
        page.setRows(limit);
        page.setPage(1);
        int start = page.getStart();
        page.setStart(start);
        page.setId(customer_id);
        //status 0-未使用,1-已使用,2-已过期,3-冻结
        data.put("status",0);
        List<Coupon> couponList = couponService.queryCouponCustomerMap(page);
        // 查询已经领取过并且可用的优惠券
        data = new HashMap<>();
        for(Coupon coupon : couponList){
            // 查询优惠券批次
            data.put("batch_id",coupon.getBatch_id());
            Coupon_batch batch= couponService.queryCouponBatch(data);
            // 查询规则
            data.put("rule_id",batch.getRule_id());
            // 是否过期 TODO 定时任务
            data.put("use_ended_at",new Date());
            Rule rule= couponService.queryRule(data);
            if(rule == null) {
                //过期了 更新Coupon表 没有使用但是过期了
                HashMap<String,Object> updateMap = new HashMap<>();
                updateMap.put("coupon_id",coupon.getCoupon_id());
                updateMap.put("status",2); // 更新已经过期
                couponService.updateCoupon(updateMap);
            }else {
                String  use_started_at = rule.getUse_started_at().substring(0,10);
                String  use_ended_at = rule.getUse_ended_at().substring(0,10);
                rule.setUse_ended_at(use_ended_at);
                rule.setUse_started_at(use_started_at);
                coupon.setRule(rule);
                coupon.setCoupon_batch(batch);
                return HttpResult.ok("查询成功",coupon);
            }
        }
        return HttpResult.ok("暂无可用优惠券");
    }



    /**
     * 领取的优惠券 修改状态
     * @param jsonData { coupon_id }
     * @return
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping("/collect")
    public HttpResult collectCoupons(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        try {
            // 更新
            Coupon_batch batch = couponService.queryCouponBatch(param);
            int assign_count = batch.getAssign_count();
            int total_count = batch.getTotal_count();
            if(assign_count == total_count){
                return HttpResult.error("优惠券已经领取完了～");
            }
            param.put("assign_count",assign_count+1);
            couponService.updateCouponBatch(param);
            // 新增
            couponService.insertCoupon(param);
            return HttpResult.ok("领取成功");
        }catch (Exception e){
            return HttpResult.error(e.toString());
        }
    }


    /**
     * 查询可以领取的优惠券
     * 只显示领取开始时间 receive_started_at 到 领取结束时间 receive_ended_at
     * @param jsonData
     * @return
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/collection")
    public HttpResult getAllCouponsCanCollection(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        Page page = new Page();
        int limit = 20;
        int curr = Integer.parseInt(param.get("currNo").toString());
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        limit = Integer.parseInt(param.get("limit").toString());
        page.setRows(limit);
        page.setPage(curr);
        int start = page.getStart();
        page.setStart(start);
        //status 0-未使用,1-已使用,2-已过期,3-冻结
        List<Coupon_batch> batches =  couponService.querycCouponBatchMap(page);
        HashMap<String,Object> data = new HashMap<>();
        // O(n) 复杂度
        List<HashMap<String,Object>> res = new ArrayList<>();
        for(Coupon_batch coupon_batch : batches){
            data.put("rule_id",coupon_batch.getRule_id());
            data.put("receive_ended_at",new Date());
            Rule rule= couponService.queryRule(data);
            if(rule != null){
                data = new HashMap<>();
                // 查询用户是否已经领取 不管是否过期
                data.put("batch_id",coupon_batch.getBatch_id());
                data.put("customer_id",customer_id);
                Coupon coupon = couponService.queryCoupon(data);
                if(coupon == null){
                    String  use_started_at = rule.getUse_started_at().substring(0,10);
                    String  use_ended_at = rule.getUse_ended_at().substring(0,10);
                    DecimalFormat format = new DecimalFormat("#.00");
                    String str = format.format(rule.getAmount());
                    String str2 = format.format(rule.getDiscount());
                    rule.setAmount(Double.parseDouble(str));
                    rule.setDiscount(Double.parseDouble(str2));

                    HashMap<String,Object> Map = new HashMap<>();
                    Map.put("discount",rule.getDiscount());
                    Map.put("threshold",rule.getThreshold());
                    Map.put("amount",rule.getAmount());
                    Map.put("type",rule.getType());
                    Map.put("use_range",rule.getUse_range());
                    Map.put("use_started_at",use_started_at);
                    Map.put("use_ended_at",use_ended_at);

                    HashMap<String,Object> batchMap = new HashMap<>();
                    batchMap.put("batch_id",coupon_batch.getBatch_id());
                    batchMap.put("coupon_name",coupon_batch.getCoupon_name());
                    batchMap.put("rule_id",coupon_batch.getRule_id());
                    batchMap.put("rule",Map);
                    res.add(batchMap);
                }
            }
        }
        return HttpResult.ok("查询成功",res);
    }


    /**
     * 查询可以领取的优惠券
     * 注意如果此优惠券已经被领取过，那么从集合中删除
     * @param jsonData
     * @return
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/customer/collection")
    public HttpResult getAllCoupons(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        Page page = new Page();
        int limit = 20;
        HashMap<String,Object> data = new HashMap<>();
        int curr = Integer.parseInt(param.get("currNo").toString());
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        limit = Integer.parseInt(param.get("limit").toString());
        page.setData(data);
        page.setRows(limit);
        page.setPage(curr);
        int start = page.getStart();
        page.setStart(start);
        page.setId(customer_id);
        //status 0-未使用,1-已使用,2-已过期,3-冻结
        data.put("status",0);

        List<Coupon> couponList = couponService.queryCouponCustomerMap(page);
        // 查询已经领取过并且可用的优惠券
        List<HashMap<String,Object>> res = new ArrayList<>();

        for(Coupon coupon : couponList){
            data = new HashMap<>();
            // 查询优惠券批次
            data.put("batch_id",coupon.getBatch_id());
            Coupon_batch coupon_batch= couponService.queryCouponBatch(data);
            // 查询规则
            data.put("rule_id",coupon_batch.getRule_id());
            // 是否过期 TODO 定时任务
            data.put("use_ended_at",new Date());
            Rule rule= couponService.queryRule(data);
            if(rule == null) {
                //过期了 更新Coupon表 没有使用但是过期了
                HashMap<String,Object> updateMap = new HashMap<>();
                updateMap.put("coupon_id",coupon.getCoupon_id());
                updateMap.put("status",2); // 更新已经过期
                couponService.updateCoupon(updateMap);
            }else {

                String  use_started_at = rule.getUse_started_at().substring(0,10);
                String  use_ended_at = rule.getUse_ended_at().substring(0,10);
                DecimalFormat format = new DecimalFormat("#.00");
                String str = format.format(rule.getAmount());
                String str2 = format.format(rule.getDiscount());
                rule.setAmount(Double.parseDouble(str));
                rule.setDiscount(Double.parseDouble(str2));


                HashMap<String,Object> Map = new HashMap<>();
                Map.put("discount",rule.getDiscount());
                Map.put("threshold",rule.getThreshold());
                Map.put("amount",rule.getAmount());
                Map.put("type",rule.getType());
                Map.put("use_range",rule.getUse_range());
                Map.put("use_started_at",use_started_at);
                Map.put("use_ended_at",use_ended_at);


                HashMap<String,Object> batchMap = new HashMap<>();
                batchMap.put("batch_id",coupon_batch.getBatch_id());
                batchMap.put("coupon_name",coupon_batch.getCoupon_name());
                batchMap.put("rule_id",coupon_batch.getRule_id());
                batchMap.put("rule",Map);
                res.add(batchMap);
            }
        }

        return HttpResult.ok("查询成功",res);
    }

}
