package com.sunvote.udptransfer;

import com.sunvote.udptransfer.stream.UDPInputStream;
import com.sunvote.udptransfer.stream.UDPOutputStream;
import com.sunvote.udptransfer.utils.LogUtil;
import com.sunvote.udptransfer.work.BaseStationProcessWork;
import com.sunvote.udptransfer.work.SDKProcessWork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description:Client 配置使用
 * 1.首先开启wifi UDP的监听。
 * 2.开启SDK写入数据的监听
 * <p>
 * 外面调用只需要调用这个类，所有的开放的东西都从这个接口调用
 * <p>
 * SDK -> UDPModule -> BaseStation
 * BaseStation -> UDPModule -> SDK
 * SDK->UDPModule->SDK
 * BaseStation->UDPModule -> BaseStation
 * UDPModule -> SDK
 */
public class UDPModule {

    public static final String TAG = UDPModule.class.getSimpleName();

    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * 由外界全部配置
     *
     * @param serverIp   服务器IP地址
     * @param serverPort 服务器端口
     * @param localPort  本地端口
     */
    public UDPModule(String serverIp, int serverPort, int localPort) {
        Config.serverIP = serverIp;
        Config.localUDPPort = localPort;
        Config.serverUDPPort = serverPort;
        init();
    }

    private void init() {
        UDPInputStream udpInputStream = new UDPInputStream();
        UDPOutputStream udpOutputStream = new UDPOutputStream();
        SDKProcessWork.getInstance().setInputStream(udpInputStream);
        BaseStationProcessWork.getInstance().start();
        SDKProcessWork.getInstance().start();
        inputStream = udpInputStream;
        outputStream = udpOutputStream;
    }

    /**
     * 自动查找服务端IP地址
     *
     * @param serverPort 服务器端口
     * @param localPort  本地端口
     */
    public UDPModule(int serverPort, int localPort) {
        Config.localUDPPort = localPort;
        Config.serverUDPPort = serverPort;
        init();
    }

    /**
     * 自动查找服务端地址，并且本地和远程端口一致
     *
     * @param port 远程和与本地使用同一个端口
     */
    public UDPModule(int port) {
        Config.localUDPPort = port;
        Config.serverUDPPort = port;
        init();
    }

    /**
     * 无需额外配置，使用默认配置
     */
    public UDPModule() {
        init();
    }

    /**
     * 获取输入流
     *
     * @return
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * 获取输出流
     *
     * @return
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * 释放资源
     */
    public void release() {

        BaseStationProcessWork.getInstance().stop();
        SDKProcessWork.getInstance().stop();

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开启module中日志输出
     */
    public void enableModuleLog() {
        LogUtil.enableLog();
    }

    /**
     * 关闭module中日志输出
     */
    public void disableModuleLog() {
        LogUtil.disableLog();
    }

}
