package org.jeecg.modules.system.service.lawyer.strategy;

/**
 * 快手数据
 * 商品关键字搜索
 */
public interface LawyerProductStrategy<T> {

    void doItemSearch (String keyword,String taskId);

    void convertData(T t);

    String fetchDataFromRemote(int page);
}
