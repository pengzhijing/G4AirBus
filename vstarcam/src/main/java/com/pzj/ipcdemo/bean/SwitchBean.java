package com.pzj.ipcdemo.bean;

import java.io.Serializable;

public class SwitchBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String motion_record_plan_enable; //移动侦测录像计划
	private String motion_push_plan_enable; //移动侦测推送计划
	private String record_plan_enable; //计划录像
	private int has_record_plan; //是否有计划录像
	public int getHas_record_plan() {
		return has_record_plan;
	}
	public void setHas_record_plan(int has_record_plan) {
		this.has_record_plan = has_record_plan;
	}


	private int oldfirmware; //区分新旧固件
	
	

	public int getOldfirmware() {
		return oldfirmware;
	}
	public void setOldfirmware(int oldfirmware) {
		this.oldfirmware = oldfirmware;
	}

	public String getMotion_record_plan_enable() {
		return motion_record_plan_enable;
	}
	public void setMotion_record_plan_enable(String motion_record_plan_enable) {
		this.motion_record_plan_enable = motion_record_plan_enable;
	}
	public String getMotion_push_plan_enable() {
		return motion_push_plan_enable;
	}
	public void setMotion_push_plan_enable(String motion_push_plan_enable) {
		this.motion_push_plan_enable = motion_push_plan_enable;
	}
	public String getRecord_plan_enable() {
		return record_plan_enable;
	}
	public void setRecord_plan_enable(String record_plan_enable) {
		this.record_plan_enable = record_plan_enable;
	}

	
	@Override
	public String toString() {
		return "SdcardBean [motion_record_plan_enable=" + motion_record_plan_enable + ", motion_push_plan_enable=" + motion_push_plan_enable
				+ ", record_plan_enable=" + record_plan_enable
				+ "]";
	}

}
