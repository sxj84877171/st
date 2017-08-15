package com.sunvote.udptransfer.work;

import com.sunvote.udptransfer.UDPModule;
import com.sunvote.udptransfer.core.LocalUDPDataSender;
import com.sunvote.udptransfer.core.LocalUDPSocketProvider;
import com.sunvote.udptransfer.utils.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Elvis on 2017/8/14.
 * Email:Eluis@psunsky.com
 * Description:
 * 负责接收基站的信息，然后负责处理，并建立消息通道。
 * 基站接收处理器
 */

public class BaseStationProcessWork {

    public static final String NAME = BaseStationProcessWork.class.getSimpleName();

    private WorkThread workThread;
    private WorkThread receiverThread;
    private boolean isRun = false;
    /**
     * 基站名字
     */
    private String baddh ;
    private String baseName;

    public void setBaddh(String baddh) {
        this.baddh = baddh;
    }

    public String getBaddh() {
        return baddh;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getBaseName() {
        return baseName;
    }

    public WorkThread getWorkThread() {
        return workThread;
    }

    public byte baseID ;

    public byte getBaseID() {
        return baseID;
    }

    public void setBaseID(byte baseID) {
        this.baseID = baseID;
    }

    private byte online = 0x02 ;

    public byte getOnline() {
        return online;
    }

    public void setOnline(byte online) {
        this.online = online;
    }

    private static BaseStationProcessWork instance;

    public static BaseStationProcessWork getInstance() {
        if (instance == null) {
            instance = new BaseStationProcessWork();
        }
        return instance;
    }

    private BaseStationProcessWork() {

    }


    /**
     * 开启基站接收处理器工作
     * 如果已经开启工作，则直接跳过，不处理
     */
    public void start() {
        LogUtil.d(UDPModule.TAG, "基站接收处理器被调用开启工作");
        if (!isRunning()) {
            LogUtil.d(UDPModule.TAG, "基站接收处理器开始工作");
            workThread = new WorkThread(NAME);
            receiverThread = new WorkThread(NAME + "-receiver");
            WorkThread.MessageBean messageBean = new WorkThread.MessageBean();
            messageBean.executeMethod = executeMethod;
            receiverThread.sendMessage(messageBean);
            isRun = true;
        }
    }

    /**
     * 判断基站接收处理器是否在工作
     *
     * @return
     */
    public boolean isRunning() {
        return isRun;
    }

    /**
     * 停止基站接收处理器工作
     * 如果已停止，则直接不处理
     */
    public void stop() {
        LogUtil.d(UDPModule.TAG, "基站接收处理器被调用停止工作");
        if (isRunning()) {
            LogUtil.d(UDPModule.TAG, "基站接收处理器停止工作");
            workThread.destroyObject();
            receiverThread.destroyObject();
            LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
            isRun = false;
        }
    }

    /**
     * 基站接收数据到模块
     */
    private WorkThread.ExecuteMethod executeMethod = new WorkThread.ExecuteMethod() {
        @Override
        public void execute(WorkThread.MessageBean messageBean) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            DatagramSocket localUDPSocket = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
            while (isRunning()) {
                if (localUDPSocket != null && !localUDPSocket.isClosed()) {
                    try {
                        localUDPSocket.receive(packet);
                        byte[] result = new byte[packet.getLength()];
                        for(int i=0;i < packet.getLength() ; i++){
                            result[i] = data[i];
                        }
                        LogUtil.v(UDPModule.TAG,"SDKProcessWork(BaseStation -> Module):",result);
                        WorkThread.MessageBean bean = ProtocalFactory.execute(result, result.length);
                        workThread.sendMessageDelayed(bean,500);
                    } catch (Exception ex) {
                        LogUtil.e(UDPModule.TAG, ex);
                    }
                } else {
                    localUDPSocket = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
                }
            }
        }
    };

    /**
     * 模块发送数据到基站
     * @param datas
     */
    public void postData(final byte[] datas){
        WorkThread.MessageBean messageBean = new WorkThread.MessageBean();
        messageBean.datas = datas ;
        messageBean.executeMethod = new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.v(UDPModule.TAG,"SDKProcessWork(BaseStation -> Module):",datas);
                LocalUDPDataSender.getInstance().send(datas,datas.length);
            }
        };
        workThread.sendMessage(messageBean);
    }


}
