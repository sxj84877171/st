package com.sunvote.cmd.push;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/9.
 * Email:Eluis@psunsky.com
 * Description:
 * 自定义命令控制
 * 涉及到一些新的需求，例如wifi控制、议案浏览控制、平板用户权限控制等，原老协议没有支持，在此定义，具体数据格式和意义由软件团队定义。
 * 模块提交给SDK：（即主控电脑发给平板）
 * <p>
 * 字节	标识符	描述
 * 1	KEYCMD	0x30 表决器下载单包类指令
 * 2-3	KEYID	表决器编号，或0x0000广播
 * 4	KCMD	十进制50到79，定义为自定义命令控制的命令类别
 *              例如，51可以定义为wifi控制类
 * 5-24	INFO	参数序列，最多20字节，格式由软件团队定义
 * 建议第一字节是小类别
 * @deprecated
 */

public class CustomCommandControl extends PushBaseCmd{

    public static final byte CMD = 0x30 ;

    /**
     * KEYCMD	0x30 表决器下载单包类指令
     */
    private byte keyCmd = CMD;

    /**
     * 表决器编号，或0x0000广播
     */
    private byte[] keyId ;

    /**
     * 十进制50到79，定义为自定义命令控制的命令类别
     *              例如，51可以定义为wifi控制类
     */
    private byte kCmd ;

    /**
     * 5-24	INFO
     * 参数序列，最多20字节，格式由软件团队定义
     */
    private  byte[] info;

    public byte getKeyCmd() {
        return keyCmd;
    }

    public void setKeyCmd(byte keyCmd) {
        this.keyCmd = keyCmd;
    }

    public byte[] getKeyId() {
        return keyId;
    }

    public void setKeyId(byte[] keyId) {
        this.keyId = keyId;
    }

    public byte getkCmd() {
        return kCmd;
    }

    public void setkCmd(byte kCmd) {
        this.kCmd = kCmd;
    }

    public byte[] getInfo() {
        return info;
    }

    public void setInfo(byte[] info) {
        this.info = info;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        return null;
    }
}
