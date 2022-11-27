package com.doggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Category;
import com.doggy.entity.CustomerAddress;
import com.doggy.entity.Goods;
import com.doggy.entity.OrderCart;
import com.doggy.service.SysAddressService;
import com.doggy.service.SysGoodsService;
import com.doggy.utils.HttpResult;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:AddressController
 * @Auther: yyj
 * @Description:
 * @Date: 23/11/2022 15:35
 * @Version: v1.0
 */
@RestController
@RequestMapping("address")
public class AddressController {

    @Autowired
    private SysAddressService addressService;

    @SneakyThrows
    @ResponseBody
    @PostMapping("/get/unique")
    public HttpResult GetUnqiueAddress(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        int customer_addr_id = Integer.parseInt(param.get("customer_addr_id").toString());
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        param = new HashMap<>();
        if( customer_addr_id == -1){
            param.put("customer_id",customer_id);
            param.put("is_default",0);
        }else {
            param.put("customer_id",customer_id);
            param.put("customer_addr_id",customer_addr_id);
        }
        CustomerAddress customerAddress = addressService.queryAddressByParam(param);
        if(customerAddress ==null){
            param = new HashMap<>();
            param.put("customer_id",customer_id);
            customerAddress = addressService.queryAddressByParam(param);
        }
        return HttpResult.ok("successfully", customerAddress);
    }
    /**
     *
     * @param jsonData
     * uuid
     * @return
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping("/get/all")
    public HttpResult GetAllAddress(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        List<CustomerAddress> list = addressService.queryAllAddress(param);
        return HttpResult.ok("successfully", list);
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/get/id")
    public HttpResult GetAddressById(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        List<CustomerAddress> list = addressService.queryAllAddress(param);
        return HttpResult.ok("successfully", list.get(0));
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/add")
    public HttpResult AddNewAddress(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        JSONObject json= JSONObject.parseObject(jsonData);
        HashMap<String, Object> param  =  JSONObject.parseObject(json.get("addressObj").toString(), HashMap.class);

        addressService.insertAddress(param);
        return HttpResult.ok("successfully");
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/delete")
    public HttpResult DeleteAddress(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        addressService.deleteAddress(param);
        param = new HashMap<>();
        param.put("customer_id",customer_id);
        List<CustomerAddress> list = addressService.queryAllAddress(param);
        return HttpResult.ok("successfully",list);
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/update")
    public HttpResult UpdateAddress(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        JSONObject json= JSONObject.parseObject(jsonData);
        HashMap<String, Object> param  =  JSONObject.parseObject(json.get("addressObj").toString(), HashMap.class);
        addressService.updateAddress(param);
        return HttpResult.ok("successfully");
    }



}
