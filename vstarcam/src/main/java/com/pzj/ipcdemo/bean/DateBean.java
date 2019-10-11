package com.pzj.ipcdemo.bean;

public class DateBean {
	private int now;
	private int tz;
	private int ntp_enable;
	private String ntp_ser;
	public int getNow() {
		return now;
	}
	public void setNow(int now) {
		this.now = now;
	}
	public int getTz() {
		return tz;
	}
	public void setTz(int tz) {
		this.tz = tz;
	}
	public int getNtp_enable() {
		return ntp_enable;
	}
	public void setNtp_enable(int ntp_enable) {
		this.ntp_enable = ntp_enable;
	}
	public String getNtp_ser() {
		return ntp_ser;
	}
	public void setNtp_ser(String ntp_ser) {
		this.ntp_ser = ntp_ser;
	}
	@Override
	public String toString() {
		return "DateBean [now=" + now + ", tz=" + tz + ", ntp_enable="
				+ ntp_enable + ", ntp_ser=" + ntp_ser + "]";
	}
	
}
