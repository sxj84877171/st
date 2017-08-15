package com.sunvote.cmd.state;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 主要是考虑到xPAD应用可能是当为基站，也可能当为键盘，所以需要查询和设置模块的工作模式，功能特性兼容需要了解版本号
 * 模块应答：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0xF0 状态类指令应答
 * 2	CMD1	命令类型
 * 1 返回当前工作模式和版本
 * 3	MODE	模块当前主从模式
 * 1 基站模式（主）
 * 2 键盘模式（从）
 * 5	HMODEL	硬件型号，数字
 * 6-8	SVER	固件版本，3位数字，例如 0.1.0
 */

public class WorkPattenStateResponse extends StateBaseCmd {

    public static final int CMD_LENGTH = 7;

    public WorkPattenStateResponse() {
    }

    public WorkPattenStateResponse(WorkPattenStateRequest request) {
        parseCmd(request.toBytes());
        setCmd(RESPONSE_CMD);
    }

    /**
     * 状态类指令应答
     */
    private byte cmd = RESPONSE_CMD;

    /**
     * 命令类型
     */
    private byte cmd1;

    /**
     * 模块当前主从模式
     * 1 基站模式（主）
     * 2 键盘模式（从）
     */
    private byte mode;

    /**
     * 硬件型号，数字
     */
    private byte hmodel;

    /**
     * 固件版本，3位数字，例如 0.1.0
     */
    private byte[] sver = new byte[3];


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

    public byte getHmodel() {
        return hmodel;
    }

    public void setHmodel(byte hmodel) {
        this.hmodel = hmodel;
    }

    public byte[] getSver() {
        return sver;
    }

    public void setSver(byte[] sver) {
        this.sver = sver;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = cmd1;
        result[2] = mode;
        result[3] = hmodel;
        for (int i = 0; i < 3; i++) {
            result[4 + i] = sver[i];
        }
        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if (source != null && source.length >= CMD_LENGTH + start) {
            cmd = source[start + 0];
            cmd1 = source[start + 1];
            mode = source[start + 2];
            hmodel = source[start + 3];
            for (int i = 0; i < 3; i++) {
                sver[i] = source[start + 4 + i];
            }
        }
        return this;
    }
}
