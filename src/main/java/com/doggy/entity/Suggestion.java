package com.doggy.entity;

import lombok.Data;

/**
 * @author birdyyoung
 * @ClassName:Suggestion
 * @Description:
 * @Date: 15/12/2022 12:23
 * @Version: v1.0
 */
@Data
public class Suggestion {
    private String suggest_id;
    private String content=null;
    private String create_time;
    private int customer_id;
    private String modified_time;
    private String reply_content=null;
}
