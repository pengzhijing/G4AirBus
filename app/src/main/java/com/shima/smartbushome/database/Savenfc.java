package com.shima.smartbushome.database;

/**
 * Created by Administrator on 16-6-27.
 */
public class Savenfc {
    public int nfc_id,state,action_type,marco_ID,resume1,delaytime;
    public String nfc_name,nfc_icon,nfc_content,marco_name,call_num,message,resume2;
    public Savenfc(){

    }
    public Savenfc(int nfc_id, String nfc_name, String nfc_icon
            , String nfc_content, int state, int action_type, int marco_ID, String marco_name,String call_num,
                   String message,int resume1,String resume2,int delaytime){
        this.nfc_id=nfc_id;
        this.nfc_name=nfc_name;
        this.nfc_icon=nfc_icon;
        this.nfc_content=nfc_content;
        this.state=state;
        this.action_type=action_type;
        this.marco_ID=marco_ID;
        this.marco_name=marco_name;
        this.call_num=call_num;
        this.message=message;
        this.resume1=resume1;
        this.resume2=resume2;
        this.delaytime=delaytime;
    }
}
