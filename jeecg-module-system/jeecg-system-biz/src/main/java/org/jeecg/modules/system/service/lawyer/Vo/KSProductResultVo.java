package org.jeecg.modules.system.service.lawyer.Vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
@Data
public class KSProductResultVo {

    private KSProductResultVo.ItemsBean items;
    private String error_code;
    private String reason;
    private String secache;
    private int secache_time;
    private String secache_date;
    private String translate_status;
    private int translate_time;
    private JdProductResultVo.LanguageBean language;
    private String error;
    private int cache;
    private String api_info;
    private double execution_time;
    private String server_time;
    private String client_ip;
    private JdProductResultVo.CallArgsBean call_args;
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
        private List<KSProductResultVo.ItemsBean.ItemBean> item;
        private Object nav_catcamp;
        private Object nav_filter;

        @NoArgsConstructor
        @Data
        public static class ItemBean {
            private String title;
            private String pic_url;
            private String promotion_price;
            private String price;
            private String num_iid;
            private int seller_id;
            private String shop_name;
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
        private String start_price;
        private String end_price;
        private String page;
        private String cat;
    }


}
