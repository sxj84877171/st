package com.sunvote.udptransfer.work;

import android.os.Handler;
import android.os.HandlerThread;

import com.sunvote.protocal.Protocal;

/**
 * Created by Elvis on 2017/8/14.
 * Email:Eluis@psunsky.com
 * Description:
 * 工作模块封装，提供给与基站通信 和 与SDK通信模块的循坏工作队列。
 */

public class WorkThread extends HandlerThread {

    private boolean isAlive = false;

    private Handler handler;

    /**
     * 给工作命名
     * 方便debug调试 发现处理逻辑执行在哪个线程上面。
     * @param name 工作名
     */
    public WorkThread(String name) {
        super(name);
        start();
        isAlive = true;
        handler = new Handler(getLooper());
    }

    /**
     * 发送消息(任务）处理
     * @param messageBean 消息（任务）
     */
    public void sendMessage(final MessageBean messageBean) {
        if (isAlive) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (messageBean != null && messageBean.executeMethod != null) {
                        messageBean.executeMethod.execute(messageBean);
                        if (messageBean.callback != null) {
                            messageBean.callback.onMsgHasSend(messageBean);
                        }
                    }
                }
            });
        }
    }

    /**
     * 定时发送一个消息（任务）
     * @param messageBean 定时消息
     * @param interval 间隔时间
     */
    public void sendIntervalMessage(final MessageBean messageBean, final long interval) {
        if (isAlive) {
            sendMessage(messageBean);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendIntervalMessage(messageBean,interval);
                }
            }, interval);
        }
    }

    /**
     * 延迟发送一个消息
     * @param messageBean 消息
     * @param last 延迟时间 毫秒为单位
     */
    public void sendMessageDelayed(final MessageBean messageBean,long last){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendMessage(messageBean);
            }
        },last);
    }

    /**
     * 工作模块退出
     * 回收垃圾对象
     */
    public void destroyObject() {
        isAlive = !quit();
    }

    /**
     * 防止调用者没有调用
     *
     * @throws Throwable
     * @see #destroyObject()
     * 垃圾无法回收对象
     */
    @Override
    protected void finalize() throws Throwable {
        if (isAlive) {
            destroyObject();
        }
        super.finalize();
    }

    /**
     * 发送完消息的处理
     */
    public interface Callback {
        void onMsgHasSend(MessageBean messageBean);
    }

    /**
     * 对应的协议的处理操作
     */
    public interface ExecuteMethod {
        void execute(MessageBean messageBean);
    }

    public static class MessageBean {
        /**
         * 接收到的协议，待处理
         */
        public Protocal protocal;
        /**
         * 协议处理完成后的操作
         */
        public Callback callback;
        /**
         * 该协议的处理方式
         */
        public ExecuteMethod executeMethod;

        /**
         * 如果沒有封裝成协议，则直接把数据放在该数据段。
         * 由调用者来确定。
         */
        public byte[] datas;

    }


}
