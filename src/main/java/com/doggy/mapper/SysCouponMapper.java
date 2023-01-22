package com.doggy.mapper;

import com.doggy.entity.Comment;
import com.doggy.entity.Coupon;
import com.doggy.entity.Coupon_batch;
import com.doggy.entity.Rule;
import com.doggy.utils.Page;
import org.apache.ibatis.annotations.Delete;

import java.util.HashMap;
import java.util.List;

public interface SysCouponMapper {
    List<Coupon> pageQuerycCouponData(Page page);

    int pageQueryCouponCount(Page page);

    List<Coupon_batch> pageQueryCouponBatchData(Page page);

    int pageQueryCouponBatchCount(Page page);

    List<Rule> pageQueryRuleData(Page page);

    int pageQueryRuleCount(Page page);

    void deleteCoupons(HashMap<String, Object> map);

    void deleteCouponsBatch(int batch_id);

    void deleteRule(int rule_id);

    void insertRule(HashMap<String, Object> param);

    void updateRule(HashMap<String, Object> param);

    void insertCouponBatch(HashMap<String, Object> param);

    void updateCouponBatch(HashMap<String, Object> param);

    void insertCoupon(HashMap<String, Object> param);

    void updateCoupon(HashMap<String, Object> param);

    Coupon queryCoupon(HashMap<String, Object> map);

    Coupon_batch queryCouponBatch(HashMap<String, Object> map);

    Rule queryRule(HashMap<String, Object> map);

    List<Rule> queryRuleAll(HashMap<String, Object> map);

    List<Coupon> queryCouponCustomerMap(Page page);

    List<Coupon_batch> querycCouponBatchMap(Page page);

    List<Coupon> queryAllCouponCustomer(HashMap<String, Object> param);

    void updateCouponPayment(HashMap<String, Object> param);

    Rule queryRuleOutDate(HashMap<String, Object> data);
}
