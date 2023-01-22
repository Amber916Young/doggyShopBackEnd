package com.doggy.entity.EManage;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:TreeStru
 * @Auther: yyj
 * @Description:
 * @Date: 21/01/2023 21:11
 * @Version: v1.0
 */
@Data
public class TreeStructure {
    private int id;
    private String title;
    private int pid;
    private String href;
    private List<TreeStructure> children = new ArrayList();
    private boolean checked = true;
    private boolean spread = true;
    private boolean disabled = false;
}
