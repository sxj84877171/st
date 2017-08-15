package com.sunvote.cmd.state;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 * 模块应答：
 * 字节	标识符	描述
 * 1	REQUEST_CMD	0xF0 状态类指令应答
 * 2	CMD1	命令类型
 * 7 返回当前在线状态
 * 3	ONLINE	键盘是否在线
 * 1 在线
 * 2 离线
 * 4	IDMODE	基站的识别模式
 * 定义同信标变化通知里面的，APP可以不处理此信息
 * 5	CHAN	当前频点号
 * 6	RSSI	接收到基站的信号强度RSSI值，负数，越小表示信号越大
 * 7	TX	1表示刚才1秒内有提交数据过
 * 0 表示没有
 * 8	RX	1 表示刚才1秒内收到过基站的投票指令（特指投票中）
 * 0 表示没有
 * 9	BaseID	基站编号
 * 10,11	KEYID	当前键盘编号，高位在前（便于改编号时候app获得信息）
 * 12-17	KEYSN	6字节键盘SN
 */

public class QueryOnlineStateResponse extends StateBaseCmd {

    public static final int CMD_LENGTH = 17 ;

    public QueryOnlineStateResponse(){}

    public QueryOnlineStateResponse(QueryOnlineStateRequest request){
        parseCmd(request.toBytes());
        setCmd(RESPONSE_CMD);
    }
    /**
     * REQUEST_CMD	0xF0 状态类指令应答
     */
    private byte cmd = RESPONSE_CMD;

    /**
     * 命令类型
     * 7 返回当前在线状态
     */
    private byte cmd1 = 0x07 ;

    /**
     * ONLINE	键盘是否在线
     * 1 在线
     * 2 离线
     */
    private byte online;

    /**
     * IDMODE	基站的识别模式
     * 定义同信标变化通知里面的，APP可以不处理此信息
     */
    private byte idmode;

    /**
     * 当前频点号
     */
    private byte chan;

    /**
     * 接收到基站的信号强度RSSI值，负数，越小表示信号越大
     */
    private byte rssi;

    /**
     * TX	1表示刚才1秒内有提交数据过
     * 0 表示没有
     */
    private byte tx;

    /**
     * RX	1 表示刚才1秒内收到过基站的投票指令（特指投票中）
     * 0 表示没有
     */
    private byte rx;

    /**
     * BaseID	基站编号
     */
    private byte baseID;

    /**
     * KEYID	当前键盘编号，高位在前（便于改编号时候app获得信息）
     */
    private byte[] keyId = new byte[2];

    /**
     * KEYSN	6字节键盘SN
     */
    private byte[] keySn = new byte[6];

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

    public byte getOnline() {
        return online;
    }

    public void setOnline(byte online) {
        this.online = online;
    }

    public byte getIdmode() {
        return idmode;
    }

    public void setIdmode(byte idmode) {
        this.idmode = idmode;
    }

    public byte getChan() {
        return chan;
    }

    public void setChan(byte chan) {
        this.chan = chan;
    }

    public byte getRssi() {
        return rssi;
    }

    public void setRssi(byte rssi) {
        this.rssi = rssi;
    }

    public byte getTx() {
        return tx;
    }

    public void setTx(byte tx) {
        this.tx = tx;
    }

    public byte getRx() {
        return rx;
    }

    public void setRx(byte rx) {
        this.rx = rx;
    }

    public byte getBaseID() {
        return baseID;
    }

    public void setBaseID(byte baseID) {
        this.baseID = baseID;
    }

    public byte[] getKeyId() {
        return keyId;
    }

    public void setKeyId(byte[] keyId) {
        this.keyId = keyId;
    }

    public byte[] getKeySn() {
        return keySn;
    }

    public void setKeySn(byte[] keySn) {
        this.keySn = keySn;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];
        result[0] = cmd;
        result[1] = cmd1;
        result[2] = online;
        result[3] = idmode;
        result[4] = chan ;
        result[5] = rssi;
        result[6] = tx;
        result[7] = rx ;
        result[8] = baseID ;
        for(int i =0 ; i < 2;i++) {
            result[9+i] = keyId[i];
        }
        for(int i=0;i<6;i++){
            result[11+i] = keySn[i];
        }

        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {

        if(source != null && source.length > CMD_LENGTH + start){
            cmd = source[start + 0];
            cmd1 = source[start + 1];
            online = source[start + 2];
            idmode = source[start + 3];
            chan = source[start + 4] ;
            rssi = source[start + 5];
            tx = source[start + 6];
            rx  = source[start +7];
            baseID = source[start + 8];
            for(int i =0 ; i < 2;i++) {
                keyId[i] = source[start + 9+i];
            }
            for(int i=0;i<6;i++){
                keySn[i] = source[start + 11 + i];
            }
        }

        return this;
    }
}
