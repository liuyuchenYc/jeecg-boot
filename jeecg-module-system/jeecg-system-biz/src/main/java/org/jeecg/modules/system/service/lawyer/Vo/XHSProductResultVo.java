package org.jeecg.modules.system.service.lawyer.Vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class XHSProductResultVo {
    private ItemsBean items;
    private String error_code;
    private String reason;
    private String secache;
    private int secache_time;
    private String secache_date;
    private String translate_status;
    private int translate_time;
    private LanguageBean language;
    private String error;
    private int cache;
    private String api_info;
    private double execution_time;
    private String server_time;
    private String client_ip;
    private CallArgsBean call_args;
    private String api_type;
    private String translate_language;
    private String translate_engine;
    private String server_memory;
    private String request_id;

    @NoArgsConstructor
    @Data
    public static class ItemsBean {
        private String q;
        private int page;
        private int page_size;
        private int real_total_results;
        private int total_results;
        private int pagecount;
        private String data_from;
        private List<ItemBean> item;

        @NoArgsConstructor
        @Data
        public static class ItemBean {
            private String title;
            private String desc;
            private String num_iid;
            private double price;
            private int orginal_price;
            private double promotion_price;
            private String pic_url;
            private int sales;
            private String seller_id;
            private String seller_nick;
            private String detail_url;
        }
    }

    @NoArgsConstructor
    @Data
    public static class LanguageBean {
        private String default_lang;
        private String current_lang;
    }

    @NoArgsConstructor
    @Data
    public static class CallArgsBean {
        private String q;
    }
}
