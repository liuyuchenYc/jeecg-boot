package org.jeecg.modules.system.service.lawyer;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.LawyerTask;
import org.jeecg.modules.system.mapper.LawyerTaskMapper;
import org.jeecg.modules.system.service.ILawyerTaskInfoService;
import org.jeecg.modules.system.service.ILawyerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DelayedQueueService {

    private static final String DELAYED_QUEUE_KEY = "delayed_queue";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private LawyerTaskMapper lawyerTaskMapper;
    @Autowired
    private ILawyerTaskInfoService iLawyerTaskService;

    @PostConstruct
    public void init() {
        log.info("启动延时队列成功");
        new Thread(this::processTasks).start();
    }

    // 添加任务到延时队列
    public void addTask(String taskId, long delayInSeconds) {
        long score = System.currentTimeMillis() / 1000 + delayInSeconds;
        redisTemplate.opsForZSet().add(DELAYED_QUEUE_KEY, taskId, score);
    }

    // 处理延时任务的线程
    public void processTasks() {
        log.info("处理延时队列线程,已启动");
        while (true) {
            try {
                // 获取当前时间戳
                long now = System.currentTimeMillis() / 1000;
                // 获取已经到达执行时间的任务
                Set<ZSetOperations.TypedTuple<Object>> tasks = redisTemplate.opsForZSet().rangeByScoreWithScores(
                        DELAYED_QUEUE_KEY, 0, now, 0, 1);

                // 遍历并执行任务
                for (ZSetOperations.TypedTuple<Object> task : tasks) {
                    String taskId = (String) task.getValue();
                    // 从有序集合中移除任务
                    redisTemplate.opsForZSet().remove(DELAYED_QUEUE_KEY, taskId);
                    // 执行任务逻辑
                    executeTask(taskId);
                }

                // 休眠一段时间再次轮询
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 执行任务的逻辑
    private void executeTask(String taskId) {
        log.info("任务Id {},已完成",taskId);
        LawyerTask lawyearTask =  lawyerTaskMapper.selectById(taskId);
        if(lawyearTask != null && lawyearTask.getStatus() != 2){
            LawyerTask task = new LawyerTask();
            task.setFinishTime(new Date());
            task.setStatus(1);
            task.setId(taskId);
            int infoNum =iLawyerTaskService.selectTaskInfoNum(taskId);
            task.setClueTotal(infoNum);
            lawyerTaskMapper.updateById(task);
        }else{
            log.info("任务Id {},已停止",taskId);
        }
    }
}
