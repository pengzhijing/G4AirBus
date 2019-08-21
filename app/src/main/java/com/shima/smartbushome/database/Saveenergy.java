package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-6-27.
 */
public class Saveenergy {
    public int energy_id,subnetID,deviceID;
    public String energyname;
    public String channel1_name,channel2_name,channel3_name,channel4_name,channel5_name,channel6_name,channel7_name,
            channel8_name,channel9_name,channel10_name,channel11_name,channel12_name,channel13_name,channel14_name,
            channel15_name,channel16_name,channel17_name,channel18_name,channel19_name,channel20_name,channel21_name,
            channel22_name,channel23_name,channel24_name;
    public Saveenergy(){

    }

    public Saveenergy(int energy_id, int subnetID, int deviceID
            ,String energyname,String channel1_name,String channel2_name,String channel3_name,String channel4_name,
                      String channel5_name,String channel6_name,String channel7_name,String channel8_name,
                      String channel9_name,String channel10_name,String channel11_name,String channel12_name,
                      String channel13_name,String channel14_name,String channel15_name,String channel16_name,
                      String channel17_name,String channel18_name,String channel19_name,String channel20_name,
                      String channel21_name,String channel22_name,String channel23_name,String channel24_name){
        this.energy_id=energy_id;
        this.subnetID=subnetID;
        this.deviceID=deviceID;
        this.energyname=energyname;
        this.channel1_name=channel1_name;
        this.channel2_name=channel2_name;
        this.channel3_name=channel3_name;
        this.channel4_name=channel4_name;
        this.channel5_name=channel5_name;
        this.channel6_name=channel6_name;
        this.channel7_name=channel7_name;
        this.channel8_name=channel8_name;
        this.channel9_name=channel9_name;
        this.channel10_name=channel10_name;
        this.channel11_name=channel11_name;
        this.channel12_name=channel12_name;
        this.channel13_name=channel13_name;
        this.channel14_name=channel14_name;
        this.channel15_name=channel15_name;
        this.channel16_name=channel16_name;
        this.channel17_name=channel17_name;
        this.channel18_name=channel18_name;
        this.channel19_name=channel19_name;
        this.channel20_name=channel20_name;
        this.channel21_name=channel21_name;
        this.channel22_name=channel22_name;
        this.channel23_name=channel23_name;
        this.channel24_name=channel24_name;
    }
}
