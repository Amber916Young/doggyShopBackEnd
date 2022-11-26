package com.doggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Goods;
import com.doggy.entity.ImageRepo;
import com.doggy.entity.OrderCart;
import com.doggy.utils.HttpResult;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:ErrorHandleController
 * @Auther: yyj
 * @Description:
 * @Date: 26/11/2022 00:18
 * @Version: v1.0
 */
@RestController
@RequestMapping("error")
public class ErrorHandleController {
    @SneakyThrows
    @ResponseBody
    @GetMapping("/invalid")
    public HttpResult GoodsDetail(){
        return HttpResult.invalid("登陆信息过期");
    }
}
