package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.system.entity.LawyerTaskInfo;
import org.jeecg.modules.system.mapper.LawyerTaskInfoMapper;
import org.jeecg.modules.system.mapper.LawyerTaskMapper;
import org.jeecg.modules.system.service.ILawyerTaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description: lawyer_task_info
 * @Author: jeecg-boot
 * @Date:   2024-03-02
 * @Version: V1.0
 */
@Service
public class LawyerTaskInfoServiceImpl extends ServiceImpl<LawyerTaskInfoMapper, LawyerTaskInfo> implements ILawyerTaskInfoService {

    @Autowired
    private LawyerTaskInfoMapper lawyerTaskMapper;

    @Override
    public int selectTaskInfoNum(String taskId) {
        return lawyerTaskMapper.selectTaskInfoNum(taskId);
    }
}
