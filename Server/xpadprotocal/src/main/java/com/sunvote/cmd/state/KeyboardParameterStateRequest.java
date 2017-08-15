package com.sunvote.cmd.state;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 3查询和设置键盘参数
 * 包括键盘编号ID，键盘序列号SN，配对码。
 * 提供设置命令，是考虑到APP可能在实际应用或生产设置中，直接通过APP设置会更方便。
 * SDK提交给模块：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0x70 状态类指令
 * 2	CMD1	命令类型
 * 5 查询键盘参数（第3字节后数据填0）
 * 6 设置键盘参数（第3字节后数据才有效）
 * 3-4	KEYID	键盘编号，高位在前
 * （如果全0不修改键盘编号）
 * 5-10	KEYSN	6字节键盘序列号（设置功能不对外开放）
 * （如果第1字节FF表示不修改SN）
 * 11-14	MATCHCODE	4字节配对码（设置功能不对外开放）
 * （如果全FF表示不修改）
 */

public class KeyboardParameterStateRequest extends StateBaseCmd {

    public KeyboardParameterStateRequest(){}

    public KeyboardParameterStateRequest(KeyboardParameterStateResponse response){
        parseCmd(response.toBytes());
        setCmd(REQUEST_CMD);
    }


    public static final int CMD_LENGTH = 14;

    /**
     * 状态类指令
     */
    private byte cmd = REQUEST_CMD ;

    /**
     * 命令类型
     */
    private byte cmd1;

    /**
     * 键盘编号，高位在前
     * （如果全0不修改键盘编号）
     */
    private byte[] keyid = new byte[2];

    /**
     * 6字节键盘序列号（设置功能不对外开放）
     * （如果第1字节FF表示不修改SN）
     */
    private byte[] skysn = new byte[6];

    /**
     * 4字节配对码（设置功能不对外开放）
     * （如果全FF表示不修改）
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

    public byte[] getKeyid() {
        return keyid;
    }

    public void setKeyid(byte[] keyid) {
        this.keyid = keyid;
    }

    public byte[] getSkysn() {
        return skysn;
    }

    public void setSkysn(byte[] skysn) {
        this.skysn = skysn;
    }

    public byte[] getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(byte[] matchCode) {
        this.matchCode = matchCode;
    }


    @Override
    public byte[] toBytes() {
        byte[] result = new byte[3];
        result[0] = cmd;
        result[1] = cmd1;
        for (int i = 0; i < 2; i++) {
            result[2 + i] = keyid[i];
        }
        for (int i = 0; i < 6; i++) {
            result[4 + i] = skysn[i];
        }

        for (int i = 0; i < 4; i++) {
            result[10 + i] = matchCode[i];
        }

        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if (source != null && source.length >= CMD_LENGTH+start) {
            cmd = source[start + 0];
            cmd1 = source[start + 1];
            for (int i = 0; i < 2; i++) {
                keyid[i] = source[start + 2 + i];
            }

            for (int i = 0; i < 6; i++) {
                skysn[i] = source[start + 4 + i];
            }

            for (int i = 0; i < 4; i++) {
                matchCode[i] = source[start + 10 + i];
            }
        }
        return this;
    }
}
