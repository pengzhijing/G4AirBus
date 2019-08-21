package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-5-3.
 */
public class seclogdata {
        public String Date,Time,Address,Channel,Type;
        public int lognum;
        public seclogdata() {
        }

        public seclogdata(int lognum,String Date, String Time, String Address, String Channel,String Type) {
            this.lognum=lognum;
            this.Date = Date;
            this.Time = Time;
            this.Address = Address;
            this.Channel=Channel;
            this.Type=Type;
        }

}
