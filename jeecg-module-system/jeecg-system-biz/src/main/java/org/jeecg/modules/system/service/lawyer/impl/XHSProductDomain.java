package org.jeecg.modules.system.service.lawyer.impl;

import com.alibaba.fastjson.JSONObject;
import com.xkcoding.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.system.entity.LawyerTaskInfo;
import org.jeecg.modules.system.service.ILawyerTaskInfoService;
import org.jeecg.modules.system.service.lawyer.Vo.XHSProductResultVo;
import org.jeecg.modules.system.service.lawyer.constants.OneBoundContants;
import org.jeecg.modules.system.service.lawyer.strategy.LawyerProductStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class XHSProductDomain implements LawyerProductStrategy<XHSProductResultVo> {


    private static final int MAX_RETRIES = 5;

    private static final long retry_delay_ms = 1000;

//    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private ILawyerTaskInfoService lawyerTaskService;

    private static String url = "";

    private static String keyword = "";

    private String TASK_ID = "";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void doItemSearch(String keywords,String taskId) {
        keyword = keywords;
        url = "https://api-gw.onebound.cn/smallredbook/item_search/?key=" + OneBoundContants.key + "&secret=" + OneBoundContants.secret + "&q=" + keyword + "&start_price=0&end_price=0&page=" + 1 + "&cat=0&discount_only=&sort=&seller_info=no&nick=&seller_info=&nick=&ppath=&imgid=&filter=";
        TASK_ID = taskId;
        int totalPages = 1; // 总页数
        int currentPage = 1; // 当前页数
        int retryCount = 0;
        try {
            String reqStr = HttpUtil.get(url);
            log.info("XHSProductDomain 请求页码{},返回结果{}", currentPage, JSONObject.toJSONString(reqStr));
            XHSProductResultVo resultVo = JSONObject.parseObject(reqStr, XHSProductResultVo.class);
            log.info("XHSProductDomain,格式化返回结果{}", JSONObject.toJSONString(resultVo));
            if (!"0000".equals(resultVo.getError_code())) {
                log.error("XHSProductDomain 调用目标异常");
                while (retryCount < MAX_RETRIES) {
                    String retryStr = HttpUtil.get(url);
                    XHSProductResultVo retryVo = JSONObject.parseObject(retryStr, XHSProductResultVo.class);
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
            for (int i = currentPage; i < totalPages ; i++) {
                String isReady =  redisTemplate.opsForValue().get("lawyer_task:"+taskId);
                if(isReady.equals("2")){
                    break;
                }
                int finalI = i;
                log.info("第{}页请求",i);
                CompletableFuture.runAsync(()->{
                    fetchDataFromRemote(finalI);
                });
            }
        } catch (Exception e) {
            log.error("XHSProductDomain 调用异常{}", e.getMessage());
        }

    }


    /**
     * 调用远程数据
     * @param page
     * @return
     */
    @Override
    public String fetchDataFromRemote(int page) {
        log.info("XHS: 页码为{},开始跑数据",page);
        url = "https://api-gw.onebound.cn/smallredbook/item_search/?key=" + OneBoundContants.key + "&secret=" + OneBoundContants.secret + "&q=" + keyword + "&start_price=0&end_price=0&page=" + page + "&cat=0&discount_only=&sort=&seller_info=no&nick=&seller_info=&nick=&ppath=&imgid=&filter=";
        boolean state = true;
        try {
            String remoteStr = HttpUtil.get(url);
            int retryCount = 0;
            log.info("XHSProductDomain 请求页码{},返回结果{}", page, JSONObject.toJSONString(remoteStr));
            XHSProductResultVo resultVo = JSONObject.parseObject(remoteStr, XHSProductResultVo.class);
            log.info("XHSProductDomain,格式化返回结果{}", JSONObject.toJSONString(resultVo));
            if (!"0000".equals(resultVo.getError_code())) {
                log.error("XHSProductDomain 调用目标异常");
                while (retryCount < MAX_RETRIES) {
                    String retryStr = HttpUtil.get(url);
                    XHSProductResultVo retryVo = JSONObject.parseObject(retryStr, XHSProductResultVo.class);
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
            log.error("XHSProductDomain 调用异常{}", e.getMessage());
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
    public void convertData(XHSProductResultVo vo){
        vo.getItems().getItem().stream().forEach(item->{
            LawyerTaskInfo lawyerTaskInfo = new LawyerTaskInfo();
            lawyerTaskInfo.setChannel("小红书");
            if(!StringUtils.isEmpty(item.getTitle())){
                lawyerTaskInfo.setProductTitle(item.getTitle());
            }
            if(!StringUtils.isEmpty(item.getDesc())){
                lawyerTaskInfo.setProductSummary(item.getDesc());
            }
            lawyerTaskInfo.setCommodityPrice(new BigDecimal(item.getPrice()));
            if(!StringUtils.isEmpty(item.getPic_url())){
                lawyerTaskInfo.setProductCover(item.getPic_url());
            }
            lawyerTaskInfo.setSalesVolume(item.getSales());
            lawyerTaskInfo.setShopName(item.getSeller_nick());
            lawyerTaskInfo.setProductLink(item.getDetail_url());
            lawyerTaskInfo.setTaskId(TASK_ID);
            lawyerTaskService.save(lawyerTaskInfo);
        });

    }

}