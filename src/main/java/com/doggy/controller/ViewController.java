package com.doggy.controller;

import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName:ViewController
 * @Auther: yyj
 * @Description:
 * @Date: 27/11/2022 20:11
 * @Version: v1.0
 */
@Controller
@RequestMapping("view")
public class ViewController {

    @Autowired
    private SysGoodsService goodsService;

    @Autowired
    private SysOrderService orderService;

    @SneakyThrows
    @GetMapping("/dashboard")
    synchronized public String page1() {
        return "/admin/dashboard/index";
    }

    @SneakyThrows
    @GetMapping("/timingtask")
    synchronized public String timingtask_index() {
        return "/admin/TimingTask/index";
    }

    @SneakyThrows
    @GetMapping("/product")
    synchronized public String page2() {
        return "/admin/product/index";
    }
    @SneakyThrows
    @GetMapping("/product/good/index")
    synchronized public String goodIndex() {
        return "/admin/goods/index";
    }



    @SneakyThrows
    @GetMapping("/comment")
    synchronized public String comment_index() {
        return "/admin/comment/index";
    }

    @GetMapping("/coupons")
    synchronized public String coupons_index() {
        return "/admin/coupons/index";
    }

    @GetMapping("/coupons/batch")
    synchronized public String coupons_batch_index() {
        return "/admin/coupons_batch/index";
    }


    @GetMapping("/coupon/batch/addform")
    synchronized public String coupons_Batch() {
        return "/admin/coupons_batch/addform";
    }

    @GetMapping("/coupons/rule")
    synchronized public String rule_index() {
        return "/admin/coupons_rule/index";
    }

    @GetMapping("/coupons/rule/addform")
    synchronized public String coupon_rule_form() {
        return "/admin/coupons_rule/addform";
    }


    @SneakyThrows
    @GetMapping("/coupon/addform")
    synchronized public String coupon_form() {
        return "/admin/coupons/addform";
    }

    @SneakyThrows
    @GetMapping("/order")
    synchronized public String page_order() {
        return "/admin/order/index";
    }

    @SneakyThrows
    @GetMapping("/order/deliver")
    synchronized public String page_deliver() {
        return "/admin/order_deliver/index";
    }



    @SneakyThrows
    @GetMapping("/user/detail/addform")
    synchronized public String user_form() {
        return "/admin/user/addform";
    }





    @SneakyThrows
    @GetMapping("/order/detail/addform")
    synchronized public String order_detail_form() {
        return "/admin/order_detail/addform";
    }

    @SneakyThrows
    @GetMapping("/order/addform")
    synchronized public String order_form() {
        return "/admin/order/addform";
    }



    @SneakyThrows
    @GetMapping("/product/addform")
    synchronized public String page2_1() {
        return "/admin/product/addform";
    }


    @SneakyThrows
    @GetMapping("/product/goods/addform")
    synchronized public String good_Page() {
        return "/admin/goods/goodaddform";
    }

    @SneakyThrows
    @GetMapping("/user")
    synchronized public String page3() {
        return "/admin/user/index";
    }


    @SneakyThrows
    @GetMapping("/user/address")
    synchronized public String user_address() {
        return "/admin/user_address/index";
    }

    @SneakyThrows
    @GetMapping("/user/address/addform")
    synchronized public String user_form_address() {
        return "/admin/user_address/addform";
    }



}
