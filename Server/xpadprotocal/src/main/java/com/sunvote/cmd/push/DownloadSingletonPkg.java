package com.sunvote.cmd.push;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/8.
 * Email:Eluis@psunsky.com
 * Description:下载单包
 * 表决器下载单包类指令主要用于控制表决器执行某种操作，例如修改编号、遥控关机等。
 * 前面已经讨论过，采用透传的模式，所以指令结构和表决器协议的下载单包一样，不再重新定义，具体参考《通讯协议-政务商务-表决器部分-V5.0.docx》
 * <p>
 * 发送给SDK的指令结构如下
 * <p>
 * 字节	标识符	描述
 * 1	KEYCMD	0x30 表决器管理类指令
 * 2-3	KEYID	表决器编号，2字节，高位在前
 * 0x0000时候是广播，所有表决器都接收和处理
 * 其他值是指定编号执行，编号和KEYID相同的才执行命令
 * 4	KCMD	执行命令的类型
 * 5-24	KCMDS	根据KCMD值不同，有不同含义
 */

public class DownloadSingletonPkg extends PushBaseCmd {

    public static final byte CMD = 0x30 ;

    public static final int CMD_LENGTH = 24 ;


    public DownloadSingletonPkg(){}

    /**
     * 表决器管理类指令
     */
    private byte keycmd = CMD;

    /**
     * 表决器编号，2字节，高位在前
     * 0x0000时候是广播，所有表决器都接收和处理
     * 其他值是指定编号执行，编号和KEYID相同的才执行命令
     */
    private byte[] keyid = new byte[2];

    /**
     * 执行命令的类型
     */
    private byte kcmd;

    /**
     * 根据KCMD值不同，有不同含义
     */
    private byte[] kcmds = new byte[20];

    public byte getKeycmd() {
        return keycmd;
    }

    public void setKeycmd(byte keycmd) {
        this.keycmd = keycmd;
    }

    public byte[] getKeyid() {
        return keyid;
    }

    public void setKeyid(byte[] keyid) {
        this.keyid = keyid;
    }

    public byte getKcmd() {
        return kcmd;
    }

    public void setKcmd(byte kcmd) {
        this.kcmd = kcmd;
    }

    public byte[] getKcmds() {
        return kcmds;
    }

    public void setKcmds(byte[] kcmds) {
        this.kcmds = kcmds;
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CMD_LENGTH];

        result[0] = keycmd;
        for(int i= 0 ;i < 2 ; i++) {
            result[1+i] = keyid[i];
        }
        result[3] = kcmd;

        for(int i= 0 ;i < 20 ; i++) {
            result[4+i] = kcmds[i];
        }

        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length >= start + CMD_LENGTH){
            keycmd = source[start+0];
            for(int i= 0 ;i < 2 ; i++) {
                keyid[i] = source[start + i];
            }
            kcmd = source[start+3];

            for(int i= 0 ;i < 20 ; i++) {
                kcmds[i] = source[start + 20];
            }
        }
        return this;
    }
}
