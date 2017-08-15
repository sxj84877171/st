package com.sunvote.cmd.state;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 模块应答：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0xF0 状态类指令应答
 * 2	CMD1	命令类型
 * 5 返回当前键盘参数
 * 6 返回当前键盘参数
 * 3	OK	1 成功
 * 4	CHAN	键盘当前频点
 * 5,6	KEYID	当前键盘编号，高位在前
 * 7-12	KEYSN	6字节键盘SN
 * 13-16	MATCHCODE	4字节键盘配对码
 * 17	HMODEL	硬件型号，数字
 * 18-20	SVER	固件版本，3位数字，例如 0.1.0
 */

public class KeyboardParameterStateResponse extends StateBaseCmd {

    public static final int CMD_LENGTH = 20;

    public KeyboardParameterStateResponse(){}

    public KeyboardParameterStateResponse(KeyboardParameterStateRequest request){
        parseCmd(request.toBytes());
        setCmd(RESPONSE_CMD);
    }
    /**
     * 状态类指令应答
     */
    private byte cmd = RESPONSE_CMD;

    /**
     * 命令类型
     * 5 返回当前键盘参数
     * 6 返回当前键盘参数
     */
    private byte cmd1 ;

    /**
     * 1 成功
     */
    private byte ok ;

    /**
     * 键盘当前频点
     */
    private byte chan ;

    /**
     * 当前键盘编号，高位在前
     */
    private byte[] keyid = new byte[2];

    /**
     * KEYSN	6字节键盘SN
     */
    private byte[] keysn = new byte[6];

    /**
     * 4字节键盘配对码
     */
    private byte[] matchCode = new byte[4];

    /**
     * 硬件型号，数字
     */
    private byte hmode ;

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

    public byte getOk() {
        return ok;
    }

    public void setOk(byte ok) {
        this.ok = ok;
    }

    public byte getChan() {
        return chan;
    }

    public void setChan(byte chan) {
        this.chan = chan;
    }

    public byte[] getKeyid() {
        return keyid;
    }

    public void setKeyid(byte[] keyid) {
        this.keyid = keyid;
    }

    public byte[] getKeysn() {
        return keysn;
    }

    public void setKeysn(byte[] keysn) {
        this.keysn = keysn;
    }

    public byte[] getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(byte[] matchCode) {
        this.matchCode = matchCode;
    }

    public byte getHmode() {
        return hmode;
    }

    public void setHmode(byte hmode) {
        this.hmode = hmode;
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
        result[2] = ok;
        result[3] = chan;
        for (int i = 0; i < 2; i++) {
            result[4 + i] = keyid[i];
        }
        for (int i = 0; i < 6; i++) {
            result[6 + i] = keysn[i];
        }

        for (int i = 0; i < 4; i++) {
            result[12 + i] = matchCode[i];
        }
        result[16] = hmode;
        for (int i = 0; i < 3; i++) {
            result[17 + i] = sver[i];
        }
        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {

        if(source != null && source.length >= CMD_LENGTH + start) {
            cmd = source[start + 0];
            cmd1 = source[start + 1];
            ok = source[start + 2];
            chan = source[start + 3];

            for (int i = 0; i < 2; i++) {
                keyid[i] = source[start + 4 + i];
            }
            for (int i = 0; i < 6; i++) {
                keysn[i] = source[start + 6 + i];
            }

            for (int i = 0; i < 4; i++) {
                matchCode[i] = source[start + 12 + i];
            }
            hmode = source[16];
            for (int i = 0; i < 3; i++) {
                sver[i] = source[start + 17 + i];
            }
        }

        return this;
    }
}
