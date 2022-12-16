package com.doggy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.*;
import com.doggy.service.CustomerService;
import com.doggy.service.SysCommentService;
import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
import com.doggy.utils.*;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @ClassName:CommentsController
 * @Auther: yyj
 * @Description:
 * @Date: 30/11/2022 12:36
 * @Version: v1.0
 */
@RestController
@RequestMapping("comment")
public class CommentsController {
    @Autowired
    private SysCommentService commentService;
    @Autowired
    private SysGoodsService goodsService;
    @Autowired
    private CustomerService customerService;

    @Autowired
    private SysOrderService orderService;


    @SneakyThrows
    @ResponseBody
    @PostMapping("/get/all")
    public HttpResult GetAllComments(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        // good_id
//        int good_id= Integer.parseInt(param.get("good_id").toString());
//        List<Comment> commentList = getComments(good_id);
//        return HttpResult.ok("successfully", commentList);


        Page page = new Page();
        HashMap<String,Object> map = new HashMap<>();
        double rate = Double.parseDouble(param.get("rate").toString());
        int good_id= Integer.parseInt(param.get("good_id").toString());
        page.setRows(20);
        map.put("rateHigh",rate);
        map.put("rateLow",0);
        map.put("good_id",good_id);
        if(rate == -1){
            map.put("rateHigh",5);
            map.put("rateLow",0);
        }else {
            if(rate <= 2.0){
                map.put("rateHigh",rate);
                map.put("rateLow",0);
            }else  if(rate > 2.0 && rate <4){
                map.put("rateHigh",3.9);
                map.put("rateLow",3.1);
            } else {
                map.put("rateHigh",5);
                map.put("rateLow",4);
            }
        }
        page.setKeyWord(null);
        page.setData(map);
        List<Comment> lists = commentService.pageQueryCommentDataByid(page);
        for(Comment tmp : lists){
            // 还原表情
            String content = EmojiParser.parseToUnicode( tmp.getContent());
            tmp.setContent(content);
            HashMap<String,Object> customer_info = new HashMap<>();
            CustomerInfo  customerInfo = customerService.queryCustomerByid(tmp.getCustomer_id());
            if(customerInfo == null){
                customer_info.put("username","匿名用户");
            }else {
                customer_info.put("username",customerInfo.getUsername());
                customer_info.put("avatarUrl",customerInfo.getAvatarUrl());
            }

            tmp.setCustomer_info(customer_info);
        }
        return HttpResult.ok("查询成功", lists);

    }


    @SneakyThrows
    @ResponseBody
    @PostMapping("/getAll/suggestion")
    public HttpResult getAllSuggestion(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        Page page = new Page();
        int limit = 20;
        HashMap<String,Object> data = new HashMap<>();
        int curr = Integer.parseInt(param.get("currNo").toString());
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        String  status = param.get("status").toString();
        limit = Integer.parseInt(param.get("limit").toString());
        page.setData(data);
        page.setRows(limit);
        page.setPage(curr);
        int start = page.getStart();
        page.setStart(start);
        page.setId(customer_id);
        data.put("status",status);
        List<Suggestion> list = commentService.querySuggestionPage(page);
        return HttpResult.ok("查询成功",list);
    }


    @SneakyThrows
    @ResponseBody
    @PostMapping("/suggestion/add")
    public HttpResult addSuggestion(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        String content = param.get("content").toString();
        String suggest_id = NumberUtil.customFormatDate("yyyyMMddHHmmss");
        param.put("suggest_id",suggest_id);
        if (EmojiManager.containsEmoji(content)){
            //parseToHtmlHexadecimal parseToUnicode
             content = EmojiParser.parseToHtmlHexadecimal(content);
            param.put("content",content);
        }
        commentService.insertSuggestion(param);
        return HttpResult.ok("评价成功");
    }




    @SneakyThrows
    @ResponseBody
    @PostMapping("/add")
    public HttpResult addComment(@RequestBody String jsonData) {
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        // good_id
//        List<Comment> commentList = commentService.queryAllComments(param);
        int good_id= Integer.parseInt(param.get("good_id").toString());
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        int order_detail_id = Integer.parseInt(param.get("order_detail_id").toString());
        double rate  = Double.parseDouble(param.get("rate").toString());
        String content =param.get("content").toString();
        if(EmojiManager.containsEmoji(content)){
            content = EmojiParser.parseToHtmlHexadecimal(content);
            param.put("content",content);
        }

        List<String> imgMap = JsonUtils.jsonToPojo(param.get("imgMap").toString(),List.class);
        if(imgMap.size() > 0){
            List<ImageRepo> list = new ArrayList<>();
            for (String base64 : imgMap){
                ImageRepo imageRepo = new ImageRepo();
                imageRepo.setFid(good_id);
                imageRepo.setType("comment");
                imageRepo.setImg_url(base64);
                imageRepo.setCustomer_id(customer_id);
                list.add(imageRepo);
            }
            commentService.insertBatchComments_ImageRepo(list);
        }
        Comment comment = new Comment();
        comment.setGood_id(good_id);
        comment.setContent(content);
        comment.setCustomer_id(customer_id);
        comment.setRate(rate);
        comment.setOrder_detail_id(order_detail_id);
        commentService.insertComments(comment);
        HashMap<String,Object> map = new HashMap<>();
        map.put("order_detail_id",order_detail_id);
        map.put("is_comment",1);
        orderService.updateOrderDetail(map);
        return HttpResult.ok("评价成功");
    }





}
