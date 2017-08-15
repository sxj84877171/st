package com.sunvote.cmd.upgrade;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 然后，模块返回擦除情况：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0xF8 固件升级指令
 * 2	CMD1	3 模块擦除FLASH中
 * 3	SECTORH	已经擦除的扇区数sector的高位
 * 4	SECTORL	已经擦除的扇区数sector的低位
 * 5	OK	1 成功 0失败
 */

public class CleanFlashStateResponse extends UpgradeBaseCmd {

    public static final int CMD_LENGTH = 4;

    public CleanFlashStateResponse(){}

    public CleanFlashStateResponse(CleanFlashStateRequest request){
        parseCmd(request.toBytes());
        setCmd(RESPONSE_CMD);
    }

    /**
     * 固件升级指令
     */
    private byte cmd = 0x78;
    /**
     * 3 模块擦除FLASH
     */
    private byte cmd1 = 3;
    /**
     * 擦除的扇区数sector的高位，暂为0
     */
    private byte sectorh;
    /**
     * 要擦除的扇区数sector的低位
     */
    private byte sectorl;

    /**
     *1 成功 0失败
     */
    private byte ok = 0x01;

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

    public byte getSectorh() {
        return sectorh;
    }

    public void setSectorh(byte sectorh) {
        this.sectorh = sectorh;
    }

    public byte getSectorl() {
        return sectorl;
    }

    public void setSectorl(byte sectorl) {
        this.sectorl = sectorl;
    }

    public byte getOk() {
        return ok;
    }

    public void setOk(byte ok) {
        this.ok = ok;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = cmd1;
        result[2] = sectorh;
        result[3] = sectorl;

        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if (source != null && source.length > start + CMD_LENGTH) {
            cmd = source[start];
            cmd1 = source[start + 1];
            sectorh = source[start + 2];
            sectorl = source[start + 3];
        }
        return this;
    }
}
