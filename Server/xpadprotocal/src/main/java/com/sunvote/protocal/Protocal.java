package com.sunvote.protocal;

import com.sunvote.cmd.ICmd;
import com.sunvote.utils.Crc16;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description:
 * 1.1数据包的格式
 F5   AA   AA   LEN   有效数据 CRC1  CRC2
 */

public class Protocal<C extends ICmd> implements IProtocal {

    protected final static byte[] HEADER =  new byte[]{(byte) 0xF5, (byte) 0xAA, (byte) 0xAA};

    /**
     * F5   AA   AA
     */
    protected byte[] header = HEADER ;

    /**
     * 消息体长度
     */
    protected byte length;

    /**
     * 消息体内容
     * !! 不能超过126个字节
     */
    protected byte[] messageBody;

    /**
     * crc校验码
     */
    protected byte[] crcs = new byte[]{0x00,0x00};

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public void setCrcs(byte[] crcs) {
        this.crcs = crcs;
    }

    public void setCmd(C cmd) {
        this.cmd = cmd;
    }

    public C getCmd() {
        return cmd;
    }

    public void roundLength() {
        length = 0;
        if(messageBody == null){
            if(cmd != null){
                messageBody = cmd.toBytes();
            }
        }
        if (messageBody != null) {
            length = (byte) (messageBody.length + 2);
        }
    }

    private C cmd ;

    public byte[] toBytes() {
        if (length == 0) {
            roundLength();
        }
        byte[] res = new byte[length + 4];
        int i = 0;
        // 头信息
        for (; i < header.length; i++) {
            res[i] = header[i];
        }
        // 长度信息
        res[i] = length;
        i++;

        //内容
        for (; i < length + header.length + 1 - crcs.length; i++) {
            res[i] = messageBody[i - header.length-1];
        }


        //CRC校验码
        roundCrc(res);
        for (; i < length + header.length + 1 ; i++) {
            res[i] = crcs[i - (length + header.length + 1 - crcs.length)];
        }
        return res;
    }

    public void roundCrc(byte[] data){
        int crcValue = Crc16.getUnsignedShort(Crc16.crc16(data, data.length - 4 - 2));
        crcs[0] = (byte) (crcValue >> 8);
        crcs[1] = (byte) (crcValue);
    }


    public static boolean checkHeader(byte[] datas){
        if(datas!= null && datas.length >= 3){
            return datas[0] == HEADER[0] && datas[1] ==HEADER[1] && datas[2] == HEADER[2] ;
        }
        return false;
    }

    public static int parseLength(byte[] datas){
        if(datas != null && datas.length >= 4){
            return datas[3] & 0xFF ;
        }
        return 0;
    }

}
