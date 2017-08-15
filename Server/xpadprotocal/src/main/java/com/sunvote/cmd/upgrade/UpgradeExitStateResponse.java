package com.sunvote.cmd.upgrade;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:退出升级
 *  * 字节	标识符	描述
 * 1	REQUEST_CMD	0xF8 固件升级指令
 * 2	CMD1	5 模块退出升级
 * App可显示模块退出升级。
 */

public class UpgradeExitStateResponse extends UpgradeBaseCmd {

    public static final int CMD_LENGTH = 2 ;

    public UpgradeExitStateResponse(){

    }

    public UpgradeExitStateResponse(UpgradeExitStateRequest request){
        parseCmd(request.toBytes());
        setCmd(RESPONSE_CMD);
    }

    /**
     * 0xF8 固件升级指令
     */
    private byte cmd = RESPONSE_CMD;

    /**
     * 5 模块退出升级
     */
    private byte cmd1 ;

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
        result[1] = cmd1;
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
