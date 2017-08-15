package com.sunvote.cmd;

/**
 * Created by Elvis on 2017/8/9.
 * Email:Eluis@psunsky.com
 * Description: 命令
 */

public interface ICmd {

    byte[] toBytes();

    ICmd parseCmd(byte[] source);

    ICmd parseCmd(byte[] source, int start);
    /**
     * 状态类指令;
     */
    byte CMD_STATE_REQUEST = 0x70;

    /**
     * 基础信标变化;
     */
    byte CMD_BASE_REQUEST = 0x71;

    /**
     * 0x72	投票信标变化
     */
    byte CMD_POLLING_BEACON_REQUEST = 0x72;

    /**
     * 0x73	上传结果
     */
    byte CMD_UPLOAD_RESULT_REQUEST = 0x73;

    /**
     * 0x78	固件在线升级
     */
    byte CMD_FIRMWARE_UPGRADE_REQUEST = 0x78;
    /**
     * 状态类指令;
     */
    byte CMD_STATE_RESPONSE = (byte) 0xF0;

    /**
     * 0xF1 基础信标变化反馈;
     */
    byte CMD_BASE_RESPONSE = (byte) 0xF1;

    /**
     * 0xF2	投票信标变化反馈
     */
    byte CMD_POLLING_BEACON_RESPONSE = (byte) 0xF2;

    /**
     * 0xF3	上传结果反馈
     */
    byte CMD_UPLOAD_RESULT_RESPONSE = (byte) 0xF3;

    /**
     * 0xF8	固件在线升级反馈
     */
    byte CMD_FIRMWARE_UPGRADE_RESPONSE = (byte) 0xF8;

    /**
     * 0x30 表决器管理类指令
     */
    byte CMD_VOTER_MANAGEMENT = 0x30 ;


}
