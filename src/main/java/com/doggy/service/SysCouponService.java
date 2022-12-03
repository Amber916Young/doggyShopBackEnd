package com.doggy.service;

import com.doggy.entity.Comment;
import com.doggy.entity.Coupon;
import com.doggy.entity.Coupon_batch;
import com.doggy.entity.Rule;
import com.doggy.mapper.SysCouponMapper;
import com.doggy.mapper.SysGoodsMapper;
import com.doggy.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:SysCouponService
 * @Auther: yyj
 * @Description:
 * @Date: 02/12/2022 21:07
 * @Version: v1.0
 */
@Service
public class SysCouponService {
    @Autowired
    SysCouponMapper couponMapper;

    public List<Coupon> pageQuerycCouponData(Page page) {
        return couponMapper.pageQuerycCouponData(page);
    }

    public int pageQueryCouponCount(Page page) {
        return couponMapper.pageQueryCouponCount(page);
    }

    public List<Coupon_batch> pageQueryCouponBatchData(Page page) {
        return couponMapper.pageQueryCouponBatchData(page);
    }

    public int pageQueryCouponBatchCount(Page page) {
        return couponMapper.pageQueryCouponBatchCount(page);
    }

    public List<Rule> pageQueryRuleData(Page page) {
        return couponMapper.pageQueryRuleData(page);
    }

    public int pageQueryRuleCount(Page page) {
        return couponMapper.pageQueryRuleCount(page);
    }


    public void deleteCoupons(HashMap<String, Object> map) {
        couponMapper.deleteCoupons(map);
    }

    public void deleteCouponsBatch(int batch_id) {
        couponMapper.deleteCouponsBatch(batch_id);
    }

    public void deleteRule(int rule_id) {
        couponMapper.deleteRule(rule_id);
    }

    public void insertRule(HashMap<String, Object> param) {
        couponMapper.insertRule(param);
    }

    public void updateRule(HashMap<String, Object> param) {
        couponMapper.updateRule(param);
    }

    public void insertCouponBatch(HashMap<String, Object> param) {
        couponMapper.insertCouponBatch(param);
    }

    public void updateCouponBatch(HashMap<String, Object> param) {
        couponMapper.updateCouponBatch(param);
    }

    public void insertCoupon(HashMap<String, Object> param) {
        couponMapper.insertCoupon(param);
    }

    public void updateCoupon(HashMap<String, Object> param) {
        couponMapper.updateCoupon(param);
    }

    public Coupon queryCoupon(HashMap<String, Object> map) {
       return couponMapper.queryCoupon(map);
    }
}
