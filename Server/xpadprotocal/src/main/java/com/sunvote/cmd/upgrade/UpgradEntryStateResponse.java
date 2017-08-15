package com.sunvote.cmd.upgrade;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 模块执行，然后100ms内应答：（如果模块没有固件或上次升级失败，也会重复询问这条指令）
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0xF8 固件升级指令应答
 * 2	CMD1	命令类型
 * 2 模块需要保持在升级模式吗？（注意是2）
 */

public class UpgradEntryStateResponse extends UpgradeBaseCmd {

    public static final int CMD_LENGTH = 2 ;

    public UpgradEntryStateResponse(){}

    public UpgradEntryStateResponse(UpgradEntryStateResponse response){
        parseCmd(response.toBytes());
        setCmd(RESPONSE_CMD);
    }

    /**
     * 固件升级指令应答
     */
    private byte cmd = RESPONSE_CMD;

    /**
     * 命令类型
     * 2 模块需要保持在升级模式吗？（注意是2）
     */
    private byte cmd1 = 0x02;


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
