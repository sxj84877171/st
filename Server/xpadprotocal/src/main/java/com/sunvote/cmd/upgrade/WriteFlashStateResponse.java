package com.sunvote.cmd.upgrade;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:按页写FLASH
 * 模块返回写情况：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0xF8 固件升级指令
 * 2	CMD1	4 模块写页执行情况
 * 3	PAGEH	当前数据的页地址高位
 * 4	PAGEL	当前数据的页地址低位
 * 5	STATUS	1 执行写OK
 * 0 失败
 */

public class WriteFlashStateResponse extends UpgradeBaseCmd{
    public static final int CMD_LENGTH = 5 ;

    public WriteFlashStateResponse(){}

    public WriteFlashStateResponse(WriteFlashStateRequest request){
        parseCmd(request.toBytes());
        setCmd(RESPONSE_CMD);
    }

    /**
     * 固件升级指令
     */
    private byte cmd = RESPONSE_CMD;

    /**
     * 4 模块按页写FLASH
     */
    private byte cmd1 ;

    /**
     * 当前数据的页地址高位
     */
    private byte pageh ;

    /**
     * 当前数据的页地址低位
     */
    private byte pagel ;

    /**
     * 1 执行写OK
     * 0 失败
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

    public byte getPageh() {
        return pageh;
    }

    public void setPageh(byte pageh) {
        this.pageh = pageh;
    }

    public byte getPagel() {
        return pagel;
    }

    public void setPagel(byte pagel) {
        this.pagel = pagel;
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
        result[2] = pageh ;
        result[3] = pagel ;
        result[4] = status;
        return result;
    }


    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length >= start + CMD_LENGTH){
            cmd = source[start] ;
            cmd1 = source[start + 1] ;
            pageh = source[start + 2] ;
            pagel = source[start + 3] ;
            status = source[start + 4];
        }
        return this;
    }
}
