package com.pzj.ipcdemo.bean;

import java.io.Serializable;

public class WifiBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String did;
	private int enable;
	private String ssid;
	private int channel;
	private int mode;
	private int authtype;
	private int encryp;
	private int keyformat;
	private int defkey;
	private String key1;
	private String key2;
	private String key3;
	private String key4;
	private int key1_bits;
	private int key2_bits;
	private int key3_bits;
	private int key4_bits;
	private String wpa_psk;
	private int dbm0;

	public int getDbm0() {
		return dbm0;
	}

	public void setDbm0(int dbm0) {
		this.dbm0 = dbm0;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getAuthtype() {
		return authtype;
	}

	public void setAuthtype(int authtype) {
		this.authtype = authtype;
	}

	public int getEncryp() {
		return encryp;
	}

	public void setEncryp(int encryp) {
		this.encryp = encryp;
	}

	public int getKeyformat() {
		return keyformat;
	}

	public void setKeyformat(int keyformat) {
		this.keyformat = keyformat;
	}

	public int getDefkey() {
		return defkey;
	}

	public void setDefkey(int defkey) {
		this.defkey = defkey;
	}

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}

	public String getKey3() {
		return key3;
	}

	public void setKey3(String key3) {
		this.key3 = key3;
	}

	public String getKey4() {
		return key4;
	}

	public void setKey4(String key4) {
		this.key4 = key4;
	}

	public int getKey1_bits() {
		return key1_bits;
	}

	public void setKey1_bits(int key1_bits) {
		this.key1_bits = key1_bits;
	}

	public int getKey2_bits() {
		return key2_bits;
	}

	public void setKey2_bits(int key2_bits) {
		this.key2_bits = key2_bits;
	}

	public int getKey3_bits() {
		return key3_bits;
	}

	public void setKey3_bits(int key3_bits) {
		this.key3_bits = key3_bits;
	}

	public int getKey4_bits() {
		return key4_bits;
	}

	public void setKey4_bits(int key4_bits) {
		this.key4_bits = key4_bits;
	}

	public String getWpa_psk() {
		return wpa_psk;
	}

	public void setWpa_psk(String wpa_psk) {
		this.wpa_psk = wpa_psk;
	}

	@Override
	public String toString() {
		return "WifiBean [did=" + did + ", enable=" + enable + ", ssid=" + ssid
				+ ", channel=" + channel + ", mode=" + mode + ", authtype="
				+ authtype + ", encryp=" + encryp + ", keyformat=" + keyformat
				+ ", defkey=" + defkey + ", key1=" + key1 + ", key2=" + key2
				+ ", key3=" + key3 + ", key4=" + key4 + ", key1_bits="
				+ key1_bits + ", key2_bits=" + key2_bits + ", key3_bits="
				+ key3_bits + ", key4_bits=" + key4_bits + ", wpa_psk="
				+ wpa_psk + "]";
	}

}
