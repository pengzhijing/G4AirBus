package com.pzj.ipcdemo.bean;

import java.io.Serializable;

public class WifiScanBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String did;
	private String ssid;
	private String mac;
	private int security;
	private int dbm0;
	private int dbm1;
	private int mode;
	private int channel;

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getSecurity() {
		return security;
	}

	public void setSecurity(int security) {
		this.security = security;
	}

	public int getDbm0() {
		return dbm0;
	}

	public void setDbm0(int dbm0) {
		this.dbm0 = dbm0;
	}

	public int getDbm1() {
		return dbm1;
	}

	public void setDbm1(int dbm1) {
		this.dbm1 = dbm1;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "WifiScanBean [did=" + did + ", ssid=" + ssid + ", mac=" + mac
				+ ", security=" + security + ", dbm0=" + dbm0 + ", dbm1="
				+ dbm1 + ", mode=" + mode + ", channel=" + channel + "]";
	}

}
