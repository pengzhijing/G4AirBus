package com.pzj.ipcdemo.utils;

import com.pzj.ipcdemo.bean.DoorBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SensorDoorData {

	public static final String TAG = "SensorDoorData";

	public static ArrayList<Map<String, ArrayList<DoorBean>>> sensordoorItems = new ArrayList<Map<String, ArrayList<DoorBean>>>();// did，门磁传感器list

	public static void AddSensor(String did, DoorBean door) {
//		Log.i("info", "AddSensor add==========" + did
//				+ ".......door.toString():" + door.toString());
		String doortag = door.getSensoridTag();

		if (CheckSensorDoor(did, doortag)) {
		//	Log.i(TAG, "new sensordoor:" + sensordoorItems.toString());
			int size = sensordoorItems.size();
			if (size == 0) {
			//	Log.i(TAG, "size==0");
				ArrayList<DoorBean> newList = new ArrayList<DoorBean>();
				newList.add(door);
				Map<String, ArrayList<DoorBean>> map = new HashMap<String, ArrayList<DoorBean>>();
				map.put(did, newList);
				sensordoorItems.add(map);
			} else {
				for (int i = 0; i < size; i++) {
					if (sensordoorItems.get(i).containsKey(did)) {
					//	Log.i(TAG, "size==1111111"+i);
						Map<String, ArrayList<DoorBean>> map = sensordoorItems
								.get(i);
						ArrayList<DoorBean> doorList = map.get(did);
						doorList.add(door);
					}else{
						//Log.i(TAG, "size==2222222222"+i);
						ArrayList<DoorBean> newList = new ArrayList<DoorBean>();
						newList.add(door);
						Map<String, ArrayList<DoorBean>> map = new HashMap<String, ArrayList<DoorBean>>();
						map.put(did, newList);
						sensordoorItems.add(map);
					}
				}

			}

		}

	}

	private static boolean CheckSensorDoor(String did, String tag) {
		// TODO Auto-generated method stub
		int size = sensordoorItems.size();
		int i;
		for (i = 0; i < size; i++) {
			if (sensordoorItems.get(i).containsKey(did)) {
				ArrayList<DoorBean> list = sensordoorItems.get(i).get(did);
				int num = list.size();
				for (int j = 0; j < num; j++) {
					DoorBean door = list.get(j);
					String doortag = door.getSensoridTag();
					if (tag.equals(doortag)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static ArrayList<DoorBean> getSensorDoorBeanList(String did) {

		int size = sensordoorItems.size();
		for (int i = 0; i < size; i++) {
			if (sensordoorItems.get(i).containsKey(did)) {
				ArrayList<DoorBean> list = sensordoorItems.get(i).get(did);
				return list;
			}
		}

		return null;
	}
	
	public static void ChangeDoorOpenCloseStatus(String did, String tag, int status){
		int size = sensordoorItems.size();
		for (int i = 0; i < size; i++) {
			if (sensordoorItems.get(i).containsKey(did)) {
				ArrayList<DoorBean> list = sensordoorItems.get(i).get(did);
				int num=list.size();
				for(int j=0;j<num;j++){
					DoorBean bean=list.get(j);
					String doortag=bean.getSensoridTag();
					if(doortag.equals(tag)){
						bean.setStatus(status);
					}
				}
			}
		}

		
	}
	
	public static void ChangeDoorName(String did, String tag, String name){
		int size = sensordoorItems.size();
		for (int i = 0; i < size; i++) {
			if (sensordoorItems.get(i).containsKey(did)) {
				ArrayList<DoorBean> list = sensordoorItems.get(i).get(did);
				int num=list.size();
				for(int j=0;j<num;j++){
					DoorBean bean=list.get(j);
					String doortag=bean.getSensoridTag();
					if(doortag.equals(tag)){
						bean.setName(name);
					}
				}
			}
		}
	}
	
	public static void removeSnesorDoor(String did, String tag){
		int size = sensordoorItems.size();
		for (int i = 0; i < size; i++) {
			if (sensordoorItems.get(i).containsKey(did)) {
				ArrayList<DoorBean> list = sensordoorItems.get(i).get(did);
				int num=list.size();
				for(int j=0;j<num;j++){
					DoorBean bean=list.get(j);
					String doortag=bean.getSensoridTag();
					if(doortag.equals(tag)){
						sensordoorItems.remove(i);
					}
				}
			}
		}
		
		
	}
	
	

}
