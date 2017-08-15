package com.sunvote.cmd.upgrade;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:进入升级模式
 * 因为平常模块是在正常工作模式，升级模式是特殊模式，所以先要进入升级模式。
 * App提交给模块：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0x78 固件升级指令
 * 2	CMD1	命令类型
 * 1 模块进入固件升级模式
 * 2 模块保持在固件升级模式
 * App必须100ms应答如下，否则模块就会退出升级模块
 */

public class UpgradEntryStateRequest extends UpgradeBaseCmd {

    public static final int CMD_LENGTH = 2;

    public UpgradEntryStateRequest(){}

    public UpgradEntryStateRequest(UpgradEntryStateResponse response){
        parseCmd(response.toBytes());
        setCmd(REQUEST_CMD);
    }

    /**
     * 固件升级指令
     */
    private byte cmd = REQUEST_CMD;

    /**
     * 命令类型
     * 1 模块进入固件升级模式
     * 2 模块保持在固件升级模式
     * App必须100ms应答如下，否则模块就会退出升级模块
     */
    private byte cmd1;

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

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = cmd1 ;
        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length > start + CMD_LENGTH){
            cmd = source[start];
            cmd1 = source[start+1];
        }
        return this;
    }
}
