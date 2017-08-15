package com.sunvote.cmd.upload;

import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/9.
 * Email:Eluis@psunsky.com
 * Description:编号模式结果
 * SDK提交给模块：
 * <p>
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0x73
 * 2	MSGID	流水号 1-255
 * 3	MSGTYPE	后继信息包的打包格式
 * 1 编号模式
 * 4	ANSTYPE	使用编号模式时候的数据包类型
 * 例如：
 * 0状态
 * 1 单值（签到、表决、评议）
 * 2 单选多选
 * 3 评分
 * 4 排序
 * 5 填空
 * ...
 * 21 批次单值结果
 * 26另选他人
 * 5-24	ANSDATA	根据结果类型ANSTYPE的不同，有不同的数据长度和含义
 * 具体参考《通讯协议-政务商务-表决器部分-V5.0.docx》第三章 上传单包类的描述
 * 对于要传递按键时间KEYTIME值的，需要SDK自己计算
 */

public class NumberingModeResult extends UploadBaseCmd {

    public NumberingModeResult(){}

    public static final int CMD_LENGTH = 24 ;

    /**
     * REQUEST_CMD	0x73
     */
    private byte cmd = REQUEST_CMD;

    /**
     * 流水号 1-255
     */
    private byte msgId = 0;

    /**
     * 后继信息包的打包格式
     * 1 编号模式
     */
    private byte msgType;

    /**
     * ANSTYPE	    使用编号模式时候的数据包类型
     * 例如：
     * 0状态
     * 1 单值（签到、表决、评议）
     * 2 单选多选
     * 3 评分
     * 4 排序
     * 5 填空
     * ...
     * 21 批次单值结果
     * 26另选他人
     */
    private byte ansType;

    /**
     * 根据结果类型ANSTYPE的不同，有不同的数据长度和含义
     * 具体参考《通讯协议-政务商务-表决器部分-V5.0.docx》第三章 上传单包类的描述
     * 对于要传递按键时间KEYTIME值的，需要SDK自己计算
     */
    private byte[] ansData = new byte[32];

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getMsgId() {
        return msgId;
    }

    public void setMsgId(byte msgId) {
        this.msgId = msgId;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public byte getAnsType() {
        return ansType;
    }

    public void setAnsType(byte ansType) {
        this.ansType = ansType;
    }

    public void setAnsData(byte[] ansData) {
        this.ansData = ansData;
    }

    public byte[] getAnsData() {
        return ansData;
    }

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
