package org.jeecg.modules.system.service.lawyer.Vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class JdProductResultVo {

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
        private String url;
        private String keyword;
        private String page;
        private String real_total_results;
        private int total_results;
        private int pagecount;
        private String products_total;
        private String list_count;
        private String page_size;
        private List<ItemBean> item;
        private Object nav_catcamp;
        private Object nav_filter;

        @NoArgsConstructor
        @Data
        public static class ItemBean {
            private String num_iid;
            private String detail_url;
            private String title;
            private String pic_url;
            private String price;
            private int promotion_price;
            private Object sales;
            private String sample_id;
            private String post_fee;
            private String area;
            private String seller;
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
        private String start_price;
        private String end_price;
        private String page;
        private String cat;
    }
}
