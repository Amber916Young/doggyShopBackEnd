package com.doggy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Category;
import com.doggy.entity.Goods;
import com.doggy.entity.ImageRepo;
import com.doggy.entity.OrderCart;
import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
import com.doggy.utils.HttpResult;
import com.doggy.utils.JsonUtils;
import com.doggy.utils.Page;
import com.doggy.utils.UnicodeUtils;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
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
@RequestMapping("web/product")
public class WebProductController {
    @Autowired
    private SysGoodsService goodsService;
    @Autowired
    private SysOrderService orderService;
    @Value("${Delcode.key}")
    private String Delcode;



    @SneakyThrows
    @ResponseBody
    @GetMapping("/goods/queryAll/allKind")
    public HttpResult good_queryAll_allKind(Page page, @RequestParam("limit") int limit) {
        page.setRows(limit);
        List<Goods> lists = goodsService.pageQueryGoodData(page);
        int totals=goodsService.pageQueryGoodCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping("/goods/queryAll")
    public HttpResult good_queryAll(Page page, @RequestParam("limit") int limit, @RequestParam("id") int id, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        page.setRows(limit);
        page.setKeyWord(keyword);
        page.setId(id);
        List<Goods> lists = goodsService.pageQueryGoodData(page);
        int totals=goodsService.pageQueryGoodCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/goods/detail")
    public HttpResult good_detail(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        try {
            int id = Integer.parseInt(param.get("id").toString());
            Goods goods = goodsService.queryAllGoodsById(id);
            param.put("fid",id);
            param.put("type","goods");
            List<ImageRepo> imgList = goodsService.queryAllImageList(param);
            StringBuilder sb = new StringBuilder();
            for (ImageRepo imageRepo : imgList){
                sb.append(imageRepo.getImg_url()+"\r");
            }
            param = new HashMap<>();
            param.put("item",goods);
            param.put("imgList",imgList);
            param.put("imgArea",sb.toString());
            return HttpResult.ok("查询成功",param);
        }catch (Exception e){
            return HttpResult.error(e.getMessage());
        }
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/goods/update/flag")
    public HttpResult good_update_flag(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        try {
            goodsService.updateGoods(param);
            return HttpResult.ok("更新成功");
        }catch (Exception e){
            return HttpResult.error(e.getMessage());
        }
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/goods/update")
    public HttpResult good_update(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        try {
            String[] imgList = param.get("imgList").toString().trim().split("\\n");
            int fid = Integer.parseInt(param.get("id").toString());
            param.put("type","goods");

            List<String > tmp = new ArrayList<>();
            tmp.add("title");
//            tmp.add("description");
            param = UnicodeUtils.DECODEUnicode(param,tmp);


            goodsService.updateGoods(param);
            goodsService.deleteImageRepo(fid);
            for(String s : imgList){
                if(s.trim().equals("")) continue;
                ImageRepo imageRepo = new ImageRepo();
                imageRepo.setFid(fid);
                imageRepo.setImg_url(s);
                goodsService.insertImageRepo(imageRepo);
            }
            return HttpResult.ok("更新成功");
        }catch (Exception e){
            return HttpResult.error(e.getMessage());
        }
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/goods/add")
    public HttpResult good_add(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        try {
            HashMap<String, Object> map = JsonUtils.jsonToPojo(param.get("field").toString(),HashMap.class);
            String[] imgList = map.get("imgList").toString().trim().split("\\n");
            Goods goods = JsonUtils.jsonToPojo(param.get("goods").toString(),Goods.class);
            goods = UnicodeUtils.DECODEUnicode(goods);
            int c = goodsService.insertGoods(goods);
            int fid = goods.getId();
            for(String s : imgList){
                if(s.trim().equals("")) continue;
                ImageRepo imageRepo = new ImageRepo();
                imageRepo.setFid(fid);
                imageRepo.setImg_url(s);
                imageRepo.setType("goods");
                goodsService.insertImageRepo(imageRepo);
            }
            return HttpResult.ok("新增");
        }catch (Exception e){
            return HttpResult.error(e.getMessage());
        }
    }


    @SneakyThrows
    @ResponseBody
    @PostMapping("/goods/deletes")
    public HttpResult good_delete(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        JSONObject json = JSONObject.parseObject(jsonData);
        JSONArray ids = json.getJSONArray("ids");
        String code = json.getString("code");
        if (code.equals(Delcode)) {
            HashMap<String, Object> param = new HashMap<>();
            param.put("flag",0);
            for (int i = 0; i < ids.size(); i++) {
                param.put("id",ids.get(i).toString());
                goodsService.deleteGoods(param);
            }
            return HttpResult.ok("successfully");
        }
        return  HttpResult.error("Code is wrong，delete fail!");
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/category/queryAll/option")
    public HttpResult category_queryAll(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        List<Category> lists = goodsService.queryAllCategory(param);
        return HttpResult.ok("查询成功", lists);
    }








    @SneakyThrows
    @ResponseBody
    @GetMapping("/category/queryAll")
    public HttpResult category_queryAll(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        page.setRows(limit);
        page.setKeyWord(keyword);
        List<Category> lists = goodsService.pageQueryCategoryData(page);
        int totals=goodsService.pageQueryCategoryCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/category/add")
    public HttpResult category_add(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        try {
            List<String > list = new ArrayList<>();
            list.add("title");
            list.add("description");
            param = UnicodeUtils.DECODEUnicode(param,list);
            goodsService.insertCategory(param);
            return HttpResult.ok("新增");
        }catch (Exception e){
            return HttpResult.error(e.getMessage());
        }
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/category/update")
    public HttpResult category_update(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        try {
            List<String > list = new ArrayList<>();
            list.add("title");
            list.add("description");
            param = UnicodeUtils.DECODEUnicode(param,list);
            goodsService.updateCategory(param);
            return HttpResult.ok("更新成功");
        }catch (Exception e){
            return HttpResult.error(e.getMessage());
        }
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/category/deletes")
    public HttpResult delete(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        JSONObject json = JSONObject.parseObject(jsonData);
        JSONArray ids = json.getJSONArray("ids");
        String code = json.getString("code");
        if (code.equals(Delcode)) {
            HashMap<String, Object> param = new HashMap<>();
            param.put("flag",0);
            for (int i = 0; i < ids.size(); i++) {
                param.put("id",ids.get(i).toString());
                goodsService.deleteCategory(param);
            }
            return HttpResult.ok("successfully");
        }
        return  HttpResult.error("Code is wrong，delete fail!");
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/category/detail")
    public HttpResult category_detail(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        try {
            int id = Integer.parseInt(param.get("id").toString());
            Category category = goodsService.queryCategoryById(id);
            return HttpResult.ok("查询成功",category);
        }catch (Exception e){
            return HttpResult.error(e.getMessage());
        }
    }

}
