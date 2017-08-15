package com.sunvote.cmd.state;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 执行配对、配置模式操作
 * 本指令涉及硬件的底层配置工作，例如配对，写SN等。
 * SDK提交给模块：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0x70 状态类指令
 * 2	CMD1	命令类型
 * 8 执行配对
 * 9 进入配置模式，允许执行硬件信息操作，包括读写ID，读写SN，版本信息等
 * 3	MODE	配对模式
 * 1 快速配对（可以同时编号）
 * 2 自由配对
 * 4	CHAN	自由配对的时候指定频点
 */

public class ModeOperationStateRequest extends StateBaseCmd {

    public static final int CMD_LENGTH = 4;

    public ModeOperationStateRequest(){}

    public ModeOperationStateRequest(ModeOperationStateResponse response){
        parseCmd(response.toBytes());
        setCmd(REQUEST_CMD);
    }
    /**
     * 状态类指令
     */
    private byte cmd = REQUEST_CMD;

    /**
     * 命令类型
     * 8 执行配对
     * 9 进入配置模式，允许执行硬件信息操作，包括读写ID，读写SN，版本信息等
     */
    private byte cmd1 ;

    /**
     * 配对模式
     * 1 快速配对（可以同时编号）
     * 2 自由配对
     */
    private byte mode ;

    /**
     * 自由配对的时候指定频点
     */
    private byte chan ;

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

    public byte getMode() {
        return mode;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public byte getChan() {
        return chan;
    }

    public void setChan(byte chan) {
        this.chan = chan;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = cmd1;
        result[2] = mode ;
        result[3] = chan ;

        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length >= CMD_LENGTH + start){
            cmd = source[start];
            cmd1 = source[start+1];
            mode = source[start +2];
            chan = source[start + 3];
        }
        return this;
    }
}
