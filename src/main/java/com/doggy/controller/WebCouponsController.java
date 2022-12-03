package com.doggy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.*;
import com.doggy.service.*;
import com.doggy.utils.HttpResult;
import com.doggy.utils.Page;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
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
@RequestMapping("web/coupon")
public class WebCouponsController {
    @Autowired
    private SysCommentService commentService;
    @Autowired
    private SysGoodsService goodsService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SysOrderService orderService;
    @Autowired
    private SysCouponService couponService;
    @Value("${Delcode.key}")
    private String Delcode;

    @SneakyThrows
    @ResponseBody
    @GetMapping("/queryAll")
    public HttpResult couponQueryAll(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        page.setKeyWord(keyword);
        page.setRows(limit);
        List<Coupon> lists = couponService.pageQuerycCouponData(page);
        int totals=couponService.pageQueryCouponCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping("/detail")
    public HttpResult detailCoupon(HttpServletRequest request) {
        String coupon_id = request.getParameter("coupon_id");
        HashMap<String,Object> map = new HashMap<>();
        map.put("coupon_id",coupon_id);
        Coupon coupon = couponService.queryCoupon(map);
        return HttpResult.ok("查询成功", coupon);
    }



    @SneakyThrows
    @ResponseBody
    @PostMapping("/add")
    public HttpResult  couponAdd(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        couponService.insertCoupon(param);
        return HttpResult.ok("新增成功");
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/edit")
    public HttpResult  couponEdit(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        couponService.updateCoupon(param);
        return HttpResult.ok("新增成功");
    }
    @SneakyThrows
    @ResponseBody
    @GetMapping("/batch/queryAll")
    public HttpResult couponBatchQueryAll(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        page.setKeyWord(keyword);
        page.setRows(limit);
        List<Coupon_batch> lists = couponService.pageQueryCouponBatchData(page);
        int totals=couponService.pageQueryCouponBatchCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }


    @SneakyThrows
    @ResponseBody
    @GetMapping("/rule/queryAll")
    public HttpResult ruleQueryAll(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        page.setKeyWord(keyword);
        page.setRows(limit);
        List<Rule> lists = couponService.pageQueryRuleData(page);
        int totals=couponService.pageQueryRuleCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/deletes")
    public HttpResult coupon_delete(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        JSONObject json = JSONObject.parseObject(jsonData);
        JSONArray ids = json.getJSONArray("ids");
        String code = json.getString("code");
        HashMap<String,Object> map = new HashMap<>();
        if (code.equals(Delcode)) {
            for (int i = 0; i < ids.size(); i++) {
                map.put("coupon_id",Integer.parseInt(ids.get(i).toString()));
                couponService.deleteCoupons(map);
            }
            return HttpResult.ok("successfully");
        }
        return  HttpResult.error("Code is wrong，delete fail!");
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/batch/deletes")
    public HttpResult couponBatch_delete(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        JSONObject json = JSONObject.parseObject(jsonData);
        JSONArray ids = json.getJSONArray("ids");
        String code = json.getString("code");
        HashMap<String,Object> map = new HashMap<>();
        if (code.equals(Delcode)) {
            for (int i = 0; i < ids.size(); i++) {
                int batch_id = Integer.parseInt(ids.get(i).toString());
                map.put("batch_id",batch_id);
                couponService.deleteCoupons(map);
                couponService.deleteCouponsBatch(batch_id);
            }
            return HttpResult.ok("successfully");
        }
        return  HttpResult.error("Code is wrong，delete fail!");
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/batch/add")
    public HttpResult  couponBatchAdd(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        couponService.insertCouponBatch(param);
        return HttpResult.ok("新增成功");
    }


    @SneakyThrows
    @ResponseBody
    @PostMapping("/batch/edit")
    public HttpResult couponBatchEdit(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        couponService.updateCouponBatch(param);
        return HttpResult.ok("修改成功");
    }



    @SneakyThrows
    @ResponseBody
    @PostMapping("/rule/deletes")
    public HttpResult rule_delete(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        JSONObject json = JSONObject.parseObject(jsonData);
        JSONArray ids = json.getJSONArray("ids");
        String code = json.getString("code");
        HashMap<String,Object> map = new HashMap<>();
        if (code.equals(Delcode)) {
            for (int i = 0; i < ids.size(); i++) {
                int rule_id = Integer.parseInt(ids.get(i).toString());
                couponService.deleteRule(rule_id);
            }
            return HttpResult.ok("successfully");
        }
        return  HttpResult.error("Code is wrong，delete fail!");
    }



    @SneakyThrows
    @ResponseBody
    @PostMapping("/rule/add")
    public HttpResult ruleAdd(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        couponService.insertRule(param);
        return HttpResult.ok("新增成功");
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/rule/edit")
    public HttpResult ruleEdit(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        couponService.updateRule(param);
        return HttpResult.ok("修改成功");
    }




}
