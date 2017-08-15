package com.sunvote.cmd.state;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * <p>
 * 查询和设置模块工作模式
 * 主要是考虑到xPAD应用可能是当为基站，也可能当为键盘，所以需要查询和设置模块的工作模式，功能特性兼容需要了解版本号
 * SDK提交给模块：
 * <p>
 * <p>
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0x70 状态类指令
 * 2	CMD1	命令类型
 *              1 读取
 *              2 设置模块工作模式
 * 3	MODE	CMD1=2时有效
 * 1 设置模块在基站模式
 * 2 设置模块在键盘模式
 */

public class WorkPattenStateRequest extends StateBaseCmd {
    public static final int CMD_LENGTH = 3 ;

    public WorkPattenStateRequest(){}

    public WorkPattenStateRequest(WorkPattenStateResponse response){
        parseCmd(response.toBytes());
        setCmd(REQUEST_CMD);
    }

    /**
     * 状态类指令
     */
    private byte cmd = 0x70;

    /**
     * 命令类型
     * 1 读取
     * 2 设置模块工作模式
     */
    private byte cmd1 ;

    /**
     * CMD1=2时有效
     * 1 设置模块在基站模式
     * 2 设置模块在键盘模式
     */
    private byte mode ;

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

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = cmd1;
        result[2] = mode;
        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length >= CMD_LENGTH + start) {
            cmd = source[start+ 0];
            cmd1 = source[start + 1];
            mode = source[start +2];
        }
        return this;
    }
}
