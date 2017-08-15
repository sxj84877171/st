package com.sunvote.cmd.upgrade;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 按页写FLASH
 * 由于指令长度不能大于64，我们限定每次只能改写32字节。每32字节代表一页（page，FLASH概念），页地址从0开始，总共多少页的算法，类似扇区算法。
 * <p>
 * App提交给模块：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0x78 固件升级指令
 * 2	CMD1	4 模块按页写FLASH
 * 3	PAGEH	当前数据的页地址高位
 * 4	PAGEL	当前数据的页地址低位
 * 5-36	DATAS	32字节的数据
 */

public class WriteFlashStateRequest extends UpgradeBaseCmd {

    public static final int CMD_LENGTH = 32 ;

    public WriteFlashStateRequest(){}

    public WriteFlashStateRequest(WriteFlashStateResponse response){
        parseCmd(response.toBytes());
        setCmd(REQUEST_CMD);
    }

    /**
     * 固件升级指令
     */
    private byte cmd = REQUEST_CMD;

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
     * 32字节的数据
     */
    private byte[] datas = new byte[32];


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

    public byte[] getDatas() {
        return datas;
    }

    public void setDatas(byte[] datas) {
        this.datas = datas;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = cmd1;
        result[2] = pageh ;
        result[3] = pagel ;
        for(int i=0; i < 32 && i < datas.length ;i++){
            result[i + 4] = datas[i] ;
        }

        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length >= start + CMD_LENGTH){
            cmd = source[start] ;
            cmd1 = source[start + 1] ;
            pageh = source[start + 2] ;
            pagel = source[start + 3] ;
            for(int i=0; i < 32  ;i++){
                 datas[i] = source[start + i + 4];
            }
        }
        return this;
    }
}
