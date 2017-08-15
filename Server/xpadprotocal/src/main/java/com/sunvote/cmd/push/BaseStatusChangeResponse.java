package com.sunvote.cmd.push;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description:
 * SDK应答表示收到：
 * 字节	标识符	描述
 1	REQUEST_CMD	0xF1
 2	BADDH	基站编号
 3-24		暂和原指令相同
 */

public class BaseStatusChangeResponse extends PushBaseCmd {

    public static final byte RESPONSE_CMD = (byte) 0xF1;

    public static final int CMD_LENGTH = 24 ;

    public BaseStatusChangeResponse(){

    }

    public BaseStatusChangeResponse(BaseStatusChangeRequest request){
        parseCmd(request.toBytes());
        setCmd(RESPONSE_CMD);
    }

    /**
     * 0x71 基础信标变化
     */
    private byte cmd = RESPONSE_CMD;

    /**
     * 基站编号
     */
    private byte baddh;

    /**
     * 基站识别模式
     * 1编号、2序列号
     * SDK要依据此模式来判断提交数据时候的打包格式
     */
    private byte idmode;

    /**
     * 2字节，会议资料UID编号，1-65535，高位字节在前
     * （对应系统使用后创见的会议文件的唯一编号）
     */
    private byte[] confid = new byte[2];

    /**
     * 议题编号或索引（1-255），对应到具体用户待机时候可以浏览的议案文件内容
     * 值=0，表示不浏览内容 ，值=255，表示自由浏览所有内容
     * 其他值，指定议案编号浏览
     */
    private byte voteid;

    /**
     * 授权号，2字节，高位在前，0-0xFFFF
     * =0时候不使用授权模式，表决器可以参与表决
     * >0时候，表决器保存的授权号相同才能参与表决
     */
    private byte[] authcode = new byte[2];

    /**
     * 登录申请模式（后台签到模式），是否需要IC卡、登录码（用户编号\
     */
    private byte login;

    /**
     * 表决器报告状态模式和指定语言
     */
    private byte reportLanguage;


    /**
     *自动关机时间
     */
    private byte offtime;

    /**
     * 表决器特性：背光模式+蜂鸣器模式等等
     */
    private byte attrib;

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getBaddh() {
        return baddh;
    }

    public void setBaddh(byte baddh) {
        this.baddh = baddh;
    }

    public byte getIdmode() {
        return idmode;
    }

    public void setIdmode(byte idmode) {
        this.idmode = idmode;
    }

    public byte[] getConfid() {
        return confid;
    }

    public void setConfid(byte[] confid) {
        this.confid = confid;
    }

    public byte getVoteid() {
        return voteid;
    }

    public void setVoteid(byte voteid) {
        this.voteid = voteid;
    }

    public byte[] getAuthcode() {
        return authcode;
    }

    public void setAuthcode(byte[] authcode) {
        this.authcode = authcode;
    }

    public byte getLogin() {
        return login;
    }

    public void setLogin(byte login) {
        this.login = login;
    }

    public byte getReportLanguage() {
        return reportLanguage;
    }

    public void setReportLanguage(byte reportLanguage) {
        this.reportLanguage = reportLanguage;
    }

    public byte getOfftime() {
        return offtime;
    }

    public void setOfftime(byte offtime) {
        this.offtime = offtime;
    }

    public byte getAttrib() {
        return attrib;
    }

    public void setAttrib(byte attrib) {
        this.attrib = attrib;
    }

    public byte[] getBasename() {
        return basename;
    }

    public void setBasename(byte[] basename) {
        this.basename = basename;
    }

    /**
     *基站名称，最多12字节
     */
    private byte[] basename = new byte[12];

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];

        result[0] = cmd;
        result[1] = baddh;
        result[2] = idmode;
        for(int i= 0 ;i < 2 ; i++) {
            result[3+i] = confid[i];
        }
        result[5] = voteid;

        for(int i= 0 ;i < 2 ; i++) {
            result[6+i] = authcode[i];
        }
        result[8] = login;
        result[9] = reportLanguage ;
        result[10] = offtime;
        result[11] = attrib;

        for(int i= 0 ; i < 12 && i < basename.length;i++){
            result[12+i] = basename[i];
        }

        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length >= start + CMD_LENGTH){
            cmd = source[start];
            baddh = source[start + 1];
            idmode = source[start + 2];
            for(int i= 0 ;i < 2 ; i++) {
                confid[i] = source[start + 3+i];
            }
            voteid = source[start + 5];

            for(int i= 0 ;i < 2 ; i++) {
                authcode[i] = source[start + 6 + i];
            }
            login = source[start + 8];
            reportLanguage = source[start + 9];
            offtime = source[start + 10];
            attrib = source[start + 11];

            for(int i= 0 ; i < 12 && i < basename.length;i++){
                basename[i] = source[start + 12 + i];
            }
        }
        return this;
    }
}
