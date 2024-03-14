package org.jeecg.modules.quartz.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.system.entity.LawyerTask;
import org.jeecg.modules.system.entity.LawyerTaskChannel;
import org.jeecg.modules.system.entity.LawyerTaskInfo;
import org.jeecg.modules.system.mapper.LawyerTaskChannelMapper;
import org.jeecg.modules.system.mapper.LawyerTaskInfoMapper;
import org.jeecg.modules.system.mapper.LawyerTaskMapper;
import org.jeecg.modules.system.service.ILawyerTaskInfoService;
import org.jeecg.modules.system.service.ILawyerTaskService;
import org.jeecg.modules.system.service.lawyer.DelayedQueueService;
import org.jeecg.modules.system.service.lawyer.LawyerProductContext;
import org.jeecg.modules.system.service.lawyer.Vo.DYProductResultVo;
import org.jeecg.modules.system.service.lawyer.Vo.JdProductResultVo;
import org.jeecg.modules.system.service.lawyer.Vo.KSProductResultVo;
import org.jeecg.modules.system.service.lawyer.Vo.XHSProductResultVo;
import org.jeecg.modules.system.service.lawyer.impl.DyProductDomain;
import org.jeecg.modules.system.service.lawyer.impl.JDProductDomain;
import org.jeecg.modules.system.service.lawyer.impl.KSProductDomain;
import org.jeecg.modules.system.service.lawyer.impl.XHSProductDomain;
import org.jeecg.modules.system.service.lawyer.strategy.LawyerProductStrategy;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class LawyerTaskJob implements Job {

    /**
     * 若参数变量名修改 QuartzJobController中也需对应修改
     */
    private String parameter;

    private static final String SET_IF_NOT_EXIST = "NX";

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);


    @Autowired
    private ILawyerTaskService taskService;

    @Autowired
    private ILawyerTaskInfoService taskInfoService;

    @Resource
    private LawyerTaskMapper lawyerTaskMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LawyerTaskChannelMapper taskChannelMapper;

    @Autowired
    private LawyerProductContext lawyerProductContext;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    @Autowired
    private DelayedQueueService delayedQueueService;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = "lawyerProductJob:taskId:";
        LambdaQueryWrapper<LawyerTask> lambdaQueryWrapper = new LambdaQueryWrapper<LawyerTask>();
        lambdaQueryWrapper.eq(LawyerTask::getStatus, 0);
//        lambdaQueryWrapper.eq(LawyerTask::getSearchDomain, 2);
        lambdaQueryWrapper.eq(LawyerTask::getYn, 1);
        List<LawyerTask> listJob = taskService.list(lambdaQueryWrapper);
        listJob.stream().forEach(item->{
            Boolean result = redisTemplate.opsForValue().setIfAbsent(redisKey + item.getId(), item.getId(), Duration.ofHours(12));
            if (!result) {
                return;
            }
            String channels = item.getChannel();
            String[] channelArray = channels.split(",");
            LambdaQueryWrapper<LawyerTaskChannel>  queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LawyerTaskChannel::getChannelType,item.getSearchDomain());
            queryWrapper.eq(LawyerTaskChannel::getYn,1);
            List<LawyerTaskChannel> taskChannelList = taskChannelMapper.selectList(queryWrapper);
            Map<String,String> channelMap = taskChannelList.stream().collect(Collectors.toMap(LawyerTaskChannel::getServiceNum, p->p.getServiceName()));
            Arrays.stream(channelArray).forEach(k -> {
                String serviceName = channelMap.get(k);
                lawyerProductContext.getProxy(serviceName).doItemSearch(item.getKeyWord(),item.getId());
            });
            Random rand = new Random();
            // 2小时到4小时的毫秒范围
            long currentMillis = System.currentTimeMillis();
            long minMillis = 2L * 60 * 60 * 1000; // 2小时
            long maxMillis = 4L * 60 * 60 * 1000; // 4小时
            // 生成随机毫秒数
            long randomMillis = minMillis + (long) (rand.nextDouble() * (maxMillis - minMillis));
            long score = currentMillis + randomMillis;
            delayedQueueService.addTask(item.getId(),score);
        });
        log.info(" Job Execution key：" + jobExecutionContext.getJobDetail().getKey());
        log.info(String.format("LawyerTaskJob Job !   时间:" + DateUtils.now(), this.parameter));
    }



}
