package com.sunvote.cmd.upload;

import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/9.
 * Email:Eluis@psunsky.com
 * Description:
 * SDK发送：
 * <p>
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0x73使用硬件序列号提交单包结果
 * 2	MSGID	流水号 1-255
 * 3	MSGTYPE	后继信息包的打包格式
 * 2 序列号模式
 * 4	ANSTYPE	数据包类型（根据ANSTYPE的不同，第3字节起数据格式有不同定义）
 * 例如：
 * 1 统一BCD码格式
 * 5-20	ANSDATA	根据结果类型ANSTYPE的不同，有不同的数据长度和含义
 * 一般只到第20字节，从ANSTYPE起占用16字节
 * 和《通讯协议-政务商务-表决器部分-V5.0.docx》3.6节序列号结果对照看，一般前2字节是按键时间值KEYTIME
 * 21-26		空
 */

public class SequenceFormatResult extends UploadBaseCmd {

    public static final int CMD_LENGTH = 26;

    public SequenceFormatResult(){}

    /**
     * 使用硬件序列号提交单包结果
     */
    private byte cmd = REQUEST_CMD;

    /**
     * 流水号 1-255
     */
    private byte msgId;

    /**
     * 后继信息包的打包格式
     * 2 序列号模式
     */
    private byte msgType;

    /**
     * 数据包类型（根据ANSTYPE的不同，第3字节起数据格式有不同定义）
     * 例如：
     * 1 统一BCD码格式
     */
    private byte ansType;

    /**
     * 根据结果类型ANSTYPE的不同，有不同的数据长度和含义
     * 一般只到第20字节，从ANSTYPE起占用16字节
     * 和《通讯协议-政务商务-表决器部分-V5.0.docx》3.6节序列号结果对照看，一般前2字节是按键时间值KEYTIME
     */
    private byte[] ansData = new byte[32];


    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = msgId;
        result[2] = msgType ;
        result[3] = ansType ;
        for(int i=0; i < 32 && i < ansData.length ;i++){
            result[i + 4] = ansData[i] ;
        }
        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {

        if(source != null && source.length >= start + CMD_LENGTH) {
            cmd = source[start];
            msgId = source[start + 1];
            msgType = source[start + 2];
            ansType = source[start + 3];
            for (int i = 0; i < 32 && i < ansData.length; i++) {
                ansData[i] = source[start + i+ 4];
            }
        }

        return this;
    }
}
