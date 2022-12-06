package com.doggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.*;
import com.doggy.service.SysCouponService;
import com.doggy.service.SysOrderService;
import com.doggy.utils.HttpResult;
import com.doggy.utils.Page;
import com.sun.tools.corba.se.idl.constExpr.Or;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @SneakyThrows
    @ResponseBody
    @PostMapping("/get/unique")
    public HttpResult getUniqueCoupon(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        Coupon_batch batch= couponService.queryCouponBatch(param);
        param.put("rule_id",batch.getRule_id());
        Rule rule= couponService.queryRule(param);
        batch.setRule(rule);
        return HttpResult.ok("可使用优惠券查询成功",batch);
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
        List<OrderCart> orderCarts = orderService.queryOrderCartList(param);
        param.put("status",0);
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
        List<Coupon_batch> res = new ArrayList<>();

        for(Coupon_batch coupon_batch : batches){
            data.put("rule_id",coupon_batch.getRule_id());
            data.put("receive_ended_at",new Date());
            Rule rule= couponService.queryRule(data);
            if(rule != null){
                data = new HashMap<>();
                // 查询用户是否已经领取 不管是否过期
                data.put("batch_id",coupon_batch.getBatch_id());
                Coupon coupon = couponService.queryCoupon(data);
                if(coupon == null){
                    String  use_started_at = rule.getUse_started_at().substring(0,10);
                    String  use_ended_at = rule.getUse_ended_at().substring(0,10);
                    rule.setUse_ended_at(use_ended_at);
                    rule.setUse_started_at(use_started_at);
                    DecimalFormat format = new DecimalFormat("#.00");
                    String str = format.format(rule.getAmount());
                    String str2 = format.format(rule.getDiscount());
                    rule.setAmount(Double.parseDouble(str));
                    rule.setDiscount(Double.parseDouble(str2));
                    coupon_batch.setRule(rule);
                    res.add(coupon_batch);
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
        List<Coupon> res = new ArrayList<>();

        for(Coupon coupon : couponList){
            data = new HashMap<>();
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

                res.add(coupon);
            }
        }

        return HttpResult.ok("查询成功",res);
    }

}
