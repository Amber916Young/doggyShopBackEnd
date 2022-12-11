package com.doggy.service;

import com.doggy.entity.ScheduleTask;
import com.doggy.mapper.*;
import com.doggy.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ScheduleTaskService {

    @Autowired
    ScheduleTaskMapper scheduleTaskMapper;

    public void insertScheduleTask(HashMap<String, Object> param){
        scheduleTaskMapper.insertScheduleTask(param);
    }

    public void updateScheduleTask(HashMap<String, Object> param) {
        scheduleTaskMapper.updateScheduleTask(param);
    }


    public List<HashMap<String, Object>> queryAll() {
        return scheduleTaskMapper.queryAll();
    }

    public void deleteScheduleTask(HashMap<String, Object> param) {
        scheduleTaskMapper.deleteScheduleTask(param);
    }

    public List<ScheduleTask> pageQueryScheduleTaskData(Page page) {
        return scheduleTaskMapper.pageQueryScheduleTaskData(page);
    }

    public int pageQueryScheduleTaskCount(Page page) {
        return scheduleTaskMapper.pageQueryScheduleTaskCount(page);
    }
}
