package com.doggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Category;
import com.doggy.entity.Goods;
import com.doggy.entity.ImageRepo;
import com.doggy.entity.OrderCart;
import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.doggy.utils.*;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:GoodController
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 13:26
 * @Version: v1.0
 */
@RestController
@RequestMapping("goods")
public class GoodController {
    @Autowired
    private SysGoodsService goodsService;
    @Autowired
    private SysOrderService orderService;

    @SneakyThrows
    @ResponseBody
    @PostMapping(value = "/get/current/category", produces = "application/json; charset=utf-8")
    public HttpResult GetCurrentGood(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        int category_id = Integer.parseInt(param.get("category_id").toString());
        List<Goods> goodsList = goodsService.queryAllGoods(param);
        for (Goods goods : goodsList) {
            param.put("pid", goods.getId());
            param.put("type","goods");
            List<ImageRepo> imageList = goodsService.queryAllImageList(param);
            goods.setImageList(imageList);
            String content = EmojiParser.parseToUnicode( goods.getDescription());
            String title = EmojiParser.parseToUnicode(goods.getTitle());
            goods.setDescription(content);
            goods.setTitle(title);

        }
        param = new HashMap<>();
        param.put("id",category_id);
        Category currentCategory = goodsService.querycurrentCategory(param);
        String  content = EmojiParser.parseToUnicode( currentCategory.getDescription());
        String title = EmojiParser.parseToUnicode(currentCategory.getTitle());
        currentCategory.setDescription(content);
        currentCategory.setTitle(title);



        param = new HashMap<>();
        param.put("currentCategory",currentCategory);
        param.put("currentCategorySub",goodsList);
        return HttpResult.ok("successfully", param);
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping(value = "/test", produces = "application/json; charset=utf-8")
    public Object sas() {
        List<HashMap> test = goodsService.queryAllTest();
        return  HttpResult.ok("successfully",test);
    }
    @SneakyThrows
    @ResponseBody
    @PostMapping(value = "/get/all" , produces = "application/json; charset=utf-8")
        public HttpResult GetAllGood(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        int index = Integer.parseInt(param.get("index").toString());
        List<Category> categoryList = goodsService.queryAllCategory(param);

        for(Category category : categoryList){
            // 还原表情
            String  content = EmojiParser.parseToUnicode( category.getDescription());
            String title = EmojiParser.parseToUnicode(category.getTitle());
            category.setDescription(content);
            category.setTitle(title);

            // 减少非必要 信息
            category.setCreate_time(null);
            category.setModify_time(null);
        }
        if(categoryList.isEmpty()){
            return HttpResult.ok("无产品");
        }


        param = new HashMap<>();
        Category category = categoryList.get(index);
        param.put("category_id", category.getId());
        List<Goods> goodsList = goodsService.queryAllGoods(param);
        List<HashMap<String,Object>> res =new ArrayList<>();
        for (Goods goods : goodsList) {
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("customer_id", customer_id);
//            map.put("good_id", goods.getId());
//            OrderCart orderCart = orderService.queryOrderCart(map);
//
            HashMap<String,Object> objectHashMap = new HashMap<>();
//            objectHashMap.put("amount", 0);
//            if(orderCart != null){
//                objectHashMap.put("amount", orderCart.getGood_amount());
//            }
            String content = EmojiParser.parseToUnicode( goods.getDescription());
            String title = EmojiParser.parseToUnicode(goods.getTitle());
            objectHashMap.put("id", goods.getId());
            objectHashMap.put("category_id", goods.getCategory_id());
            objectHashMap.put("description", content);
            objectHashMap.put("title", title);
            objectHashMap.put("img_url", goods.getImg_url());
            objectHashMap.put("original_price", goods.getPrice());
            objectHashMap.put("price", goods.getPrice());
            res.add(objectHashMap);
        }
        category.setGoodsList(res);
        return HttpResult.ok("successfully", categoryList);
    }


    @SneakyThrows
    @ResponseBody
    @PostMapping("/detail")
    public HttpResult GoodsDetail(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        int id = Integer.parseInt(param.get("id").toString());
        Goods goods = goodsService.queryAllGoodsById(id);
        String  content = EmojiParser.parseToUnicode( goods.getDescription());
        String title = EmojiParser.parseToUnicode(goods.getTitle());
        goods.setDescription(content);
        goods.setTitle(title);

        param = new HashMap<>();
        param.put("fid",goods.getId());
        param.put("good_id",goods.getId());
        param.put("type","goods");
        List<ImageRepo> imageList = goodsService.queryAllImageList(param);
        goods.setImageList(imageList);
        return HttpResult.ok("successfully",goods);
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/detail/single")
    public HttpResult GoodsDetailsingle(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        int id = Integer.parseInt(param.get("id").toString());
        Goods goods = goodsService.queryAllGoodsById(id);
        return HttpResult.ok("successfully",goods);
    }


}
