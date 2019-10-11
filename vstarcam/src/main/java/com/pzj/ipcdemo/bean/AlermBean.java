package com.pzj.ipcdemo.bean;

import java.io.Serializable;

public class AlermBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String did;
	private int alarm_audio;
	/**
	 * 是否勾选
	 */
	private int audioArmedCheck;
	private int alarm_temp;
	private int alarmTempChecked;
	private int motion_armed;
	private int motion_sensitivity;
	private int input_armed;
	private int ioin_level;
	private int ioout_level;
	private int iolinkage;
	private int alermpresetsit;
	private int mail;
	private int snapshot;
	private int record;
	private int upload_interval;
	private int schedule_enable;
	private int schedule_sun_0;
	private int schedule_sun_1;
	private int schedule_sun_2;
	private int schedule_mon_0;
	private int schedule_mon_1;
	private int schedule_mon_2;
	private int schedule_tue_0;
	private int schedule_tue_1;
	private int schedule_tue_2;
	private int schedule_wed_0;
	private int schedule_wed_1;
	private int schedule_wed_2;

	private int schedule_thu_0;
	private int schedule_thu_1;
	private int schedule_thu_2;
	private int schedule_fri_0;
	private int schedule_fri_1;
	private int schedule_fri_2;
	private int schedule_sat_0;
	private int schedule_sat_1;
	private int schedule_sat_2;
	
	int defense_plan1;
	int defense_plan2;
	int defense_plan3;
	int defense_plan4; 
	int defense_plan5;
	int defense_plan6;
	int defense_plan7; 
	int defense_plan8;
	int defense_plan9; 
	int defense_plan10; 
	int defense_plan11;
	int defense_plan12; 
	int defense_plan13; 
	int defense_plan14;
	int defense_plan15; 
	int defense_plan16; 
	int defense_plan17;
	int defense_plan18; 
	int defense_plan19; 
	int defense_plan20;
	int defense_plan21;
	public int getAudioArmedCheck() {
		return audioArmedCheck;
	}

	public void setAudioArmedCheck(int audioArmedCheck) {
		this.audioArmedCheck = audioArmedCheck;
	}
	public int getAlarmTempChecked() {
		return alarmTempChecked;
	}

	public void setAlarmTempChecked(int alarmTempChecked) {
		this.alarmTempChecked = alarmTempChecked;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public int getAlarm_temp() {
		return alarm_temp;
	}

	public void setAlarm_temp(int alarm_temp) {
		this.alarm_temp = alarm_temp;
	}

	public int getAlarm_audio() {
		return alarm_audio;
	}

	public void setAlarm_audio(int alarm_audio) {
		this.alarm_audio = alarm_audio;
	}

	public int getMotion_armed() {
		return motion_armed;
	}

	public void setMotion_armed(int motion_armed) {
		this.motion_armed = motion_armed;
	}

	public int getMotion_sensitivity() {
		return motion_sensitivity;
	}

	public void setMotion_sensitivity(int motion_sensitivity) {
		this.motion_sensitivity = motion_sensitivity;
	}

	public int getInput_armed() {
		return input_armed;
	}

	public void setInput_armed(int input_armed) {
		this.input_armed = input_armed;
	}

	public int getIoin_level() {
		return ioin_level;
	}

	public int getIoout_level() {
		return ioout_level;
	}

	public void setIoout_level(int ioout_level) {
		this.ioout_level = ioout_level;
	}

	public void setIoin_level(int ioin_level) {
		this.ioin_level = ioin_level;
	}

	public int getIolinkage() {
		return iolinkage;
	}

	public void setIolinkage(int iolinkage) {
		this.iolinkage = iolinkage;
	}

	public int getAlermpresetsit() {
		return alermpresetsit;
	}

	public void setAlermpresetsit(int alermpresetsit) {
		this.alermpresetsit = alermpresetsit;
	}

	public int getMail() {
		return mail;
	}

	public void setMail(int mail) {
		this.mail = mail;
	}

	public int getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(int snapshot) {
		this.snapshot = snapshot;
	}

	public int getRecord() {
		return record;
	}

	public void setRecord(int record) {
		this.record = record;
	}

	public int getUpload_interval() {
		return upload_interval;
	}

	public void setUpload_interval(int upload_interval) {
		this.upload_interval = upload_interval;
	}

	public int getSchedule_enable() {
		return schedule_enable;
	}

	public void setSchedule_enable(int schedule_enable) {
		this.schedule_enable = schedule_enable;
	}

	public int getSchedule_sun_0() {
		return schedule_sun_0;
	}

	public void setSchedule_sun_0(int schedule_sun_0) {
		this.schedule_sun_0 = schedule_sun_0;
	}

	public int getSchedule_sun_1() {
		return schedule_sun_1;
	}

	public void setSchedule_sun_1(int schedule_sun_1) {
		this.schedule_sun_1 = schedule_sun_1;
	}

	public int getSchedule_sun_2() {
		return schedule_sun_2;
	}

	public void setSchedule_sun_2(int schedule_sun_2) {
		this.schedule_sun_2 = schedule_sun_2;
	}

	public int getSchedule_mon_0() {
		return schedule_mon_0;
	}

	public void setSchedule_mon_0(int schedule_mon_0) {
		this.schedule_mon_0 = schedule_mon_0;
	}

	public int getSchedule_mon_1() {
		return schedule_mon_1;
	}

	public void setSchedule_mon_1(int schedule_mon_1) {
		this.schedule_mon_1 = schedule_mon_1;
	}

	public int getSchedule_mon_2() {
		return schedule_mon_2;
	}

	public void setSchedule_mon_2(int schedule_mon_2) {
		this.schedule_mon_2 = schedule_mon_2;
	}

	public int getSchedule_tue_0() {
		return schedule_tue_0;
	}

	public void setSchedule_tue_0(int schedule_tue_0) {
		this.schedule_tue_0 = schedule_tue_0;
	}

	public int getSchedule_tue_1() {
		return schedule_tue_1;
	}

	public void setSchedule_tue_1(int schedule_tue_1) {
		this.schedule_tue_1 = schedule_tue_1;
	}

	public int getSchedule_tue_2() {
		return schedule_tue_2;
	}

	public void setSchedule_tue_2(int schedule_tue_2) {
		this.schedule_tue_2 = schedule_tue_2;
	}

	public int getSchedule_wed_0() {
		return schedule_wed_0;
	}

	public void setSchedule_wed_0(int schedule_wed_0) {
		this.schedule_wed_0 = schedule_wed_0;
	}

	public int getSchedule_wed_1() {
		return schedule_wed_1;
	}

	public void setSchedule_wed_1(int schedule_wed_1) {
		this.schedule_wed_1 = schedule_wed_1;
	}

	public int getSchedule_wed_2() {
		return schedule_wed_2;
	}

	public void setSchedule_wed_2(int schedule_wed_2) {
		this.schedule_wed_2 = schedule_wed_2;
	}

	public int getSchedule_thu_0() {
		return schedule_thu_0;
	}

	public void setSchedule_thu_0(int schedule_thu_0) {
		this.schedule_thu_0 = schedule_thu_0;
	}

	public int getSchedule_thu_1() {
		return schedule_thu_1;
	}

	public void setSchedule_thu_1(int schedule_thu_1) {
		this.schedule_thu_1 = schedule_thu_1;
	}

	public int getSchedule_thu_2() {
		return schedule_thu_2;
	}

	public void setSchedule_thu_2(int schedule_thu_2) {
		this.schedule_thu_2 = schedule_thu_2;
	}

	public int getSchedule_fri_0() {
		return schedule_fri_0;
	}

	public void setSchedule_fri_0(int schedule_fri_0) {
		this.schedule_fri_0 = schedule_fri_0;
	}

	public int getSchedule_fri_1() {
		return schedule_fri_1;
	}

	public void setSchedule_fri_1(int schedule_fri_1) {
		this.schedule_fri_1 = schedule_fri_1;
	}

	public int getSchedule_fri_2() {
		return schedule_fri_2;
	}

	public void setSchedule_fri_2(int schedule_fri_2) {
		this.schedule_fri_2 = schedule_fri_2;
	}

	public int getSchedule_sat_0() {
		return schedule_sat_0;
	}

	public void setSchedule_sat_0(int schedule_sat_0) {
		this.schedule_sat_0 = schedule_sat_0;
	}

	public int getSchedule_sat_1() {
		return schedule_sat_1;
	}

	public void setSchedule_sat_1(int schedule_sat_1) {
		this.schedule_sat_1 = schedule_sat_1;
	}

	public int getSchedule_sat_2() {
		return schedule_sat_2;
	}

	public void setSchedule_sat_2(int schedule_sat_2) {
		this.schedule_sat_2 = schedule_sat_2;
	}

	public int getDefense_plan1() {
		return defense_plan1;
	}
	public void setDefense_plan1(int defense_plan1) {
		this.defense_plan1 = defense_plan1;
	}
	public int getDefense_plan2() {
		return defense_plan2;
	}
	public void setDefense_plan2(int defense_plan2) {
		this.defense_plan2 = defense_plan2;
	}
	public int getDefense_plan3() {
		return defense_plan3;
	}
	public void setDefense_plan3(int defense_plan3) {
		this.defense_plan3 = defense_plan3;
	}
	public int getDefense_plan4() {
		return defense_plan4;
	}
	public void setDefense_plan4(int defense_plan4) {
		this.defense_plan4 = defense_plan4;
	}
	public int getDefense_plan5() {
		return defense_plan5;
	}
	public void setDefense_plan5(int defense_plan5) {
		this.defense_plan5 = defense_plan5;
	}
	public int getDefense_plan6() {
		return defense_plan6;
	}
	public void setDefense_plan6(int defense_plan6) {
		this.defense_plan6 = defense_plan6;
	}
	public int getDefense_plan7() {
		return defense_plan7;
	}
	public void setDefense_plan7(int defense_plan7) {
		this.defense_plan7 = defense_plan7;
	}
	public int getDefense_plan8() {
		return defense_plan8;
	}
	public void setDefense_plan8(int defense_plan8) {
		this.defense_plan8 = defense_plan8;
	}
	public int getDefense_plan9() {
		return defense_plan9;
	}
	public void setDefense_plan9(int defense_plan9) {
		this.defense_plan9 = defense_plan9;
	}
	public int getDefense_plan10() {
		return defense_plan10;
	}
	public void setDefense_plan10(int defense_plan10) {
		this.defense_plan10 = defense_plan10;
	}
	public int getDefense_plan11() {
		return defense_plan11;
	}
	public void setDefense_plan11(int defense_plan11) {
		this.defense_plan11 = defense_plan11;
	}
	public int getDefense_plan12() {
		return defense_plan12;
	}
	public void setDefense_plan12(int defense_plan12) {
		this.defense_plan12 = defense_plan12;
	}
	public int getDefense_plan13() {
		return defense_plan13;
	}
	public void setDefense_plan13(int defense_plan13) {
		this.defense_plan13 = defense_plan13;
	}
	public int getDefense_plan14() {
		return defense_plan14;
	}
	public void setDefense_plan14(int defense_plan14) {
		this.defense_plan14 = defense_plan14;
	}
	public int getDefense_plan15() {
		return defense_plan15;
	}
	public void setDefense_plan15(int defense_plan15) {
		this.defense_plan15 = defense_plan15;
	}
	public int getDefense_plan16() {
		return defense_plan16;
	}
	public void setDefense_plan16(int defense_plan16) {
		this.defense_plan16 = defense_plan16;
	}
	public int getDefense_plan17() {
		return defense_plan17;
	}
	public void setDefense_plan17(int defense_plan17) {
		this.defense_plan17 = defense_plan17;
	}
	public int getDefense_plan18() {
		return defense_plan18;
	}
	public void setDefense_plan18(int defense_plan18) {
		this.defense_plan18 = defense_plan18;
	}
	public int getDefense_plan19() {
		return defense_plan19;
	}
	public void setDefense_plan19(int defense_plan19) {
		this.defense_plan19 = defense_plan19;
	}
	public int getDefense_plan20() {
		return defense_plan20;
	}
	public void setDefense_plan20(int defense_plan20) {
		this.defense_plan20 = defense_plan20;
	}
	public int getDefense_plan21() {
		return defense_plan21;
	}
	public void setDefense_plan21(int defense_plan21) {
		this.defense_plan21 = defense_plan21;
	}
	@Override
	public String toString() {
		return "AlermBean [did=" + did + ", motion_armed=" + motion_armed
				+ ", motion_sensitivity=" + motion_sensitivity
				+ ", input_armed=" + input_armed + ", ioin_level=" + ioin_level
				+ ", ioout_level=" + ioout_level + ", iolinkage=" + iolinkage
				+ ", alermpresetsit=" + alermpresetsit + ", mail=" + mail
				+ ", snapshot=" + snapshot + ", record=" + record
				+ ", alarm_audio=" + alarm_audio + ", upload_interval="
				+ upload_interval + ", schedule_enable=" + schedule_enable
				+ ", schedule_sun_0=" + schedule_sun_0 + ", schedule_sun_1="
				+ schedule_sun_1 + ", schedule_sun_2=" + schedule_sun_2
				+ ", schedule_mon_0=" + schedule_mon_0 + ", schedule_mon_1="
				+ schedule_mon_1 + ", schedule_mon_2=" + schedule_mon_2
				+ ", schedule_tue_0=" + schedule_tue_0 + ", schedule_tue_1="
				+ schedule_tue_1 + ", schedule_tue_2=" + schedule_tue_2
				+ ", schedule_wed_0=" + schedule_wed_0 + ", schedule_wed_1="
				+ schedule_wed_1 + ", schedule_wed_2=" + schedule_wed_2
				+ ", schedule_thu_0=" + schedule_thu_0 + ", schedule_thu_1="
				+ schedule_thu_1 + ", schedule_thu_2=" + schedule_thu_2
				+ ", schedule_fri_0=" + schedule_fri_0 + ", schedule_fri_1="
				+ schedule_fri_1 + ", schedule_fri_2=" + schedule_fri_2
				+ ", schedule_sat_0=" + schedule_sat_0 + ", schedule_sat_1="
				+ schedule_sat_1 + ", schedule_sat_2=" + schedule_sat_2
				+ ", defense_plan1=" + defense_plan1 + ", defense_plan2="
				+ defense_plan2 + ", defense_plan3=" + defense_plan3
				+ ", defense_plan4=" + defense_plan4 + ", defense_plan5="
				+ defense_plan5 + ", defense_plan6=" + defense_plan6
				+ ", defense_plan7=" + defense_plan7 + ", defense_plan8="
				+ defense_plan8 + ", defense_plan9=" + defense_plan9
				+ ", defense_plan10=" + defense_plan10 + ", defense_plan11="
				+ defense_plan11 + ", defense_plan12=" + defense_plan12
				+ ", defense_plan13=" + defense_plan13 + ", defense_plan14="
				+ defense_plan14 + ", defense_plan15=" + defense_plan15
				+ ", defense_plan16=" + defense_plan16 + ", defense_plan17="
				+ defense_plan17 + ", defense_plan18=" + defense_plan18
				+ ", defense_plan19=" + defense_plan19 + ", defense_plan20="
				+ defense_plan20 + ", defense_plan21=" + defense_plan21 + "]";
	}

}
