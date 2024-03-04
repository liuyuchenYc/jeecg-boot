package org.jeecg.modules.system.service.lawyer.Vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DouYinContentResultVo {


    private String page;
    private int real_total_results;
    private int total_results;
    private int page_size;
    private int pagecount;
    private List<ItemBean> item;
    private String _ddf;
    private String error;
    private String reason;
    private String error_code;
    private int cache;
    private String api_info;
    private String execution_time;
    private String server_time;
    private String client_ip;
    private List<?> call_args;
    private String api_type;
    private String translate_language;
    private String translate_engine;
    private String server_memory;
    private boolean last_id;

    @NoArgsConstructor
    @Data
    public static class ItemBean {
        private String title;
        private String num_iid;
        private String nick;
        private String signature;
        private String city;
        private String uid;
        private String detail_url;
    }
}
