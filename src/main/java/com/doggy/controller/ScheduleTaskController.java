package com.doggy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.Rule;
import com.doggy.entity.ScheduleTask;
import com.doggy.service.*;
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

@RestController
@RequestMapping("web/scheduletask")
public class ScheduleTaskController {

    @Autowired
    private ScheduleTaskService scheduleTaskService;

    @Value("${Delcode.key}")
    private String Delcode;

    @SneakyThrows
    @ResponseBody
    @PostMapping("/inserts")
    HttpResult insertScheduleTask(@RequestBody String jsonData){
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=", "");
        JSONObject json = JSONObject.parseObject(jsonData);
        int deleted = Integer.parseInt(json.get("deleted").toString());

        HashMap<String, Object> param = new HashMap<>();
        param.put("deleted",deleted);
        scheduleTaskService.insertScheduleTask(param);

        return HttpResult.ok("successfully");
    }
    @SneakyThrows
    @ResponseBody
    @GetMapping("/queryAll")
    public HttpResult queryAll() {
        List<HashMap<String ,Object>> mapList = scheduleTaskService.queryAll();
        return HttpResult.ok("ok",mapList);
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping("/queryByPage")
    public HttpResult queryByPage(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        page.setKeyWord(keyword);
        page.setRows(limit);
        String status = request.getParameter("status");
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",status);
        page.setData(map);
        List<ScheduleTask> lists = scheduleTaskService.pageQueryScheduleTaskData(page);
        int totals=scheduleTaskService.pageQueryScheduleTaskCount(page);
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
                scheduleTaskService.deleteScheduleTask(param);
            }
            return HttpResult.ok("successfully");
        }
        return  HttpResult.error("Code is wrong，delete fail!");

    }


    @SneakyThrows
    @ResponseBody
    @PostMapping("/updates")
    public HttpResult update(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        HashMap<String , Object> param = JSONObject.parseObject(jsonData,HashMap.class);
        scheduleTaskService.updateScheduleTask(param);
        return HttpResult.ok("successfully");

    }
}
