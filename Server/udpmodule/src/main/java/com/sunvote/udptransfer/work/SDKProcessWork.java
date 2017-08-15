package com.sunvote.udptransfer.work;

import com.sunvote.udptransfer.UDPModule;
import com.sunvote.udptransfer.stream.UDPInputStream;
import com.sunvote.udptransfer.utils.LogUtil;

/**
 * Created by Elvis on 2017/8/14.
 * Email:Eluis@psunsky.com
 * Description:
 * SDK接收处理器
 */

public class SDKProcessWork {

    public final static String NAME = SDKProcessWork.class.getSimpleName();
    private static SDKProcessWork instance = new SDKProcessWork();
    private boolean isRun = false;
    private WorkThread workThread;
    private UDPInputStream inputStream;


    public void setInputStream(UDPInputStream inputStream) {
        this.inputStream = inputStream;
    }

    private SDKProcessWork(){
    }

    public static SDKProcessWork getInstance(){
        return instance;
    }

    public WorkThread getWorkThread() {
        return workThread;
    }

    /**
     * 判断SDK接收处理器是否在工作
     * @return
     */
    public boolean isRunning() {
        return isRun;
    }


    public void start(){
        if(!isRunning()){
            workThread = new WorkThread(NAME);
            isRun = true;
        }
    }

    public void stop(){
        if(isRunning()){
            workThread.destroyObject();
            isRun = false;
        }
    }

    public void execute(byte[] datas,int length){
        LogUtil.i(UDPModule.TAG,"SDKProcessWork(SDK -> Module):",datas);
        WorkThread.MessageBean messageBean = ProtocalFactory.execute(datas,length);
        workThread.sendMessage(messageBean);
    }

    public void postData(byte[] datas,int length){
        LogUtil.i(UDPModule.TAG,"SDKProcessWork(Module -> SDK):",datas);
        if(inputStream != null){
            inputStream.pushDatas(datas,length);
        }
    }

}
