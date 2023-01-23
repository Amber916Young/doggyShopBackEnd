package com.doggy.service;

import com.doggy.entity.EManage.Pms_attr;
import com.doggy.entity.EManage.Pms_attr_attrgroup_relation;
import com.doggy.entity.EManage.Pms_attr_group;
import com.doggy.entity.EManage.TreeStructure;
import com.doggy.mapper.ScheduleTaskMapper;
import com.doggy.mapper.SysAttrSpecMapper;
import com.doggy.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:SysAttrSpecService
 * @Auther: yyj
 * @Description:
 * @Date: 21/01/2023 21:06
 * @Version: v1.0
 */
@Service
public class SysAttrSpecService {

    @Autowired
    SysAttrSpecMapper sysAttrSpecMapper;

    public List<TreeStructure> queryChildCategory(int pid) {
        return sysAttrSpecMapper.queryChildCategory(pid);
    }

    public List<Pms_attr_group> queryPms_attr_group(Page page) {
        return sysAttrSpecMapper.queryPms_attr_group(page);

    }

    public List<Pms_attr> queryPms_attr(Page page) {
        return sysAttrSpecMapper.queryPms_attr(page);
    }

    public int pageQueryPms_attr_groupCount(Page page) {
        return sysAttrSpecMapper.pageQueryPms_attr_groupCount(page);
    }

    public int pageQueryPms_attr_Count(Page page) {
        return sysAttrSpecMapper.pageQueryPms_attr_Count(page);
    }

    public void insertAssociatedAtrrGroup(Pms_attr_attrgroup_relation item) {
        sysAttrSpecMapper.insertAssociatedAtrrGroup(item);
    }

    public void deleteAssociatedAtrrGroup(Pms_attr_attrgroup_relation item) {
        sysAttrSpecMapper.deleteAssociatedAtrrGroup(item);
    }

    public List<Pms_attr_attrgroup_relation> queryPms_attr_attrgroup_relation() {
        return sysAttrSpecMapper.queryPms_attr_attrgroup_relation();
    }

    public Pms_attr_group queryPms_attr_groupById(int id) {
        return sysAttrSpecMapper.queryPms_attr_groupById(id);
    }

    public List<Pms_attr> queryPms_attrByids(List<Integer> list) {
        return sysAttrSpecMapper.queryPms_attrByids(list);
    }

    public void insertAttrGroup(HashMap<String, Object> param) {
        sysAttrSpecMapper.insertAttrGroup(param);
    }

    public void updateAttrGroup(HashMap<String, Object> param) {
        sysAttrSpecMapper.updateAttrGroup(param);
    }
}
