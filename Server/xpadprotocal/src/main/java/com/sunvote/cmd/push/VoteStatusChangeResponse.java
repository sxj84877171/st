package com.sunvote.cmd.push;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description:
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0x72 投票信标变化
 * 2	BADDH	基站编号，保证有效数据从第3字节开始，和查询指令相同
 * 3,4	NOWT	时标值，2字节，高位在前
 * 从投票启动开始的时间，用于表决器同步计时，20ms为单位，最大约21分钟，最大0xFFFF不自动变为0
 * 5	DATAPOS	表决序号（结果记录序号）
 * 1-0xFF，值发生变化的时候，代表是新的一轮表决开始
 * <p>
 * 6	MODE	投票模式
 * Bit7=1表示继续表决，表决器重新提交数据，用于系统恢复，表决器可继续输入或使用原先结果；=0 正常表决
 * 低7位是表决模式：
 * 1-9是政务应用 10-19商务应用和教育 20-29多项和批次 30-39二维表评测 40-50管理类
 * <p>
 * 7-25	MODES	投票参数，具体和MODE有关，不同模式下参数意义不同
 */

public class VoteStatusChangeResponse extends PushBaseCmd {

    public static final byte RESPONE_CMD = (byte) 0xF2 ;

    public VoteStatusChangeResponse(){

    }

    public VoteStatusChangeResponse(VoteStatusChangeRequest request){
        parseCmd(request.toBytes());
        setCmd(RESPONE_CMD);
    }

    public static final int CMD_LENGTH = 25 ;
    /**
     * 0x72  投票信标变化
     */
    private byte cmd = RESPONE_CMD;

    /**
     * 基站编号
     */
    private byte baddh;

    /**
     * 时标值，2字节，高位在前
     * 从投票启动开始的时间，用于表决器同步计时，20ms为单位，最大约21分钟，最大0xFFFF不自动变为0
     */
    private byte[] nowt = new byte[2];

    /**
     * 表决序号（结果记录序号）
     * 1-0xFF，值发生变化的时候，代表是新的一轮表决开始
     */
    private byte datapos;

    /**
     * 投票模式
     * Bit7=1表示继续表决，表决器重新提交数据，用于系统恢复，表决器可继续输入或使用原先结果；=0 正常表决
     * 低7位是表决模式：
     * 1-9是政务应用 10-19商务应用和教育 20-29多项和批次 30-39二维表评测 40-50管理类
     */
    private byte mode;

    /**
     * 投票参数，具体和MODE有关，不同模式下参数意义不同
     */
    private byte[] modes = new byte[19];

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getBaddh() {
        return baddh;
    }

    public void setBaddh(byte baddh) {
        this.baddh = baddh;
    }

    public byte[] getNowt() {
        return nowt;
    }

    public void setNowt(byte[] nowt) {
        this.nowt = nowt;
    }

    public byte getDatapos() {
        return datapos;
    }

    public void setDatapos(byte datapos) {
        this.datapos = datapos;
    }

    public byte getMode() {
        return mode;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public byte[] getModes() {
        return modes;
    }

    public void setModes(byte[] modes) {
        this.modes = modes;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];

        result[0] = cmd ;
        result[1] = baddh;
        for (int i = 0; i < 2; i++) {
            result[2 + i] = nowt[i];
        }
        result[4] = datapos ;
        result[5] = mode ;
        for (int i = 0; i < 19; i++) {
            result[6+i] = modes[i] ;
        }
        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length >= CMD_LENGTH + start) {
            cmd = source[start];
            baddh = source[start + 1];

            for (int i = 0; i < 2; i++) {
                nowt[i] = source[start + 2 + i];
            }
            datapos = source[start + 4];
            mode = source[start + 5];
            for (int i = 0; i < 19; i++) {
                modes[i] = source[start + 6 + i];
            }
        }
        return this;
    }

}
