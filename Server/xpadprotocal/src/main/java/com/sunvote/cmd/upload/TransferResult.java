package com.sunvote.cmd.upload;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/9.
 * Email:Eluis@psunsky.com
 * Description:编号模式结果
 * 模块应答表示收到：

 字节	标识符	描述
 1	REQUEST_CMD	0xF3
 2	MSGID	相同的流水号 1-255
 3	MSGTYPE	1
 4	STATUS	1 收到


 */

public class TransferResult extends UploadBaseCmd {

    public static final int CMD_LENGTH = 4 ;

    /**
     * 0xF3
     */
    private byte cmd  = RESPONSE_CMD;

    /**
     * 相同的流水号 1-255
     */
    private byte msgId ;

    /**
     * 1
     */
    private byte msgType ;

    /**
     * STATUS	1 收到
     */
    private byte status ;

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
        result[1] = msgId;
        result[2] = msgType ;
        result[3] = status ;
        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length >= start + CMD_LENGTH) {
            cmd = source[start];
            msgId = source[start + 1];
            msgType = source[start + 2];
            status = source[start + 3];
        }

        return this;
    }
}
