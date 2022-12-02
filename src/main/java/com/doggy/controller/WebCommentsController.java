package com.doggy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Comment;
import com.doggy.service.CustomerService;
import com.doggy.service.SysCommentService;
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
 * @ClassName:WebCommentsController
 * @Auther: yyj
 * @Description:
 * @Date: 02/12/2022 00:10
 * @Version: v1.0
 */
@RestController
@RequestMapping("web/comment")
public class WebCommentsController {
    @Autowired
    private SysCommentService commentService;
    @Autowired
    private SysGoodsService goodsService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SysOrderService orderService;

    @Value("${Delcode.key}")
    private String Delcode;



    @SneakyThrows
    @ResponseBody
    @GetMapping("/queryAll")
    public HttpResult commentqueryAll(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        HashMap<String,Object> map = new HashMap<>();
        double rate = Double.parseDouble(request.getParameter("rate"));
        page.setRows(limit);
        map.put("rateHigh",rate);
        map.put("rateLow",0);
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
        page.setKeyWord(keyword);
        page.setData(map);
        List<Comment> lists = commentService.pageQueryCommentData(page);
        int totals=commentService.pageQueryCommentCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/deletes")
    public HttpResult delete(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        JSONObject json = JSONObject.parseObject(jsonData);
        JSONArray ids = json.getJSONArray("ids");
        String code = json.getString("code");
        if (code.equals(Delcode)) {
            HashMap<String, Object> param = new HashMap<>();
            for (int i = 0; i < ids.size(); i++) {
                param.put("id",ids.get(i).toString());
                commentService.deleteComment(param);
            }
            return HttpResult.ok("successfully");
        }
        return  HttpResult.error("Code is wrong，delete fail!");
    }



}
