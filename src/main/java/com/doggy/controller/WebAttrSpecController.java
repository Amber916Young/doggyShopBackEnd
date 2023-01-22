package com.doggy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doggy.entity.CustomerAddress;
import com.doggy.entity.CustomerInfo;
import com.doggy.entity.EManage.Pms_attr;
import com.doggy.entity.EManage.Pms_attr_attrgroup_relation;
import com.doggy.entity.EManage.Pms_attr_group;
import com.doggy.entity.EManage.TreeStructure;
import com.doggy.service.SysAttrSpecService;
import com.doggy.service.SysCouponService;
import com.doggy.utils.HttpResult;
import com.doggy.utils.Page;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.*;

/**
 * @ClassName:WebAttrSpecController
 * @Auther: yyj
 * @Description:
 * @Date: 21/01/2023 20:18
 * @Version: v1.0
 */

@RestController
@RequestMapping("web/attr")
public class WebAttrSpecController {
    @Autowired
    private SysAttrSpecService sysAttrSpecService;

    @SneakyThrows
    @ResponseBody
    @GetMapping("/load/tree")
    public HttpResult CategoryTree() {
        TreeStructure parent = new TreeStructure();
        parent.setId(0);
        queryChild(parent);
        return HttpResult.ok("successfully", parent.getChildren());
    }
    private void queryChild(TreeStructure parent){
        List<TreeStructure> childList = sysAttrSpecService.queryChildCategory(parent.getId());
        for(TreeStructure item :childList){
            queryChild(item);
        }
        parent.setChildren(childList);
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping("/load/attr/group")
    public HttpResult groupAttrGroup(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        page.setRows(limit);
        page.setKeyWord(keyword);
        List<Pms_attr_group> lists = sysAttrSpecService.queryPms_attr_group(page);
        int totals=sysAttrSpecService.pageQueryPms_attr_groupCount(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping("/attr/group/associated")
    public HttpResult associated(@RequestBody String jsonData) {
        jsonData = URLDecoder.decode(jsonData, "utf-8").replaceAll("=","");
        JSONObject json = JSONObject.parseObject(jsonData);
        int attr_group_id = json.getIntValue("attr_group_id");
        JSONArray ids = json.getJSONArray("attr_ids");
        for (int i = 0; i < ids.size(); i++) {
            Pms_attr_attrgroup_relation item = new Pms_attr_attrgroup_relation();
            int attrId = Integer.parseInt(ids.get(i).toString());
            item.setAttr_id(attrId);
            item.setAttr_group_id(attr_group_id);
            sysAttrSpecService.deleteAssociatedAtrrGroup(item);
            sysAttrSpecService.insertAssociatedAtrrGroup(item);
        }
        return HttpResult.ok();
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping("/load/attr")
    public HttpResult groupAttr(Page page, @RequestParam("limit") int limit, HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        page.setRows(limit);
        page.setKeyWord(keyword);
        List<Pms_attr> lists = sysAttrSpecService.queryPms_attr(page);
        int totals=sysAttrSpecService.pageQueryPms_attr_Count(page);
        return HttpResult.ok(0,"查询成功", lists,totals);
    }

    @SneakyThrows
    @ResponseBody
    @GetMapping("/load/attr/{attr_type}")
    public HttpResult groupAttr(@PathVariable("attr_type") int attr_type) {
        List<Pms_attr_attrgroup_relation> lists = sysAttrSpecService.queryPms_attr_attrgroup_relation();
        Set<Integer> arrGroupIds = new HashSet<>();
        Map<Integer,List<Integer>> map = new HashMap<>();
        for(Pms_attr_attrgroup_relation item : lists){
            arrGroupIds.add(item.getAttr_group_id());
            List<Integer> tmp = map.getOrDefault(item.getAttr_group_id(),new ArrayList<>());
            tmp.add(item.getAttr_id());
            map.put(item.getAttr_group_id(),tmp);
        }
        List<Pms_attr_group> list = new ArrayList<>();
        for(int i : arrGroupIds){
            Pms_attr_group tmp = sysAttrSpecService.queryPms_attr_groupById(i);
            List<Pms_attr> attrs = sysAttrSpecService.queryPms_attrByids(map.get(i));
            for(Pms_attr key : attrs){
                key.setValue_list(key.getValue_select().split(";"));
            }
            tmp.setAttrs(attrs);
            list.add(tmp);
        }
        return HttpResult.ok(list);
    }


    @SneakyThrows
    @ResponseBody
    @GetMapping("/load/sku/attr")
    public HttpResult getSKU_sale_attr() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("")
        List<Pms_attr> lists = sysAttrSpecService。


    }





}
