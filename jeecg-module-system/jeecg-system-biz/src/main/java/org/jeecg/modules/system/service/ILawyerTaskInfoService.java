package org.jeecg.modules.system.service;

import org.jeecg.modules.system.entity.LawyerTaskInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: lawyer_task_info
 * @Author: jeecg-boot
 * @Date:   2024-03-02
 * @Version: V1.0
 */
public interface ILawyerTaskInfoService extends IService<LawyerTaskInfo> {

    int selectTaskInfoNum(String taskId);
}
