package com.doggy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.CustomerInfo;
import com.doggy.entity.Goods;
import com.doggy.entity.ImageRepo;
import com.doggy.service.CustomerService;
import com.doggy.utils.HttpResult;
import com.doggy.utils.JsonUtils;
import com.doggy.utils.TokenUtils;
import com.fasterxml.uuid.Generators;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName:UserInfoController
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 17:21
 * @Version: v1.0
 */
@RestController
@RequestMapping("userInfo")
public class UserInfoController {
    @Autowired
    CustomerService customerService;


    @SneakyThrows
    @ResponseBody
    @GetMapping("/generate/UUID")
    public HttpResult UUID(){
        UUID uuid = Generators.timeBasedGenerator().generate();
        return HttpResult.ok("successfully",uuid.toString().replaceAll("-",""));
    }


    @SneakyThrows
    @ResponseBody
    @PostMapping("/update")
    public HttpResult update(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        CustomerInfo customer = JsonUtils.jsonToPojo( param.get("customer").toString(), CustomerInfo.class);
        try {
            //update token
            String token = TokenUtils.genToken(customer);
            customer.setToken(token);
            customerService.updateCustomerInfo(customer);
            customer = customerService.queryCustomerByid(customer.getId());
        }catch (Exception e){
            customer = null;
            return HttpResult.error(e.getMessage());
        }
        return HttpResult.ok("修改个人信息成功",customer);
    }





}
