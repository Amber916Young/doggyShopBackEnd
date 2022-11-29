package com.doggy.controller;

import com.doggy.entity.Category;
import com.doggy.entity.Goods;
import com.doggy.entity.ImageRepo;
import com.doggy.entity.OrderDetail;
import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
import com.doggy.utils.HttpResult;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

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
    @GetMapping("/product")
    synchronized public String page2() {
        return "/admin/product/index";
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
    synchronized public String user_form(HttpServletRequest request, Model model) {
        return "/admin/user/addform";
    }



    @SneakyThrows
    @GetMapping("/order/detail/addform")
    synchronized public String order_detail_form(HttpServletRequest request, Model model) {
        return "/admin/order_detail/addform";
    }

    @SneakyThrows
    @GetMapping("/order/addform")
    synchronized public String order_form(HttpServletRequest request, Model model) {
        return "/admin/order/addform";
    }



    @SneakyThrows
    @GetMapping("/product/addform/id/{id}/type/{type}")
    synchronized public String page2_1(@PathVariable("id") int id,@PathVariable("type") String type ,Model model) {
        if(type.equals("add")) return "/admin/product/addform";

        Category category = goodsService.queryCategoryById(id);
        model.addAttribute("item",category);
        model.addAttribute("type",type);
        return "/admin/product/addform";
    }


    @SneakyThrows
    @GetMapping("/product/good/addform/id/{id}/type/{type}/cid/{cid}")
    synchronized public String good_Page(@PathVariable("cid") int cid,@PathVariable("id") int id,@PathVariable("type") String type ,Model model) {
        if(type.equals("add")) {
            model.addAttribute("category_id",cid);
            return "/admin/product/goodaddform";
        }
        Goods goods = goodsService.queryAllGoodsById(id);
        HashMap<String, Object> param = new HashMap<>();
        param.put("fid",id);
        List<ImageRepo> imgList = goodsService.queryAllImageList(param);
        StringBuilder sb = new StringBuilder();
        for (ImageRepo imageRepo : imgList){
            sb.append(imageRepo.getImg_url()+"\r");
        }
        model.addAttribute("item",goods);
        model.addAttribute("imgList",imgList);
        model.addAttribute("imgArea",sb.toString());
        model.addAttribute("type",type);
        model.addAttribute("category_id",cid);
        return "/admin/product/goodaddform";
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
