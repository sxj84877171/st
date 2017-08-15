package com.sunvote.cmd.upgrade;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 退出升级
 * App提交给模块：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0x78 固件升级指令
 * 2	CMD1	5 退出升级模式
 * 3	STATUS	1 升级成功
 * 2 升级失败
 * <p>
 * <p>
 * 然后，模块应答：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0xF8 固件升级指令
 * 2	CMD1	5 模块退出升级
 * App可显示模块退出升级。
 */

public class UpgradeExitStateRequest extends UpgradeBaseCmd {

    public static final int CMD_LENGTH = 3 ;

    public UpgradeExitStateRequest(){}

    public UpgradeExitStateRequest(UpgradeExitStateResponse response){
        parseCmd(response.toBytes());
        setCmd(REQUEST_CMD);
    }

    /**
     * 固件升级指令
     */
    private byte cmd = REQUEST_CMD;
    /**
     * 5 退出升级模式
     */
    private byte cmd1 ;

    /**
     * 1 升级成功
     * 2 升级失败
     */
    private byte status;

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

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = cmd1;
        result[2] = status ;
        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length > start + CMD_LENGTH){
            cmd = source[start];
            cmd1 = source[start+1];
            status = source[start +2];
        }
        return this;
    }
}
