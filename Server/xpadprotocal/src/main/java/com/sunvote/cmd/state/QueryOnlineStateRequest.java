package com.sunvote.cmd.state;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 查询在线状态、基站识别模式、频点、信号强度、刚才发送过、基站名称，同时告诉模块电池电压（用于键盘报告状态）
 * 这个指令由APP定时调用，例如1秒间隔。
 * SDK提交给模块：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0x70 状态类指令
 * 2	CMD1	命令类型
 * 7 查询键盘在线状态
 * 3	VOLT	平板锂电池电压（如果取不到，用电量百分比？）
 * 值x0.02V就是电压—xPad电压会7.8V？
 * 4	KEYINSTATUS	用户输入状态
 * 0 表决开始后没输入
 * 1 有输入，但未提交
 * 2 有输入，已经确认提交
 */

public class QueryOnlineStateRequest extends StateBaseCmd {


    public static final int CMD_LENGTH = 4;

    public QueryOnlineStateRequest(){}

    public QueryOnlineStateRequest(QueryOnlineStateResponse response){
        parseCmd(response.toBytes());
        setCmd(RESPONSE_CMD);
    }
    /**
     * 状态类指令
     */
    private byte cmd = 0x70;


    /**
     * 命令类型
     * 7 查询键盘在线状态
     */
    private byte cmd1;


    /**
     * 平板锂电池电压（如果取不到，用电量百分比？）
     * 值x0.02V就是电压—xPad电压会7.8V？
     */
    private byte volt;

    /**
     * 用户输入状态
     * 0 表决开始后没输入
     * 1 有输入，但未提交
     * 2 有输入，已经确认提交
     */
    private byte keyInstatus;

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getCmd1() {
        return cmd1;
    }

    public void setCmd1(byte cmd1) {
        this.cmd1 = cmd1;
    }

    public byte getVolt() {
        return volt;
    }

    public void setVolt(byte volt) {
        this.volt = volt;
    }

    public byte getKeyInstatus() {
        return keyInstatus;
    }

    public void setKeyInstatus(byte keyInstatus) {
        this.keyInstatus = keyInstatus;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = cmd1;
        result[2] = volt;
        result[3] = keyInstatus;
        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {

        if (source != null && source.length >= CMD_LENGTH + start) {
            cmd = source[start + 0];
            cmd1 = source[start + 1];
            volt = source[start + 2];
            keyInstatus = source[start + 3];
        }

        return this;
    }
}
