package com.shima.smartbushome.udp;

//import java.net.DatagramPacket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class udp_socket  {
	public final static String ACTION_DATA_IN =
			"com.example.udp.DATA_IN";
	public static boolean startrec=false,startsent=false;
	// CONST
	private static final int CONST_UDP_UDP_PORT = 6000;// udp端口
	private static final int CONST_MAX_UPD_PACKET_LEN = 2048;// udp包长度为1024字节，最大
	private static final int CONST_MAX_TIMES_OF_SEND = 2;// 最多可发送2次

	private static final short CONST_SELF_SUBNET_ID = (short) 0xBB;// 187.定义自己子网ID号
	private static final short CONST_SELF_DEVICE_ID = (short) 0xBB;// 1787.定义自己设备ID号
	private static final short CONST_SELF_DEVICE_TYPE_H = (short) 0xCC;// 204
	private static final short CONST_SELF_DEVICE_TYPE_L = (short) 0xCC;
	private static final int CONST_TIME_OUT_FOR_TOTAL_WAIT = 4000; // millisecond
	private static final int CONST_TIME_OUT_FOR_TOTAL_WAIT_RS232 = 4000; // millisecond
	private static final int CONST_TIME_OUT_FOR_TOTAL_WAIT_ALBUM = 5000; // millisecond
	private static final int CONST_TIME_OUT_FOR_EACH_WAIT = 1000; // millisecond
	// command type define命令类型定义
	private static final int CONST_CMD_TYPE_SCENE = 0;
	private static final int CONST_CMD_TYPE_SEQUENCE = 1;
	private static final int CONST_CMD_TYPE_UNIVERSAL_SWITCH = 2;
	private static final int CONST_CMD_TYPE_INVALID = 3;
	private static final int CONST_CMD_TYPE_SINGLE_CH = 4;
	private static final int CONST_CMD_TYPE_BR_SCENE = 5;
	private static final int CONST_CMD_TYPE_BR_CH = 6;
	private static final int CONST_CMD_TYPE_CURTAIN = 7;
	private static final int CONST_CMD_TYPE_TIMER = 8;
	private static final int CONST_CMD_TYPE_GPRS = 9;
	private static final int CONST_CMD_TYPE_PANEL = 10;
	public static DatagramSocket moUDPSocket=null;
	DatagramPacket oDataPacket;
	public static DatagramPacket packet;
	public static int SMARTSERVICE_PORT=8888;
	Handler senthandler=new Handler();
	Handler revhandler=new Handler();
	Context mcontext;
	byte[] buf = new byte[2048];
	/* CRCtable CRC校验表 */
	private static final int[] mbufintCRCTable = { 0x0000, 0x1021, 0x2042,
			0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7, 0x8108, 0x9129, 0xa14a,
			0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef, 0x1231, 0x0210, 0x3273,
			0x2252, 0x52b5, 0x4294, 0x72f7, 0x62d6, 0x9339, 0x8318, 0xb37b,
			0xa35a, 0xd3bd, 0xc39c, 0xf3ff, 0xe3de, 0x2462, 0x3443, 0x0420,
			0x1401, 0x64e6, 0x74c7, 0x44a4, 0x5485, 0xa56a, 0xb54b, 0x8528,
			0x9509, 0xe5ee, 0xf5cf, 0xc5ac, 0xd58d, 0x3653, 0x2672, 0x1611,
			0x0630, 0x76d7, 0x66f6, 0x5695, 0x46b4, 0xb75b, 0xa77a, 0x9719,
			0x8738, 0xf7df, 0xe7fe, 0xd79d, 0xc7bc, 0x48c4, 0x58e5, 0x6886,
			0x78a7, 0x0840, 0x1861, 0x2802, 0x3823, 0xc9cc, 0xd9ed, 0xe98e,
			0xf9af, 0x8948, 0x9969, 0xa90a, 0xb92b, 0x5af5, 0x4ad4, 0x7ab7,
			0x6a96, 0x1a71, 0x0a50, 0x3a33, 0x2a12, 0xdbfd, 0xcbdc, 0xfbbf,
			0xeb9e, 0x9b79, 0x8b58, 0xbb3b, 0xab1a, 0x6ca6, 0x7c87, 0x4ce4,
			0x5cc5, 0x2c22, 0x3c03, 0x0c60, 0x1c41, 0xedae, 0xfd8f, 0xcdec,
			0xddcd, 0xad2a, 0xbd0b, 0x8d68, 0x9d49, 0x7e97, 0x6eb6, 0x5ed5,
			0x4ef4, 0x3e13, 0x2e32, 0x1e51, 0x0e70, 0xff9f, 0xefbe, 0xdfdd,
			0xcffc, 0xbf1b, 0xaf3a, 0x9f59, 0x8f78, 0x9188, 0x81a9, 0xb1ca,
			0xa1eb, 0xd10c, 0xc12d, 0xf14e, 0xe16f, 0x1080, 0x00a1, 0x30c2,
			0x20e3, 0x5004, 0x4025, 0x7046, 0x6067, 0x83b9, 0x9398, 0xa3fb,
			0xb3da, 0xc33d, 0xd31c, 0xe37f, 0xf35e, 0x02b1, 0x1290, 0x22f3,
			0x32d2, 0x4235, 0x5214, 0x6277, 0x7256, 0xb5ea, 0xa5cb, 0x95a8,
			0x8589, 0xf56e, 0xe54f, 0xd52c, 0xc50d, 0x34e2, 0x24c3, 0x14a0,
			0x0481, 0x7466, 0x6447, 0x5424, 0x4405, 0xa7db, 0xb7fa, 0x8799,
			0x97b8, 0xe75f, 0xf77e, 0xc71d, 0xd73c, 0x26d3, 0x36f2, 0x0691,
			0x16b0, 0x6657, 0x7676, 0x4615, 0x5634, 0xd94c, 0xc96d, 0xf90e,
			0xe92f, 0x99c8, 0x89e9, 0xb98a, 0xa9ab, 0x5844, 0x4865, 0x7806,
			0x6827, 0x18c0, 0x08e1, 0x3882, 0x28a3, 0xcb7d, 0xdb5c, 0xeb3f,
			0xfb1e, 0x8bf9, 0x9bd8, 0xabbb, 0xbb9a, 0x4a75, 0x5a54, 0x6a37,
			0x7a16, 0x0af1, 0x1ad0, 0x2ab3, 0x3a92, 0xfd2e, 0xed0f, 0xdd6c,
			0xcd4d, 0xbdaa, 0xad8b, 0x9de8, 0x8dc9, 0x7c26, 0x6c07, 0x5c64,
			0x4c45, 0x3ca2, 0x2c83, 0x1ce0, 0x0cc1, 0xef1f, 0xff3e, 0xcf5d,
			0xdf7c, 0xaf9b, 0xbfba, 0x8fd9, 0x9ff8, 0x6e17, 0x7e36, 0x4e55,
			0x5e74, 0x2e93, 0x3eb2, 0x0ed1, 0x1ef0 };
	private GetInfoThread getInfoThread;
	private SendMsgThread smt;

	public void initprocess(){
		/*startsent=false;
		startrec=true;
		new Thread(new Udprec()).start();*/
		getInfoThread = new GetInfoThread(mcontext,handler2);
		getInfoThread.start();
	}
	Handler handler2 =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			msg.what=11;
			//解析返回的数据
			getInfoThread.setMsgType(11);
			broadcastUpdate(ACTION_DATA_IN, (byte[])msg.obj);
		}
	};
	public udp_socket(Context context){
		mcontext=context;
		new Thread(networkTask).start();
	}
	public void StopAllThread(){
		startsent=false;
		startrec=false;
		getInfoThread.close();
	}
	public boolean SendUDPBuffer(byte[] arrayAddtional,
								 short shortLenOfAddtionalBuf, int intOP, byte byteObjSubnetID,
								 byte byteObjDeviceID, boolean blnBigPack) {
		boolean blnSuccess = false;
		String strLocalIP;
		short shortLenOfBaseData, shortI, shortLenOfPackCRCBufWithAA, shortLenOfPackCRCBufWithoutAA, shortLenOfSend;
		byte[] bytebufSend;//发送的数据缓冲
		byte[] arraybyteLocalIP = new byte[4];//本地ip
		byte[] arraybyteTargetIP = new byte[4];//目标ip

		try {
			// bytebufRec=new byte[300];
			// 前提，保证有已经获得本地的 IP
			arraybyteLocalIP = GetLocalIP();
			Log.v("breakPoint", "SendUDPBuffer 正在获得目标IP地址");


			arraybyteTargetIP = GetTargetIP(arraybyteLocalIP);
			//Log.v("breakPoint", "SendUDPBuffer 正在获得TargetIP地址");
			//Log.i("breakPoint", "TaggetID==" + arraybyteTargetIP.toString());
			System.out.println("arraybytetargetIP="
					+ (arraybyteTargetIP[0]&0xff) + "." + (arraybyteTargetIP[1]&0xff)+"."+
					(arraybyteTargetIP[2]&0xff)+"."+ (arraybyteTargetIP[3]&0xff)+".");
			// short
			// shortLenOfBaseData,shortI,shortLenOfPackCRCBufWithAA,shortLenOfPackCRCBufWithoutAA,shortLenOfSend;
			shortLenOfBaseData = 11;// 数据包长度设为11.其基础udp包长度为11个字节，这里先声明UDP包的基本长度大小
			shortLenOfPackCRCBufWithoutAA = (short) (shortLenOfBaseData + shortLenOfAddtionalBuf);
			shortLenOfPackCRCBufWithAA = (short) (shortLenOfPackCRCBufWithoutAA + 2);

			shortLenOfSend = (short) (shortLenOfPackCRCBufWithAA + 14);//14个字节表示的是包头的总大小字节数，4：ip地址+10：包头+数据长度
			bytebufSend = new byte[shortLenOfSend];

			byte[] arrayPackCRC = new byte[shortLenOfPackCRCBufWithoutAA];//用于打包CRC值
			bytebufSend[0] = arraybyteLocalIP[0];
			bytebufSend[1] = arraybyteLocalIP[1];
			bytebufSend[2] = arraybyteLocalIP[2];
			bytebufSend[3] = arraybyteLocalIP[3];

			bytebufSend[4] = 0x53; // S
			bytebufSend[5] = 0x4D; // M
			bytebufSend[6] = 0x41; // A
			bytebufSend[7] = 0x52; // R
			bytebufSend[8] = 0x54; // T
			bytebufSend[9] = 0x43; // C
			bytebufSend[10] = 0x4C; // L
			bytebufSend[11] = 0x4F; // O
			bytebufSend[12] = 0x55; // U
			bytebufSend[13] = 0x44; // D

			bytebufSend[14] = (byte) 0xAA; //
			bytebufSend[15] = (byte) 0xAA; //

			// data size 判断大包是否有起始码
			if ((blnBigPack == true)) {
				// 如果是大包，必须为0xFF
				arrayPackCRC[0] = (byte) 0xFF;
			} else {
				arrayPackCRC[0] = (byte) shortLenOfPackCRCBufWithoutAA;//没有起始码默认是withoutAA的长度
			}
			;

			// 基本数据结构
			arrayPackCRC[0] =  arrayPackCRC[0]; // LEN of
			// Data
			// Package,包长大小
			arrayPackCRC[1] = (byte) CONST_SELF_SUBNET_ID; // 源subID
			arrayPackCRC[2] = (byte) CONST_SELF_DEVICE_ID; // 源设备ID
			arrayPackCRC[3] = (byte) CONST_SELF_DEVICE_TYPE_H; // 设备类型，高8位
			arrayPackCRC[4] = (byte) CONST_SELF_DEVICE_TYPE_L; // 设备类型，低8位
			arrayPackCRC[5] = (byte) (intOP / 256); // H bit of operation code
			// 高位操作码，
			arrayPackCRC[6] = (byte) (intOP % 256); // L bit of operation
			// code低位操作码
			arrayPackCRC[7] = byteObjSubnetID; // 目标子网ID
			arrayPackCRC[8] = byteObjDeviceID; // 目标设备ID

			// 到了Addtional Content 附加数据
			if (shortLenOfAddtionalBuf > 0)// 如果有附加数据
			{
				for (shortI = 0; shortI <= shortLenOfAddtionalBuf - 1; shortI++) {
					arrayPackCRC[9 + shortI] = arrayAddtional[shortI];
				}
			}

			if (blnBigPack == false) // 如果是小包
			{
				PackCRC(arrayPackCRC, (short) (arrayPackCRC.length - 2));//减去原本CRC的2个字节长度
			}else{
				PackCRC(arrayPackCRC, (short) (arrayPackCRC.length - 2));//减去原本CRC的2个字节长度
			}

			// 不管是大包还是小包.都把数据打包
			for (shortI = 0; shortI <= arrayPackCRC.length - 1; shortI++) {
				bytebufSend[shortI + 16] = arrayPackCRC[shortI];// 为什么加16,基础数据+2AA+4ip+10包头
			}

			if(rsipip){

				WifiManager wifiManager = (WifiManager) mcontext.getSystemService(mcontext.WIFI_SERVICE);
				final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				SharedPreferences sharedPre4 = mcontext.getSharedPreferences("wifiinfo", mcontext.MODE_PRIVATE);
				if((wifiInfo.getSSID()).equals(sharedPre4.getString("wifiname", "not set wifiname"))){//是否是可用的局域网
					if(netchange!=1){
						//StopAllThread();
						initprocess();
					}
					oDataPacket = new DatagramPacket(bytebufSend, shortLenOfSend,
							InetAddress.getByAddress(arraybyteTargetIP),
							CONST_UDP_UDP_PORT);
					netchange=1;
					System.out.println("------------------1-------------------");
				}else{
					if(netchange!=2){
						//StopAllThread();
						initprocess();
					}
					byte[] wei={0x02};//设备标识符 0x02 ios,
					byte[] wei2={0x00,0x00};
					byte[] newdata=new byte[bytebufSend.length+9];
					newdata[0]=remoteByte[0];
					newdata[1]=remoteByte[1];
					newdata[2]=remoteByte[2];
					newdata[3]=remoteByte[3];
					System.arraycopy(bytebufSend,4,newdata,4,bytebufSend.length-6);
					newdata[16]=(byte)((newdata[16]%0xff)+9);
					System.arraycopy(wei,0,newdata,bytebufSend.length-2,1);
					System.arraycopy(MacAddress,0,newdata,bytebufSend.length-1,8);
					System.arraycopy(wei2, 0, newdata, newdata.length - 2, 2);
					oDataPacket = new DatagramPacket(newdata, newdata.length,
							InetAddress.getByAddress(arraybyteTargetIP),
							SMARTSERVICE_PORT);
					netchange=2;
					System.out.println("------------------2-------------------");
				}

			}else{
				if(netchange!=4){
					//StopAllThread();
					initprocess();
				}
				// 实例化一个数据报包，并说明所发包，其包大小，目标ip，目标端口，指定端口号和目标IP。
				// 再通过udpsocket发送出去，就可以达到了指定的端口
				oDataPacket = new DatagramPacket(bytebufSend, shortLenOfSend,
						InetAddress.getByAddress(arraybyteTargetIP),
						CONST_UDP_UDP_PORT);
				netchange=4;
				System.out.println("------------------4-------------------");
			}

			/*if(a==1){

			}else{
				smt = new SendMsgThread(getInfoThread);
				smt.start();
				smt.putMsg(oDataPacket);
				a=1;
				senthandler.postDelayed(sentrun,15);
				blnSuccess = true;
			}*/
			smt = new SendMsgThread(getInfoThread);
			smt.start();
			smt.putMsg(oDataPacket);
			senthandler.postDelayed(sentrun,15);
			blnSuccess = true;


		} catch (Exception e) {
			// Test
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();

		}
		return blnSuccess;
	}
	int a=0;//防止发送指令太快
	int netchange=0;//判断网络变化
	Runnable sentrun=new Runnable() {
		@Override
		public void run() {
			a=0;
			senthandler.removeCallbacks(sentrun);
		}
	};


	private void broadcastUpdate(final String action,byte[] data_in) {
		final Intent intent = new Intent(action);
		intent.putExtra(ACTION_DATA_IN, data_in);
		//FounctionActivity.fcontext.sendBroadcast(intent);
		mcontext.sendBroadcast(intent);
	}

	public static boolean mansetip=false,ddnssetip=false,rsipip=false;
	public static byte[] MacAddress=new byte[8];
	public static byte[] manlocalip=new byte[4],mantargetip=new byte[4],ddnstargetip=new byte[4];
	// 获得本地IP
	public byte[] GetLocalIP() {
		// IP为4个字节
		byte[] ipAddr = new byte[4];
		String strIP;

		try {

			// 通用获得本地IP方法,
			ipAddr = null;
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()) {
						ipAddr = inetAddress.getAddress();

						strIP = inetAddress.getHostAddress().toString();
						//Log.v("breakPoint", "正在打印获得的本地IP地址");
						//	System.out.println("正在打印获得的本地IP地址==" + strIP);
						/*System.out.println("正在打印获得的本地IP地址inetAddress.getHostAddress()=="
										+ inetAddress.getHostAddress());
						System.out.println("inetAddress.getAddress()="
								+ inetAddress.getAddress());*/
						return ipAddr;
					}
				}
			}
			return ipAddr;

		} catch (Exception e) {
			// Test
			/*Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();*/
		}
		if(mansetip) {
			ipAddr[0] = manlocalip[0];
			ipAddr[1] = manlocalip[1];
			ipAddr[2] = manlocalip[2];
			ipAddr[3] = manlocalip[3];
		}

		return ipAddr;

	}

	/*
	 * 获得目标IP
	 */
	public byte[] GetTargetIP(byte[] arraybyteLocalIP) {
		byte[] arraybyteTargetIP = new byte[4];
		byte byteBit;

		try {
			/*byteBit = (byte) ((arraybyteLocalIP[0] & 0xFF) >> 5);
			if (((byteBit & 0xFF) >= 0) && ((byteBit & 0xFF) <= 3)) // IP type:A
			{
				arraybyteTargetIP[0] = arraybyteLocalIP[0];
				arraybyteTargetIP[1] = (byte) 255;
				arraybyteTargetIP[2] = (byte) 255;
				arraybyteTargetIP[3] = (byte) 255;
			} else if (((byteBit & 0xFF) >= 4) && ((byteBit & 0xFF) <= 5)) // IP
			// Type:B
			{
				arraybyteTargetIP[0] = arraybyteLocalIP[0];
				arraybyteTargetIP[1] = arraybyteLocalIP[1];
				arraybyteTargetIP[2] = (byte) 255;
				arraybyteTargetIP[3] = (byte) 255;
			} else if (((byteBit & 0xFF) >= 6) && ((byteBit & 0xFF) <= 7)) // IP
			// Type:C
			{
				arraybyteTargetIP[0] = arraybyteLocalIP[0];
				arraybyteTargetIP[1] = arraybyteLocalIP[1];
				arraybyteTargetIP[2] = arraybyteLocalIP[2];
				arraybyteTargetIP[3] = (byte) 255;
			} else {
				arraybyteTargetIP[0] = (byte) 255;
				arraybyteTargetIP[1] = (byte) 255;
				arraybyteTargetIP[2] = (byte) 255;
				arraybyteTargetIP[3] = (byte) 255;
			}*/
			arraybyteTargetIP[0] = (byte) 255;
			arraybyteTargetIP[1] = (byte) 255;
			arraybyteTargetIP[2] = (byte) 255;
			arraybyteTargetIP[3] = (byte) 255;
		} catch (Exception e) {
			// Test
			/*Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();*/
		}

		//Log.v("breakPoint", "正在打印目标IP地址");
		//System.out.println("获得的目标IP byte[] = " + arraybyteTargetIP);
		/****test****测试完后把下面这段删除掉*/

		if(mansetip) {
			WifiManager wifiManager = (WifiManager) mcontext.getSystemService(mcontext.WIFI_SERVICE);
			final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			SharedPreferences sharedPre4 = mcontext.getSharedPreferences("wifiinfo", mcontext.MODE_PRIVATE);
			if((wifiInfo.getSSID()).equals(sharedPre4.getString("wifiname", "not set wifiname"))) {//是否是可用的局域网

			}else{
				arraybyteTargetIP[0] = mantargetip[0];
				arraybyteTargetIP[1] = mantargetip[1];
				arraybyteTargetIP[2] = mantargetip[2];
				arraybyteTargetIP[3] = mantargetip[3];
			}
		}
		if(ddnssetip) {
			arraybyteTargetIP[0] = ddnstargetip[0];
			arraybyteTargetIP[1] = ddnstargetip[1];
			arraybyteTargetIP[2] = ddnstargetip[2];
			arraybyteTargetIP[3] = ddnstargetip[3];
		}
		if(rsipip){
			WifiManager wifiManager = (WifiManager) mcontext.getSystemService(mcontext.WIFI_SERVICE);
			final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			SharedPreferences sharedPre4 = mcontext.getSharedPreferences("wifiinfo", mcontext.MODE_PRIVATE);
			if(wifiInfo.getBSSID()!=null){
				if((wifiInfo.getSSID()).equals(sharedPre4.getString("wifiname", "not set wifiname"))){

				}else{
					/*arraybyteTargetIP[0] = (byte)0xa2;
					arraybyteTargetIP[1] = (byte)0x90;
					arraybyteTargetIP[2] = (byte)0x42;
					arraybyteTargetIP[3] = (byte)0x83;//162.144.66.131*/
					arraybyteTargetIP[0] = remoteByte[0];
					arraybyteTargetIP[1] = remoteByte[1];
					arraybyteTargetIP[2] = remoteByte[2];
					arraybyteTargetIP[3] = remoteByte[3];
				}
			}else{
				/*arraybyteTargetIP[0] = (byte)0xa2;
				arraybyteTargetIP[1] = (byte)0x90;
				arraybyteTargetIP[2] = (byte)0x42;
				arraybyteTargetIP[3] = (byte)0x83;*/
				arraybyteTargetIP[0] = remoteByte[0];
				arraybyteTargetIP[1] = remoteByte[1];
				arraybyteTargetIP[2] = remoteByte[2];
				arraybyteTargetIP[3] = remoteByte[3];
			}
		}
		return arraybyteTargetIP;

	}

	/*
	 * Get 2 CRC bytes 获得2个字节的CRC校验码
	 */
	protected void PackCRC(byte[] arrayBuf, short shortLenOfBuf) {
		try {
			short shortCRC = 0;
			byte bytTMP = 0;
			short shortIndexOfBuf = 0;
			byte byteIndex_Of_CRCTable = 0;
			while (shortLenOfBuf != 0) {
				bytTMP = (byte) (shortCRC >> 8); // >>: right move bit
				shortCRC = (short) (shortCRC << 8); // <<: left move bit
				byteIndex_Of_CRCTable = (byte) (bytTMP ^ arrayBuf[shortIndexOfBuf]);
				shortCRC = (short) (shortCRC ^ mbufintCRCTable[(byteIndex_Of_CRCTable & 0xFF)]); // ^:
				// xor
				shortIndexOfBuf = (short) (shortIndexOfBuf + 1);
				shortLenOfBuf = (short) (shortLenOfBuf - 1);
			}
			;

			arrayBuf[shortIndexOfBuf] = (byte) (shortCRC >> 8);
			shortIndexOfBuf = (short) (shortIndexOfBuf + 1);
			arrayBuf[shortIndexOfBuf] = (byte) (shortCRC & 0x00FF);

		} catch (Exception e) {
			// Test
			/*Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();*/
		}

	}

	public static String GetCRC(byte[] arrayBuf) {
		String strCRC2byte = "";
		try {
			byte byteH, byteL;
			short shortCRC = 0;
			byte bytTMP = 0;
			short shortIndexOfBuf = 0;
			byte byteIndex_Of_CRCTable = 0;
			int intLenOfBuf = arrayBuf.length;
			while (intLenOfBuf != 0) {
				bytTMP = (byte) (shortCRC >> 8); // >>: right move bit
				shortCRC = (short) (shortCRC << 8); // <<: left move bit
				byteIndex_Of_CRCTable = (byte) (bytTMP ^ arrayBuf[shortIndexOfBuf]);
				shortCRC = (short) (shortCRC ^ mbufintCRCTable[(byteIndex_Of_CRCTable & 0xFF)]); // ^:
				// xor
				shortIndexOfBuf = (short) (shortIndexOfBuf + 1);
				intLenOfBuf = intLenOfBuf - 1;
			}
			;

			byteH = (byte) (shortCRC >> 8);
			byteL = (byte) (shortCRC & 0x00FF);

			strCRC2byte = Integer.toHexString(shortCRC & 0xFFFF).toUpperCase();
			switch (strCRC2byte.length()) {
				case 1: {
					strCRC2byte = "000" + strCRC2byte;
					break;
				}

				case 2: {
					strCRC2byte = "00" + strCRC2byte;
					break;
				}

				case 3: {
					strCRC2byte = "0" + strCRC2byte;
					break;
				}

				default: {
					break;
				}
			}

		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}
		return strCRC2byte;
	}

	/*
	 * 利用CRC校验码判断UDP包是否正确 Check the UDP packets is correct or not by checking
	 * CRC 检查的UDP数据包是通过检查CRC正确与否
	 */
	public boolean CheckCRC(byte[] arrayBuf, int intlength) {
		boolean blnIsCorrenct = false;

		try {
			short shortCRC = 0;
			byte bytTMP = 0;
			short shortIndexOfBuf = 0;
			byte byteIndex_Of_CRCTable = 0;

			if (IsSocketClose() == true) {
				return false;
			}

			while (intlength != 0) {
				bytTMP = (byte) (shortCRC >> 8); // >>: right move bit
				shortCRC = (short) (shortCRC << 8); // <<: left move bit
				byteIndex_Of_CRCTable = (byte) (bytTMP ^ arrayBuf[shortIndexOfBuf]);
				shortCRC = (short) (shortCRC ^ mbufintCRCTable[(byteIndex_Of_CRCTable & 0xFF)]); // ^:
				// xor
				shortIndexOfBuf = (short) (shortIndexOfBuf + 1);
				intlength = (short) (intlength - 1);
			}
			;

			if (arrayBuf[shortIndexOfBuf] == (shortCRC >> 8)
					&& arrayBuf[shortIndexOfBuf + 1] == (short) (shortCRC & 0xFF)) {
				blnIsCorrenct = true;
			} else {
				blnIsCorrenct = false;
			}
			;

		} catch (Exception e) {
			// Test
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}

		return blnIsCorrenct;

	}

	public boolean IsSocketClose() {
		boolean blnIsClose = false;
		try {
			if (moUDPSocket == null) {
				blnIsClose = true;
			} else {
				if (moUDPSocket.isClosed() == true) {
					blnIsClose = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return blnIsClose;
	}

	//远程服务器地址
	public byte[] remoteByte=new byte[4];
	// 网络操作相关的子线程
	Runnable networkTask = new Runnable() {
		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			remoteByte=GetInetAddress("www.smartbuscloud.com");
			//www.smartbuscloud.com
			//74.208.144.185

			//www.g4cloud.ir
			//89.42.208.100
		}
	};


	//域名转Ip byte数组
	public  byte[] GetInetAddress(String host) {
		String IPAddress = "74.208.144.185";
		byte[] ipByte=new byte[4];
		ipByte[0]=(byte)74;
		ipByte[1]=(byte)208;
		ipByte[2]=(byte)144;
		ipByte[3]=(byte)185;
		try {
			InetAddress ReturnStr1 = java.net.InetAddress.getByName(host);
			IPAddress = ReturnStr1.getHostAddress();
			String[] ip_split=IPAddress.split("\\.");
			if (ip_split.length==4){
				ipByte[0]=(byte) Integer.parseInt(ip_split[0]);
				ipByte[1]=(byte) Integer.parseInt(ip_split[1]);
				ipByte[2]=(byte) Integer.parseInt(ip_split[2]);
				ipByte[3]=(byte) Integer.parseInt(ip_split[3]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ipByte;
	}
}
