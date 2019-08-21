package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-6-27.
 */
public class Saveenergydata {
    public int energy_id,channel1_value,channel2_value,channel3_value,channel4_value,channel5_value,channel6_value,
    channel7_value,channel8_value,channel9_value,channel10_value,channel11_value,channel12_value,channel13_value,
            channel14_value,channel15_value,channel16_value,channel17_value,channel18_value,channel19_value,channel20_value,
            channel21_value,channel22_value,channel23_value,channel24_value;
    public String Time;
    public Saveenergydata(){

    }

    public Saveenergydata(int energy_id,int channel1_value, int channel2_value,int channel3_value, int channel4_value,
                          int channel5_value, int channel6_value, int channel7_value,int channel8_value, int channel9_value,
                          int channel10_value,  int channel11_value,  int channel12_value,  int channel13_value,  int channel14_value,
                          int channel15_value,  int channel16_value,  int channel17_value,  int channel18_value,  int channel19_value,
                          int channel20_value,  int channel21_value,  int channel22_value,  int channel23_value,  int channel24_value,
                          String Time){
        this.energy_id=energy_id;
        this.channel1_value=channel1_value;
        this.channel2_value=channel2_value;
        this.channel3_value=channel3_value;
        this.channel4_value=channel4_value;
        this.channel5_value=channel5_value;
        this.channel6_value=channel6_value;
        this.channel7_value=channel7_value;
        this.channel8_value=channel8_value;
        this.channel9_value=channel9_value;
        this.channel10_value=channel10_value;
        this.channel11_value=channel11_value;
        this.channel12_value=channel12_value;
        this.channel13_value=channel13_value;
        this.channel14_value=channel14_value;
        this.channel15_value=channel15_value;
        this.channel16_value=channel16_value;
        this.channel17_value=channel17_value;
        this.channel18_value=channel18_value;
        this.channel19_value=channel19_value;
        this.channel20_value=channel20_value;
        this.channel21_value=channel21_value;
        this.channel22_value=channel22_value;
        this.channel23_value=channel23_value;
        this.channel24_value=channel24_value;
        this.Time=Time;


    }
}
