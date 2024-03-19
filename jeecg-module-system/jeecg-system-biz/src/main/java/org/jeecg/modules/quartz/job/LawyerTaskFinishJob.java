package org.jeecg.modules.quartz.job;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.system.entity.LawyerTask;
import org.jeecg.modules.system.entity.LawyerTaskChannel;
import org.jeecg.modules.system.mapper.LawyerTaskChannelMapper;
import org.jeecg.modules.system.mapper.LawyerTaskMapper;
import org.jeecg.modules.system.mapper.SysUserRoleMapper;
import org.jeecg.modules.system.service.ILawyerTaskInfoService;
import org.jeecg.modules.system.service.ILawyerTaskService;
import org.jeecg.modules.system.service.lawyer.DelayedQueueService;
import org.jeecg.modules.system.service.lawyer.LawyerProductContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Slf4j
public class LawyerTaskFinishJob implements Job {

    /**
     * 若参数变量名修改 QuartzJobController中也需对应修改
     */
    private String parameter;
    @Autowired
    private LawyerTaskMapper lawyerTaskMapper;
    @Autowired
    private ILawyerTaskInfoService iLawyerTaskService;

    @Autowired
    private ILawyerTaskService taskService;

    @Autowired
    private ILawyerTaskInfoService taskInfoService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LawyerTaskChannelMapper taskChannelMapper;

    @Autowired
    private LawyerProductContext lawyerProductContext;


    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LambdaQueryWrapper<LawyerTask> lambdaQueryWrapper = new LambdaQueryWrapper<LawyerTask>();
        lambdaQueryWrapper.eq(LawyerTask::getStatus, 0);
        lambdaQueryWrapper.eq(LawyerTask::getYn, 1);
        List<LawyerTask> listJob = taskService.list(lambdaQueryWrapper);
        listJob.stream().forEach(item -> {
            Date createTime = item.getCreateTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            List<String> roleNameList = sysUserRoleMapper.getRoleNameByUserName(item.getCreateUser());
            log.info("任务创建人角色为{}", JSONObject.toJSONString(roleNameList));
            String roleName = roleNameList.get(0);
            LambdaQueryWrapper<LawyerTask> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(LawyerTask::getStatus, 0);
            queryWrapper.eq(LawyerTask::getCreateUser, item.getCreateUser());
            if (roleName.equals("管理员" ) || roleName.equals("超级管理员")) {
                return;
            }
            if (roleName.equals("基础版")) {
                calendar.add(Calendar.HOUR_OF_DAY, -4);
            } else if (roleName.equals("标准版")) {
                calendar.add(Calendar.HOUR_OF_DAY, -3);
            }else if (roleName.equals("升级版")) {
                calendar.add(Calendar.HOUR_OF_DAY, -2);
            }

            Date fourHoursAgo = calendar.getTime();
            // 比较 createTime 是否早于 fourHoursAgo
            if (createTime.before(fourHoursAgo)) {
                item.setFinishTime(new Date());
                item.setStatus(1);
                int infoNum =iLawyerTaskService.selectTaskInfoNum(item.getId());
                item.setClueTotal(infoNum);
                lawyerTaskMapper.updateById(item);
                log.info("TaskId:{}任务已完成,总条数:{}",item.getId(),item.getClueTotal());
            }
        });
    }


}
