package com.sunvote.cmd.state;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 执行配对、配置模式操作
 * 模块执行配对，2秒内应答：（SDK 2秒内不要查询其他指令）
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0xF0 状态类指令应答
 * 2	CMD1	命令类型
 * 8 返回配对状态
 * 9 返回配置状态
 * 3	OK	1 成功
 * 2 失败（没配对成功或没收到配置指令，后面信息不用解析）
 * 4	CHAN	配对到的基站频点
 * 5,6	KEYID	当前键盘编号（可能被快速配对修改）
 * 7-12	KEYSN	6字节键盘SN
 * 13-16	MATCHCODE	4字节键盘配对码
 * 注意：返回数据，从第4字节起，和4.3节查询和设置键盘参数是一模一样的
 */

public class ModeOperationStateResponse extends StateBaseCmd {

    public static final int CMD_LENGTH = 16;

    public ModeOperationStateResponse(){}

    public ModeOperationStateResponse(ModeOperationStateRequest request){
        parseCmd(request.toBytes());
        setCmd(RESPONSE_CMD);
    }

    /**
     * 状态类指令应答
     */
    private byte cmd = RESPONSE_CMD;

    /**
     * 命令类型
     * 8 返回配对状态
     * 9 返回配置状态
     */
    private byte cmd1;

    /**
     * 1 成功
     * 2 失败（没配对成功或没收到配置指令，后面信息不用解析）
     */
    private byte ok;

    /**
     * CHAN	配对到的基站频点
     */
    private byte chan;

    /**
     * KEYID	当前键盘编号（可能被快速配对修改）
     */
    private byte[] keyId = new byte[2];

    /**
     * KEYSN	6字节键盘SN
     */
    private byte[] keySn = new byte[6];

    /**
     * MATCHCODE	4字节键盘配对码
     */
    private byte[] matchCode = new byte[4];


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

    public byte[] getKeyId() {
        return keyId;
    }

    public void setKeyId(byte[] keyId) {
        this.keyId = keyId;
    }

    public byte[] getKeySn() {
        return keySn;
    }

    public void setKeySn(byte[] keySn) {
        this.keySn = keySn;
    }

    public byte[] getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(byte[] matchCode) {
        this.matchCode = matchCode;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = cmd1;
        result[2] = ok;
        result[3] = chan;
        for (int i = 0; i < 2; i++) {
            result[4 + i] = keyId[i];
        }
        for (int i = 0; i < 6; i++) {
            result[6 + i] = keySn[i];
        }

        for (int i = 0; i < 4; i++) {
            result[12 + i] = matchCode[i];
        }

        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if (source != null && source.length >= start + CMD_LENGTH) {
            cmd = source[start];
            cmd1 = source[start + 1];
            ok = source[start + 2];
            chan = source[start + 3];
            for (int i = 0; i < 2; i++) {
                keyId[i] = source[start + i + 4];
            }
            for (int i = 0; i < 6; i++) {
                keySn[i] = source[start + i + 6];
            }

            for (int i = 0; i < 4; i++) {
                matchCode[i] = source[start + 12 + i];
            }
        }
        return this;
    }
}
