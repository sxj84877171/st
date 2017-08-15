package com.sunvote.udptransfer.core;

import android.util.Log;

import com.sunvote.udptransfer.Config;
import com.sunvote.udptransfer.UDPModule;
import com.sunvote.udptransfer.utils.UDPUtils;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description:
 */

public class LocalUDPDataSender {

    private final static String TAG = LocalUDPDataSender.class.getSimpleName();

    private static LocalUDPDataSender instance = null;


    public static LocalUDPDataSender getInstance() {
        if (instance == null)
            instance = new LocalUDPDataSender();
        return instance;
    }

    private LocalUDPDataSender() {

    }

    /**
     * 需要重载一个函数
     * 如果没有指定服务器IP地址，则只能广播出去，让服务器接收处理
     * @param fullProtocalBytes
     * @param dataLen
     * @return
     */
    public int send(byte[] fullProtocalBytes, int dataLen) {
        DatagramSocket ds = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
        if (ds != null && !ds.isConnected()) {
            try {
                ds.connect(InetAddress.getByName(Config.serverIP), Config.serverUDPPort);
            } catch (Exception e) {
                Log.w(UDPModule.TAG, "【IMCORE】send时出错，原因是：" + e.getMessage(), e);
                return -1;
            }
        }
        return UDPUtils.send(ds, fullProtocalBytes, dataLen) ? 0 : -1;

    }


}
