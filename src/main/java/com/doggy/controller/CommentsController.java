package com.doggy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Comment;
import com.doggy.entity.CustomerInfo;
import com.doggy.entity.ImageRepo;
import com.doggy.service.CustomerService;
import com.doggy.service.SysCommentService;
import com.doggy.service.SysGoodsService;
import com.doggy.service.SysOrderService;
import com.doggy.utils.HttpResult;
import com.doggy.utils.JsonUtils;
import com.doggy.utils.Page;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
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

    private void queryComment(Comment comment,int good_id){
        List<Comment> commentList = commentService.queryAllCommentsDFS(comment.getComment_id(),good_id);
        for (Comment tmp : commentList){
            int customer_id = tmp.getCustomer_id();
            CustomerInfo customer = customerService.queryCustomerByid(customer_id);
            if( customer != null){
                HashMap<String,Object> map = new HashMap<>();
                map.put("type","comment");
                map.put("fid",good_id);
                map.put("customer_id",customer.getId());
                List<ImageRepo> commentImage = goodsService.queryAllImageList(map);
                customer.setToken(null);
                customer.setLogin_time(null);
                customer.setCreate_time(null);
                customer.setPoints(0);
                customer.setOpenid(null);
                customer.setGender(null);
                customer.setPhone(null);
                customer.setUuid(null);
                tmp.setCustomer(customer);
                List<String> list = new ArrayList<>();
                for (ImageRepo img :commentImage ) {
                    list.add(img.getImg_url());
                }
                tmp.setCommentImage(list);
            }
            queryComment(tmp,good_id);
        }
        comment.setCommentList(commentList);
    }

    public List<Comment> getComments(int good_id){
        Comment parent = new Comment();
        parent.setComment_id(0);
        queryComment(parent,good_id);
        return parent.getCommentList();
    }
    @ResponseBody
    @GetMapping("/get/test")
    public HttpResult test() {
        // good_id
//        List<Comment> commentList = commentService.queryAllComments(param);
        int good_id= 3;
        List<Comment> commentList = getComments(good_id);

        return HttpResult.ok("successfully", commentList);

    }

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
            int id = tmp.getComment_id();
            CustomerInfo  customerInfo = customerService.queryCustomerByid(tmp.getCustomer_id());
            Comment subComment = commentService.queryCommentsbyId(id);
            tmp.setCustomer(customerInfo);
            tmp.setSubComment(subComment);
        }
        return HttpResult.ok("查询成功", lists);

    }




    @SneakyThrows
    @ResponseBody
    @PostMapping("/add")
    public HttpResult addComment(@RequestBody String jsonData) {
//        jsonData = URLDecoder.decode(jsonData, "utf-8").replacexAll("=", "");
        HashMap<String, Object> param = JSONObject.parseObject(jsonData, HashMap.class);
        // good_id
//        List<Comment> commentList = commentService.queryAllComments(param);
        int good_id= Integer.parseInt(param.get("good_id").toString());
        int customer_id = Integer.parseInt(param.get("customer_id").toString());
        int order_detail_id = Integer.parseInt(param.get("order_detail_id").toString());
        double rate  = Double.parseDouble(param.get("rate").toString());
        String content =param.get("content").toString();
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
