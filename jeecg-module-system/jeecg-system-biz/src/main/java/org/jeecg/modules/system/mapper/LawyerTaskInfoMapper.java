package org.jeecg.modules.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.system.entity.LawyerTaskInfo;


/**
 * @Description: lawyer_task_info
 * @Author: jeecg-boot
 * @Date:   2024-03-02
 * @Version: V1.0
 */
public interface LawyerTaskInfoMapper extends BaseMapper<LawyerTaskInfo> {


    @Select("SELECT count(id) FROM lawyer_task_info WHERE task_id = #{taskId}")
    int selectTaskInfoNum(@Param("taskId") String taskId);
}