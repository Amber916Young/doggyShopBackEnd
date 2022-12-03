package com.doggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.CustomerInfo;
import com.doggy.entity.OrderDetail;
import com.doggy.entity.OrderMaster;
import com.doggy.service.CustomerService;
import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
import com.doggy.utils.HttpResult;
import com.doggy.utils.Page;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:WebProductController
 * @Auther: yyj
 * @Description:
 * @Date: 27/11/2022 21:37
 * @Version: v1.0
 */
@RestController
@RequestMapping("web/order")
public class WebOrderController {
    @Autowired
    private SysOrderService orderService;

    @Autowired
    private SysGoodsService goodsService;


    @SneakyThrows
    @ResponseBody
    @PostMapping("/master/detail")
    public HttpResult masterDetail(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        OrderMaster orderMaster = orderService.queryOrderMaster(param);
        return HttpResult.ok("查询成功",orderMaster);
    }




    @SneakyThrows
    @ResponseBody
    @PostMapping("/master/deletes")
    public HttpResult masterDeletesl(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        OrderMaster orderMaster = orderService.queryOrderMaster(param);
        return HttpResult.ok("查询成功",orderMaster);
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping("/detail/queryAll")
    public HttpResult detailqueryAll(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        HashMap<String,Object> map = new HashMap<>();
        String order_id = request.getParameter("order_id");
        page.setRows(limit);
        map.put("order_id",order_id);
        page.setKeyWord(keyword);
        page.setData(map);
        List<HashMap<String,Object>> lists = orderService.pageQueryOrderDetailData(page);
        for(HashMap<String,Object> obj : lists){
            String good_name = goodsService.queryAllGoodsById(Integer.parseInt(obj.get("good_id").toString())).getTitle();
            obj.put("good_name",good_name);
        }
        int totals=orderService.pageQueryDetailCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }





    @SneakyThrows
    @ResponseBody
    @GetMapping("/queryAll")
    public HttpResult queryAll(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        //订单状态 0未发货 1 已发货 2已完成
        String order_status = request.getParameter("order_status") == null ? "-1": request.getParameter("order_status");
        String payment_method = request.getParameter("payment_method");
        String status = null;
        page.setRows(limit);
        switch (order_status){
            case "未发货":
                status = "0";
                break;
            case "已发货":
                status = "1";
                break;
            case "已完成":
                status = "2";
                break;
            default:
                break;
        }
        if(payment_method == null || payment_method.equals("") ){
            payment_method =null;
        }

        page.setKeyWord(keyword);
        HashMap<String,Object> map = new HashMap<>();
        map.put("order_status",status);
        map.put("payment_method",payment_method);
        page.setData(map);
        List<OrderMaster> lists = orderService.pageQueryOrderData(page);
        int totals=orderService.pageQueryOrderCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }



}
