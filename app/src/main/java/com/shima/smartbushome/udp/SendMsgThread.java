package com.shima.smartbushome.udp;

import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Administrator on 2016/11/22./**
 * 发送消息的队列，每次发送数据时，只需要调用putMsg(byte[] data)方法
 *
 * @author usr_liujinqi
 *
 */

public class SendMsgThread extends Thread {
    // 发送消息的队列
    private Queue<DatagramPacket> sendMsgQuene = new LinkedList<DatagramPacket>();
    // 是否发送消息
    private boolean send = true;

    private GetInfoThread ss;

    public SendMsgThread(GetInfoThread ss) {
        this.ss = ss;
    }

    public synchronized void putMsg(DatagramPacket msg) {
        // 唤醒线程
        if (sendMsgQuene.size() == 0)
            notify();
        sendMsgQuene.offer(msg);
    }

    public void run() {
        synchronized (this) {
            while (send) {
                // 当队列里的消息发送完毕后，线程等待
                while (sendMsgQuene.size() > 0) {
                    //取出指令并在队列中删除
                    DatagramPacket msg = sendMsgQuene.poll();
                    if (ss != null) {
                        ss.sendMsg(msg);
                    }

                    //每条指令间隔200毫秒
                    try {
                        Thread.sleep(150);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setSend(boolean send) {
        this.send = send;
    }
}
