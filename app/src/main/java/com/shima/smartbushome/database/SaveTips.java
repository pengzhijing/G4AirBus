package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-5-3.
 */
public class SaveTips {
        public int _id;
        public String title;
        public String date;
        public String detail;
        public String url;

        public SaveTips() {
        }

        public SaveTips(String title, String date,String detail,String url) {
            this.title = title;
            this.date = date;
            this.detail = detail;
            this.url=url;
        }

}
