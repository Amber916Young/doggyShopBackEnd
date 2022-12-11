package com.doggy.mapper;


import com.doggy.entity.ScheduleTask;
import com.doggy.utils.HttpResult;
import com.doggy.utils.Page;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

public interface ScheduleTaskMapper {

      void insertScheduleTask(HashMap<String, Object> param);

      void updateScheduleTask(HashMap<String, Object> param);


      @Select("select  * from DoggyPets.schedule_task")
      List<HashMap<String, Object>> queryAll();

      void deleteScheduleTask(HashMap<String, Object> param);

      List<ScheduleTask> pageQueryScheduleTaskData(Page page);

      int pageQueryScheduleTaskCount(Page page);
}
