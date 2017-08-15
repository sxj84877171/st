package com.sunvote.udptransfer.core;

import android.os.Message;

import com.sunvote.udptransfer.Config;
import com.sunvote.udptransfer.UDPModule;
import com.sunvote.udptransfer.utils.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description:
 */

public class LocalUDPDataReciever {

    private final static String TAG = LocalUDPDataReciever.class.getSimpleName();

    private static LocalUDPDataReciever instance = null;

    private Thread thread = null;

    public static LocalUDPDataReciever getInstance() {
        if (instance == null) {
            instance = new LocalUDPDataReciever();
        }
        return instance;
    }

    private LocalUDPDataReciever() {
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public void startup() {
        stop();

        try {
            thread = new Thread(new Runnable() {
                public void run() {
            try {
                LogUtil.d(UDPModule.TAG, "【IMCORE】本地UDP端口侦听中，端口=" + Config.localUDPPort + "...");
                p2pListeningImpl();
            } catch (Exception eee) {
                LogUtil.w(UDPModule.TAG, "【IMCORE】本地UDP监听停止了(socket被关闭了?)," + eee.getMessage(), eee);
            }
                }
            });
            thread.start();
        } catch (Exception e) {
            LogUtil.w(UDPModule.TAG, "【IMCORE】本地UDPSocket监听开启时发生异常," + e.getMessage(), e);
        }
    }

    private void p2pListeningImpl() throws Exception {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            DatagramSocket localUDPSocket = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
            if (localUDPSocket != null && !localUDPSocket.isClosed()) {
                localUDPSocket.receive(packet);
                Message m = Message.obtain();
                m.obj = packet;
            }
        }
    }

}
