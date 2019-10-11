package com.pzj.ipcdemo.bean;

public class DoorBean {
	public String name;
	public int status;
	public String sensoridTag;
	
	public DoorBean() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSensoridTag() {
		return sensoridTag;
	}

	public void setSensoridTag(String sensoridTag) {
		this.sensoridTag = sensoridTag;
	}

	@Override
	public String toString() {
		return "DoorBean [name=" + name + ", status=" + status
				+ ", sensoridTag=" + sensoridTag + "]";
	}

	
	
	
	
	
	
}
