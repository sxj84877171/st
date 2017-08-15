package com.sunvote.udptransfer.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description: UDP发送工具类
 */

public class UDPUtils {

    private final static String TAG = UDPUtils.class.getSimpleName();

    public static boolean send(DatagramSocket skt, byte[] d, int dataLen) {
        if (skt != null && d != null) {
            try {
                return send(skt, new DatagramPacket(d, dataLen));
            } catch (Exception e) {
                LogUtil.e(TAG, "【IMCORE】send方法中》》发送UDP数据报文时出错了：remoteIp=" + skt.getInetAddress()
                        + ", remotePort=" + skt.getPort() + ".原因是：" + e.getMessage(), e);
                return false;
            }
        } else {
            LogUtil.e(TAG, "【IMCORE】send方法中》》无效的参数：skt=" + skt);//
            return false;
        }
    }

    public synchronized static boolean send(DatagramSocket skt, DatagramPacket p) {
        boolean sendSucess = true;
        if (skt != null && p != null) {
            if (skt.isConnected()) {
                try {
                    skt.send(p);
                } catch (Exception e) {
                    sendSucess = false;
                    LogUtil.e(TAG, "【IMCORE】send方法中》》发送UDP数据报文时出错了，原因是：", e);
                }
            }
        } else {
            LogUtil.w(TAG, "【IMCORE】在send()UDP数据报时没有成功执行，原因是：skt==null || p == null!");
        }

        return sendSucess;
    }
}
