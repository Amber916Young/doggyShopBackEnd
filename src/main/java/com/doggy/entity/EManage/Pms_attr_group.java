package com.doggy.entity.EManage;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:Pms_attr_group
 * @Auther: yyj
 * @Description:
 * @Date: 21/01/2023 23:22
 * @Version: v1.0
 */
@Data
public class Pms_attr_group {
    int attr_group_id;
    String attr_group_name;
    int catelog_id;
    List<Pms_attr> attrs = new ArrayList<>();
}
