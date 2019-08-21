package com.shima.smartbushome.founction_command;

import com.shima.smartbushome.udp.udp_socket;

import java.util.Random;

/**
 * Created by Administrator on 16-6-16.
 */
public class musiccontrol {

	public void deletesong(int byteSubnetID, int byteDeviceID,String albumname,String songname,udp_socket mydupsocket){

	}
	public void sentsongcontent(int byteSubnetID, int byteDeviceID,byte[] random,byte[] content,byte[] revlength,udp_socket mydupsocket){
		byte[] sent=new byte[11+content.length];
		sent[0]=0x04;
		sent[1]=0x0b;
		sent[2]=0x01;
		sent[3]=random[0];
		sent[4]=random[1];
		for(int i=0;i<4;i++){
			sent[5+i]=revlength[i];
		}
		sent[9]=(byte) (((content.length-2) &0xff00)>>8);
		sent[10]=(byte) (((content.length-2) ) - ((content.length-2)&0xff00));
		for(int i2=0;i2<content.length;i2++){
			sent[11+i2]=content[i2];
		}
		mydupsocket.SendUDPBuffer(sent,(short)sent.length,0x0200,(byte)byteSubnetID,(byte)byteDeviceID,true);
	}

	public byte[] UploadSongStart(int byteSubnetID, int byteDeviceID,String foldername,String name,int filesize,udp_socket mydupsocket){
		byte[] add_random=new byte[2];
		int filenamelength=(name.substring(0,name.indexOf("."))).length();
		try{
			int ram=new Random().nextInt(65535);
			byte[] folder=foldername.getBytes("ascii");
			byte[] song=name.getBytes("UTF-16BE");
			byte[] songascii=name.getBytes("ascii");
			byte[] songascii_withoutmp3=name.substring(0,name.indexOf(".")).getBytes("ascii");
			if(filenamelength>8){
				byte[] sent=new byte[29+folder.length+song.length];
				sent[0]=(byte) (((sent.length-2) &0xff00)>>8);
				sent[1]=(byte) (((sent.length-2) ) - ((sent.length-2)&0xff00));
				sent[2]=0;
				sent[3]=(byte)(ram/256);
				sent[4]=(byte)(ram%256);
				add_random[0]=sent[3];
				add_random[1]=sent[4];
				sent[6]=0x5c;
				sent[7]=0;
				for(int i=0;i<(folder.length);i++){
					sent[8+i]=folder[i];
				}
				sent[8+folder.length]=0;
				sent[9+folder.length]=0x5c;
				for(int i2=0;i2<song.length;i2++){
					sent[10+folder.length+i2]=song[i2];
				}
				sent[5]=(byte)((folder.length+song.length)+18);
				sent[10+folder.length+song.length]=0;
				sent[11+folder.length+song.length]=0;
				sent[12+folder.length+song.length]=0;
				for(int i3=0;i3<6;i3++){
					sent[13+folder.length+song.length+i3]=songascii[i3];
				}
				sent[19+folder.length+song.length]=0x7e;
				sent[20+folder.length+song.length]=0x31;
				sent[21+folder.length+song.length]=0x4d;
				sent[22+folder.length+song.length]=0x50;
				sent[23+folder.length+song.length]=0x33;
				sent[24+folder.length+song.length]=(byte)(filesize/256/256/256);
				sent[25+folder.length+song.length]=(byte)(filesize/256/256%256);
				sent[26+folder.length+song.length]=(byte)(filesize/256);
				sent[27+folder.length+song.length]=(byte)(filesize%256);
				sent[28+folder.length+song.length]=0x20;
				mydupsocket.SendUDPBuffer(sent,(short)sent.length,0x0200,(byte)byteSubnetID,(byte)byteDeviceID,true);
			}else{
				byte[] sentshort=new byte[29+folder.length];
				sentshort[0]=(byte) (((sentshort.length-2) &0xff00)>>8);
				sentshort[1]=(byte) (((sentshort.length-2) ) - ((sentshort.length-2)&0xff00));
				sentshort[2]=0;
				sentshort[3]=(byte)(ram/256);
				sentshort[4]=(byte)(ram%256);
				add_random[0]=sentshort[3];
				add_random[1]=sentshort[4];
				sentshort[6]=0x5c;
				sentshort[7]=0;
				for(int i=0;i<(folder.length);i++){
					sentshort[8+i]=folder[i];
				}
				sentshort[8+folder.length]=0;
				sentshort[9+folder.length]=0x5c;
				sentshort[10+folder.length]=0;
				sentshort[11+folder.length]=0;
				sentshort[12+folder.length]=0;
				for(int i2=0;i2<8;i2++){
					if(i2<songascii_withoutmp3.length){
						sentshort[13+folder.length+i2]=songascii_withoutmp3[i2];
					}else{
						sentshort[13+folder.length+i2]=0x20;
					}
				}
				sentshort[21+folder.length]=0x4d;
				sentshort[22+folder.length]=0x50;
				sentshort[23+folder.length]=0x33;
				sentshort[24+folder.length]=(byte)(filesize/256/256/256);
				sentshort[25+folder.length]=(byte)(filesize/256/256%256);
				sentshort[26+folder.length]=(byte)(filesize/256);
				sentshort[27+folder.length]=(byte)(filesize%256);
				sentshort[28+folder.length]=0x20;
				sentshort[5]=(byte)((folder.length+18));
				mydupsocket.SendUDPBuffer(sentshort,(short)sentshort.length,0x0200,(byte)byteSubnetID,(byte)byteDeviceID,true);
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return add_random;
	}
	public void GetMusicState(byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket) {

		try {
			int intOP = 0x192e;
			short shortLenOfAddtionalBuf;
			// 命令
			byte[] arrayAddtional = new byte[11];
			arrayAddtional[0] = 0x2a;//
			arrayAddtional[1] = 0x5a;//
			arrayAddtional[2] = 0x31;//
			arrayAddtional[3] = 0x53;//
			arrayAddtional[4] = 0x54;//
			arrayAddtional[5] = 0x41;//
			arrayAddtional[6] = 0x54;//
			arrayAddtional[7] = 0x55;//
			arrayAddtional[8] = 0x53;//
			arrayAddtional[9] = 0x3f;//
			arrayAddtional[10] = 0x0d;//

			shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
			mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
					byteSubnetID, byteDeviceID, false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateZaudiolist(byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket){
		try {
			int intOP = 0x192e;
			short shortLenOfAddtionalBuf;
			// 命令
			byte[] arrayAddtional = new byte[14];
			arrayAddtional[0] = 0x2a;//
			arrayAddtional[1] = 0x53;//
			arrayAddtional[2] = 0x31;//
			arrayAddtional[3] = 0x55;//U
			arrayAddtional[4] = 0x50;//P
			arrayAddtional[5] = 0x44;//D
			arrayAddtional[6] = 0x41;//A
			arrayAddtional[7] = 0x54;//T
			arrayAddtional[8] = 0x45;//E
			arrayAddtional[9] = 0x4c;//L
			arrayAddtional[10] = 0x49;//I
			arrayAddtional[11] = 0x53;//S
			arrayAddtional[12] = 0x54;//T
			arrayAddtional[13] = 0x0d;//T
			shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
			mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
					byteSubnetID, byteDeviceID, false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void SwitchtoMusicSD(byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket){
		try {
			int intOP = 0x0218;
			short shortLenOfAddtionalBuf;
			// 命令
			byte[] arrayAddtional = new byte[4];
			arrayAddtional[0] = 1;// 固定值
			arrayAddtional[1] = 1;// 当前列表号
			arrayAddtional[2] = 0;// 固定值
			arrayAddtional[3] = 0;// 当前歌曲号ID

			shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
			mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
					byteSubnetID, byteDeviceID, false);

		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}
	}
   // 播放当前处于暂停的歌曲
	public void MusicControl2(byte data1,byte data2, byte data3,byte data4,byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket) {

		try {
			int intOP = 0x0218;
			short shortLenOfAddtionalBuf;
			// 命令
			byte[] arrayAddtional = new byte[4];
			arrayAddtional[0] = data1;// 固定值
			arrayAddtional[1] = data2;// 当前列表号
			arrayAddtional[2] = data3;// 固定值
			arrayAddtional[3] = data4;// 当前歌曲号ID

			shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
			mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
                    byteSubnetID, byteDeviceID, false);

		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}

	}// end

	public void MusicControl(byte data1,byte data2, byte data3,byte data4,byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket) {

		try {


			switch ((data1&0xff)){
				case 1:
				case 2:
					int intOP12 = 0x0218;
					short shortLenOfAddtionalBuf12;
					// 命令
					byte[] arrayAddtional = new byte[4];
					arrayAddtional[0] = data1;// 固定值
					arrayAddtional[1] = data2;// 当前列表号
					arrayAddtional[2] = data3;// 固定值
					arrayAddtional[3] = data4;// 当前歌曲号ID
					shortLenOfAddtionalBuf12 = (short) (arrayAddtional.length);
					mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf12, intOP12,
							byteSubnetID, byteDeviceID, false);
					break;
				case 3:
					break;
				case 4:
					int intOP4 = 0x192e;
					short shortLenOfAddtionalBuf4;
					byte[] arrayAddtional4 = {(byte)0x2a,(byte)0x53,(byte)0x31,0,0,0,0,(byte)0x0d,(byte)0x55,
							(byte)0xaa,(byte)0x01,(byte)0x02,(byte)0x53,(byte)0x04,0,0,0,0,(byte)0x0e,(byte)0x35};
					switch ((data2&0xff)){
						case 1://back
							arrayAddtional4[3]=(byte)0x50;
							arrayAddtional4[4]=(byte)0x52;
							arrayAddtional4[5]=(byte)0x45;
							arrayAddtional4[6]=(byte)0x56;
							break;
						case 2://next
							arrayAddtional4[3]=(byte)0x4e;
							arrayAddtional4[4]=(byte)0x45;
							arrayAddtional4[5]=(byte)0x58;
							arrayAddtional4[6]=(byte)0x54;
							break;
						case 3://play
							arrayAddtional4[3]=(byte)0x50;
							arrayAddtional4[4]=(byte)0x4c;
							arrayAddtional4[5]=(byte)0x41;
							arrayAddtional4[6]=(byte)0x59;
							break;
						case 4://stop
							arrayAddtional4[3]=(byte)0x53;
							arrayAddtional4[4]=(byte)0x54;
							arrayAddtional4[5]=(byte)0x4f;
							arrayAddtional4[6]=(byte)0x50;
							break;
					}
					shortLenOfAddtionalBuf4 = (short) (arrayAddtional4.length);
					mydupsocket.SendUDPBuffer(arrayAddtional4, shortLenOfAddtionalBuf4, intOP4,
							byteSubnetID, byteDeviceID, false);
					break;
				case 5://音量控制
					int intOP5 = 0x192e;
					short shortLenOfAddtionalBuf5;
					/*byte[] arrayAddtional5 = {(byte)0x2a,(byte)0x53,(byte)0x31,(byte)0x56,(byte)0x4f,(byte)0x4c,
							0,0,(byte)0x0d,(byte)0x55, (byte)0xaa,(byte)0x01,(byte)0x02,(byte)0x53,(byte)0x04,0,0,0,0,
							(byte)0x0e,(byte)0x35};*/
					byte[] arrayAddtional5 = {(byte)0x2a,(byte)0x5A,(byte)0x31,(byte)0x56,(byte)0x4f,(byte)0x4c,
							0,0,(byte)0x0d,(byte)0x55, (byte)0xaa,(byte)0x01};
					int data4int=data4&0xff;

					arrayAddtional5[6]=String.valueOf((data4int/10)%10).getBytes("ascii")[0];
					arrayAddtional5[7]=String.valueOf(data4int%10).getBytes("ascii")[0];
					shortLenOfAddtionalBuf5= (short) (arrayAddtional5.length);
					mydupsocket.SendUDPBuffer(arrayAddtional5, shortLenOfAddtionalBuf5, intOP5,
							byteSubnetID, byteDeviceID, false);
					break;
				case 6:
					int intOP6 = 0x192e;
					short shortLenOfAddtionalBuf6;
					byte[] arrayAddtional6 = {(byte)0x2a,(byte)0x53,(byte)0x31,(byte)0x4c,(byte)0x49,(byte)0x53,
							(byte)0x54,0,0,0,(byte)0x2c,(byte)0x53,(byte)0x4f,(byte)0x4e,(byte)0x47,0,0,0, (byte)0x0d,
							(byte)0x55, (byte)0xaa,(byte)0x01,(byte)0x02,(byte)0x53,(byte)0x04,0,0,0,0,
							(byte)0x0e,(byte)0x35};

					arrayAddtional6[7]=(byte)String.valueOf(((data2&0xff)/100)%10).getBytes("ascii")[0];
					arrayAddtional6[8]=(byte)String.valueOf(((data2&0xff)/10)%10).getBytes("ascii")[0];
					arrayAddtional6[9]=(byte)String.valueOf((data2&0xff)%10).getBytes("ascii")[0];
					arrayAddtional6[15]=(byte)String.valueOf((((data3<<8)+(data4&0xff))/100)%10).getBytes("ascii")[0];
					arrayAddtional6[16]=(byte)String.valueOf((((data3<<8)+(data4&0xff))/10)%10).getBytes("ascii")[0];
					arrayAddtional6[17]=(byte)String.valueOf((((data3<<8)+(data4&0xff)))%10).getBytes("ascii")[0];
					shortLenOfAddtionalBuf6= (short) (arrayAddtional6.length);
					mydupsocket.SendUDPBuffer(arrayAddtional6, shortLenOfAddtionalBuf6, intOP6,
							byteSubnetID, byteDeviceID, false);
					break;
			}


		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}

	}// end

	public void MusicReadAlbumQTY(byte source,byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket){
		try {
			int intOP = 0x02E0;
			short shortLenOfAddtionalBuf;
			// 命令
			byte[] arrayAddtional = new byte[1];
			arrayAddtional[0] = source;// sd card 1;ftp 2

			for(int i=1;i<2;i++) {
				shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
				mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
						byteSubnetID, byteDeviceID, false);
			}
		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}
	}

	public void MusicReadAlbum(byte source,byte albumNO,byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket){
		try {
			int intOP = 0x02E2;
			short shortLenOfAddtionalBuf;
			// 命令
			byte[] arrayAddtional = new byte[2];
			arrayAddtional[0] = source;// sd card 1;ftp 2
			arrayAddtional[1] = albumNO;
			shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
			for(int i=1;i<2;i++) {
				mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
						byteSubnetID, byteDeviceID, false);
			}
		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}
	}

	public void MusicReadSongQTY(byte source,byte albumNO,byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket){
		try {
			int intOP = 0x02E4;
			short shortLenOfAddtionalBuf;
			// 命令
			byte[] arrayAddtional = new byte[2];
			arrayAddtional[0] = source;// sd card 1;ftp 2
			arrayAddtional[1] = albumNO;
			shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
			for(int i=1;i<2;i++) {
				mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
						byteSubnetID, byteDeviceID, false);
			}
		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}
	}

	public void MusicReadSong(byte source,byte albumNO,byte songNO,byte byteSubnetID, byte byteDeviceID,udp_socket mydupsocket){
		try {
			int intOP = 0x02E6;
			short shortLenOfAddtionalBuf;
			// 命令
			byte[] arrayAddtional = new byte[3];
			arrayAddtional[0] = source;// sd card 1;ftp 2
			arrayAddtional[1] = albumNO;
			arrayAddtional[2] = songNO;
			shortLenOfAddtionalBuf = (short) (arrayAddtional.length);
			for(int i=1;i<2;i++) {
				mydupsocket.SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP,
						byteSubnetID, byteDeviceID, false);
			}
		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}
	}
}
