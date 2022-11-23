package com.doggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Goods;
import com.doggy.entity.ImageRepo;
import com.doggy.utils.HttpResult;
import com.fasterxml.uuid.Generators;
import lombok.SneakyThrows;
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


    @SneakyThrows
    @ResponseBody
    @GetMapping("/generate/UUID")
    public HttpResult GoodsDetail(){
        UUID uuid = Generators.timeBasedGenerator().generate();
        return HttpResult.ok("successfully",uuid.toString().replaceAll("-",""));
    }




}
