package com.sunvote.udptransfer;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description: 配置类
 */

public class Config {

    /**
     *
     4001 PC 收数据
     4002 Android 收数据
     就这样顶下来。
     */
    public static String serverIP = "" ;
    public static int serverUDPPort = 4001 ;
    public static int localUDPPort = 4002 ;

    public static int keepAliveInterval = 0;
    public static int networkConnectionTimeout = 0;

    public static void setSenseMode(SenseMode mode)
    {
        switch(mode)
        {
            case MODE_3S:
            {
                keepAliveInterval = 3000;// 3s
                networkConnectionTimeout = 3000 * 3 + 1000;// 10s
                break;
            }
            case MODE_10S:
                keepAliveInterval = 10000;// 10s
                networkConnectionTimeout = 10000 * 2 + 1000;// 21s
                break;
            case MODE_30S:
                keepAliveInterval = 30000;// 30s
                networkConnectionTimeout = 30000 * 2 + 1000;// 61s
                break;
            case MODE_60S:
                keepAliveInterval = 60000;// 60s
                networkConnectionTimeout = 60000 * 2 + 1000;// 121s
                break;
            case MODE_120S:
                keepAliveInterval = 120000;// 120s
                networkConnectionTimeout = 120000 * 2 + 1000;// 241s
                break;
        }
    }


    public enum SenseMode
    {
        /**
         * 此模式下：<br>
         * * KeepAlive心跳问隔为3秒；<br>
         * * 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续3 个心跳间隔后仍未收到服务端反馈）。
         */
        MODE_3S,

        /**
         * 此模式下：<br>
         * * KeepAlive心跳问隔为10秒；<br>
         * * 21秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
         */
        MODE_10S,

        /**
         * 此模式下：<br>
         * * KeepAlive心跳问隔为30秒；<br>
         * * 61秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
         */
        MODE_30S,

        /**
         * 此模式下：<br>
         * * KeepAlive心跳问隔为60秒；<br>
         * * 121秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
         */
        MODE_60S,

        /**
         * 此模式下：<br>
         * * KeepAlive心跳问隔为120秒；<br>
         * * 241秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
         */
        MODE_120S
    }

}
