package com.doggy.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName:ScheduleTask
 * @Auther: yyj
 * @Description:
 * @Date: 10/12/2022 19:20
 * @Version: v1.0
 */
@Data
public class ScheduleTask {
    private Long id;
    /**
     * 任务名称
     */
    private String name;

    private String desc_txt;

    private String cron;

    /**
     * 任务状态
     */
    private Status status;

    /**
     * 开关
     */
    private int deleted=0;
    /**
     * 执行指定类
     * 指定具体的包名
     */
    private String exe_class;
    /**
     * 任务参数 json
     */
    private String params;

    private LocalDateTime create_time;
    private LocalDateTime update_time;

    public enum Status{
        create,
        running,
        stop
    }
}
