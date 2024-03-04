package org.jeecg.modules.system.service.lawyer.impl;

import com.alibaba.fastjson.JSONObject;
import com.xkcoding.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.system.entity.LawyerTaskInfo;
import org.jeecg.modules.system.service.ILawyerTaskInfoService;
import org.jeecg.modules.system.service.lawyer.Vo.DYProductResultVo;
import org.jeecg.modules.system.service.lawyer.constants.OneBoundContants;
import org.jeecg.modules.system.service.lawyer.strategy.LawyerProductStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DyProductDomain implements LawyerProductStrategy<DYProductResultVo> {


    private static final int MAX_RETRIES = 5;

    private static final long retry_delay_ms = 1000;

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private ILawyerTaskInfoService iLawyerTaskInfoService;

    private static String url = "";

    private static String keyword = "";

    private String TASK_ID = "";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void doItemSearch(String keywords,String taskId) {
        keyword = keywords;
        url = "https://api-gw.onebound.cn/douyin/item_search/?key=" + OneBoundContants.key + "&secret=" + OneBoundContants.secret + "&q=" + keyword + "&start_price=0&end_price=0&page=" + 1 + "&cat=0&discount_only=&sort=&seller_info=no&nick=&seller_info=&nick=&ppath=&imgid=&filter=";
        TASK_ID = taskId;
        int totalPages = 1; // 总页数
        int currentPage = 1; // 当前页数
        int retryCount = 0;
        try {
            String reqStr = HttpUtil.get(url);
            log.info("DyProductDomain 请求页码{},返回结果{}", currentPage, JSONObject.toJSONString(reqStr));
            DYProductResultVo resultVo = JSONObject.parseObject(reqStr, DYProductResultVo.class);
            log.info("DyProductDomain,格式化返回结果{}", JSONObject.toJSONString(resultVo));
            if (!"0000".equals(resultVo.getError_code())) {
                log.error("DyProductDomain 调用目标异常");
                while (retryCount < MAX_RETRIES) {
                    String retryStr = HttpUtil.get(url);
                    DYProductResultVo retryVo = JSONObject.parseObject(retryStr, DYProductResultVo.class);
                    log.info("retryVo,格式化返回结果{}", JSONObject.toJSONString(retryVo));
                    if ("0000".equals(retryVo.getError_code())) {
                        convertData(retryVo);
                        break;
                    }
                    retryCount++;
                    if (retryCount < MAX_RETRIES) {
                        log.info("Retrying after delay...");
                        Thread.sleep(retry_delay_ms);
                    }
                }
            }
            convertData(resultVo);
            totalPages = resultVo.getItems().getPagecount();
            for (int i = 2; i < totalPages ; i++) {
               String isReady =  redisTemplate.opsForValue().get("lawyer_task:"+taskId);
               if(isReady.equals("2")){
                   break;
               }
                int finalI = i;
                log.info("第{}页请求",i);
                fetchDataFromRemote(finalI);

//                Runnable task = () -> {
//                    try {
//                        fetchDataFromRemote(finalI);
//                        // 模拟任务执行时间
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        log.error(e.getMessage());
//                    }
//                };
//                executorService.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);

            }
        } catch (Exception e) {
            log.error("DyProductDomain 调用异常{}", e.getMessage());
        }

    }


    /**
     * 调用远程数据
     * @param page
     * @return
     */
    @Override
    public String fetchDataFromRemote(int page) {
        log.info("dy: 页码为{},开始跑数据",page);

        url = "https://api-gw.onebound.cn/douyin/item_search/?key=" + OneBoundContants.key + "&secret=" + OneBoundContants.secret + "&q=" + keyword + "&start_price=0&end_price=0&page=" + page + "&cat=0&discount_only=&sort=&seller_info=no&nick=&seller_info=&nick=&ppath=&imgid=&filter=";
        boolean state = true;
        try {
            String remoteStr = HttpUtil.get(url);
            int retryCount = 0;
            log.info("DyProductDomain 请求页码{},返回结果{}", page, JSONObject.toJSONString(remoteStr));
            DYProductResultVo resultVo = JSONObject.parseObject(remoteStr, DYProductResultVo.class);
            log.info("DyProductDomain,格式化返回结果{}", JSONObject.toJSONString(resultVo));
            if (!"0000".equals(resultVo.getError_code())) {
                log.error("DyProductDomain 调用目标异常");
                while (retryCount < MAX_RETRIES) {
                    String retryStr = HttpUtil.get(url);
                    DYProductResultVo retryVo = JSONObject.parseObject(retryStr, DYProductResultVo.class);
                    log.info("retryVo,格式化返回结果{}", JSONObject.toJSONString(retryVo));
                    if ("0000".equals(retryVo.getError_code())) {
                        convertData(retryVo);
                        break;
                    }
                    retryCount++;
                    if (retryCount < MAX_RETRIES) {
                        log.info("Retrying after delay...");
                        Thread.sleep(retry_delay_ms);
                    }
                }
            }
            convertData(resultVo);
        } catch (Exception e) {
            log.error("DyProductDomain 调用异常{}", e.getMessage());
        }
        if(state){
            return "成功";
        }else{
            return null;
        }
    }


    /**
     * 数据转化与插入
     * @param vo
     */
    @Override
    public void convertData(DYProductResultVo vo){
        vo.getItems().getItem().stream().forEach(item->{
            LawyerTaskInfo lawyerTaskInfo = new LawyerTaskInfo();
            lawyerTaskInfo.setChannel("抖音");
            if(!StringUtils.isEmpty(item.getTitle())){
                lawyerTaskInfo.setProductTitle(item.getTitle());
            }
            if(!StringUtils.isEmpty(item.getPic_url())){
                lawyerTaskInfo.setProductCover(item.getPic_url());
            }
            lawyerTaskInfo.setCommodityPrice(new BigDecimal(item.getPrice()));
            lawyerTaskInfo.setSalesVolume(item.getSales());
            lawyerTaskInfo.setShopName(item.getShop_name());
            lawyerTaskInfo.setProductLink(item.getDetail_url());
            lawyerTaskInfo.setTaskId(TASK_ID);
            iLawyerTaskInfoService.save(lawyerTaskInfo);
        });

    }
}