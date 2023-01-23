package com.doggy.mapper;

import com.doggy.entity.EManage.Pms_attr;
import com.doggy.entity.EManage.Pms_attr_attrgroup_relation;
import com.doggy.entity.EManage.Pms_attr_group;
import com.doggy.entity.EManage.TreeStructure;
import com.doggy.utils.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

public interface SysAttrSpecMapper {


    @Select("select * from pms_category where pid =#{pid}")
    List<TreeStructure> queryChildCategory(int pid);

    List<Pms_attr_group> queryPms_attr_group(Page page);

    List<Pms_attr> queryPms_attr(Page page);

    int pageQueryPms_attr_groupCount(Page page);

    int pageQueryPms_attr_Count(Page page);

    void insertAssociatedAtrrGroup(Pms_attr_attrgroup_relation item);

    @Delete("delete from pms_attr_attrgroup_relation where attr_id =#{attr_id} and attr_group_id = #{attr_group_id}" )
    void deleteAssociatedAtrrGroup(Pms_attr_attrgroup_relation item);

    @Select("select * from pms_attr_attrgroup_relation ")
    List<Pms_attr_attrgroup_relation> queryPms_attr_attrgroup_relation();


    @Select("select * from pms_attr_group where attr_group_id = #{id} ")
    Pms_attr_group queryPms_attr_groupById(int id);

    List<Pms_attr> queryPms_attrByids(List<Integer> list);

    void insertAttrGroup(HashMap<String, Object> param);

    void updateAttrGroup(HashMap<String, Object> param);
}
