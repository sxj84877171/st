package com.sunvote.cmd.state;

import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/14.
 * Email:Eluis@psunsky.com
 * Description:
 * <p>
 * 模块应答：
 * 字节	标识符	描述
 * 1	CMD	0xF0 状态类指令应答
 * 2	CMD1	命令类型
 * 3 返回当前基础信标
 * 4 返回当前投票信标
 * 3		从这里开始，内容同信标变化通知
 * 。。。。。
 */

public class QueryBeaconStateResponse extends StateBaseCmd {

    public QueryBeaconStateResponse() {
    }

    public QueryBeaconStateResponse(QueryBeaconStateRequest request) {
        parseCmd(request.toBytes());
        setCmd(RESPONSE_CMD);
    }

    private byte cmd = RESPONSE_CMD;

    /**
     * 命令类型
     * 3 返回当前基础信标
     * 4 返回当前投票信标
     */
    private byte cmd1;


    private byte[] datas ;


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

    public byte[] getDatas() {
        return datas;
    }

    public void setDatas(byte[] datas) {
        this.datas = datas;
    }

    @Override
    public byte[] toBytes() {
        int length = 2 ;
        if(datas != null){
            length += datas.length ;
        }
        byte[] result = new byte[length];
        result[0] = cmd;
        result[1] = cmd1;
        if(datas != null){
            for(int i=0;i<datas.length;i++){
                result[2+i] = datas[i];
            }
        }
        return result;
    }

    @Override
    public ICmd parseCmd(byte[] source, int start) {
        if(source != null && source.length >= start + 2){
            cmd = source[start];
            cmd1 = source[start+1];
            datas = new byte[source.length - 2];
            for(int i=0;i<source.length - 2;i++){
                datas[i] = source[i+2];
            }
        }
        return this;
    }
}
