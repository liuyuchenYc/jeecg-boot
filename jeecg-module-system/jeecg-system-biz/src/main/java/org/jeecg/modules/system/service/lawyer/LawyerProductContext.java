package org.jeecg.modules.system.service.lawyer;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.service.lawyer.strategy.LawyerProductStrategy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LawyerProductContext  implements ApplicationContextAware {
    private Map<String, LawyerProductStrategy> beanMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //获取指定接口的实现类
        Map<String, LawyerProductStrategy> map = applicationContext.getBeansOfType(LawyerProductStrategy.class);
        this.beanMap = new HashMap<>(map.size());
        map.forEach((k, v) -> this.beanMap.put(k,v));
        log.info("beanMap信息{}", JSONObject.toJSONString(beanMap));
    }

    /**
     * 通过策略key获取具体的对象
     */
    public <T extends LawyerProductStrategy> T getProxy(String code) {
        return (T)this.beanMap.get(code);
    }
}
