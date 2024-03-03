package org.jeecg.modules.system.service.lawyer.Vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class TaobaoProductVo {


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
    private String execution_time;
    private String server_time;
    private String client_ip;
    private CallArgsBean call_args;
    private String api_type;
    private String translate_language;
    private String translate_engine;
    private String server_memory;
    private String request_id;
    private String last_id;

    @NoArgsConstructor
    @Data
    public static class ItemsBean {
        private String page;
        private String real_total_results;
        private String total_results;
        private int page_size;
        private String pagecount;
        private String data_from;
        private List<ItemBean> item;
        private int item_weight_update;

        @NoArgsConstructor
        @Data
        public static class ItemBean {
            private String title;
            private String pic_url;
            private String promotion_price;
            private String orginal_price;
            private String price;
            private int sales;
            private String num_iid;
            private String detail_url;
            private String area;
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
