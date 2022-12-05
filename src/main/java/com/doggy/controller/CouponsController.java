package com.doggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Coupon;
import com.doggy.entity.Coupon_batch;
import com.doggy.entity.OrderMaster;
import com.doggy.entity.Rule;
import com.doggy.service.SysCouponService;
import com.doggy.service.SysOrderService;
import com.doggy.utils.HttpResult;
import com.doggy.utils.Page;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
        param.put("status",2); // 更新已经过期
        try {
            couponService.updateCoupon(param);
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
                    coupon_batch.setRule(rule);
                }
            }
        }
        // 删除不符合的
        List<Coupon_batch> res = new ArrayList<>();
        for(Coupon_batch batch : batches){
            if(batch.getRule() != null){
                res.add(batch);
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

        // use_ended_at 根据使用结束时间正序查询
        List<Coupon> couponList = couponService.querycCouponCustomerMap(page);
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
                coupon.setRule(rule);
                coupon.setCoupon_batch(batch);
            }
        }

        // 删除不符合的
        List<Coupon> res = new ArrayList<>();
        for(Coupon coupon : couponList){
            if(coupon.getCoupon_batch() != null){
                res.add(coupon);
            }
        }
        return HttpResult.ok("查询成功",res);
    }

}
