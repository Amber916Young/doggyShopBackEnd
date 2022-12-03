package com.doggy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Category;
import com.doggy.entity.CustomerAddress;
import com.doggy.entity.CustomerInfo;
import com.doggy.service.CustomerService;
import com.doggy.service.SysAddressService;
import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
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
 * @ClassName:WebProductController
 * @Auther: yyj
 * @Description:
 * @Date: 27/11/2022 21:37
 * @Version: v1.0
 */
@RestController
@RequestMapping("web/user")
public class WebUserController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private SysAddressService addressService;

    @Value("${Delcode.key}")
    private String Delcode;

    @SneakyThrows
    @ResponseBody
    @GetMapping("/queryAll")
    public HttpResult queryAll(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        page.setRows(limit);
        page.setKeyWord(keyword);
        List<CustomerInfo> lists = customerService.pageQueryUserData(page);
        int totals=customerService.pageQueryUserCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }
    @SneakyThrows
    @ResponseBody
    @GetMapping("/address/queryAll")
    public HttpResult address_queryAll(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        page.setRows(limit);
        page.setKeyWord(keyword);
        List<CustomerAddress> lists = customerService.pageQueryAddressData(page);
        int totals=customerService.pageQueryAddressCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/detail")
    public HttpResult UserDetail(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        String customer_id = param.get("customer_id").toString();
        CustomerInfo customer = customerService.queryCustomerByid(Integer.parseInt(customer_id));
        return HttpResult.ok("查询成功",customer);
    }
    @SneakyThrows
    @ResponseBody
    @PostMapping("/address/update/flag")
    public HttpResult address_update_flag(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        try {
            addressService.updateAddress(param);
            return HttpResult.ok("更新成功");
        }catch (Exception e){
            return HttpResult.error(e.getMessage());
        }
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/address/update")
    public HttpResult address_update(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        try {
            addressService.updateAddress(param);
            return HttpResult.ok("更新成功");
        }catch (Exception e){
            return HttpResult.error(e.getMessage());
        }
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/address/detail")
    public HttpResult addressDetail(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        String customer_addr_id = param.get("customer_addr_id").toString();
        CustomerAddress address = addressService.queryAddressByid(Integer.parseInt(customer_addr_id));
        return HttpResult.ok("查询成功",address);
    }


    @SneakyThrows
    @ResponseBody
    @PostMapping("/address/deletes")
    public HttpResult address_delete(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        JSONObject json = JSONObject.parseObject(jsonData);
        JSONArray ids = json.getJSONArray("ids");
        String code = json.getString("code");
        if (code.equals(Delcode)) {
            HashMap<String, Object> param = new HashMap<>();
            for (int i = 0; i < ids.size(); i++) {
                param.put("customer_addr_id",ids.get(i).toString());
                addressService.deleteAddress(param);
            }
            return HttpResult.ok("successfully");
        } else {
            return  HttpResult.error("Code is wrong，delete fail!");
        }
    }




}
