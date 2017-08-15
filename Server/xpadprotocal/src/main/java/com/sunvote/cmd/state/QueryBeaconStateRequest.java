package com.sunvote.cmd.state;

import com.sunvote.cmd.ICmd;

/**
 * Created by Elvis on 2017/8/14.
 * Email:Eluis@psunsky.com
 * Description:
 * 模块在信标变化时候自动报告，但考虑到APP不知什么时候上线或者存在奔溃离线的情况，所以还是提供查询信标的指令。
 * SDK提交给模块：
 * 字节	标识符	描述
 * 1	CMD	0x70 状态类指令
 * 2	CMD1	命令类型
 * 3 查询基础信标
 * 4 查询投票信标
 *
 */

public class QueryBeaconStateRequest extends StateBaseCmd {

    public QueryBeaconStateRequest(){}

    public QueryBeaconStateRequest(QueryBeaconStateResponse response){
        parseCmd(response.toBytes());
        setCmd(REQUEST_CMD);
    }

    private byte cmd = RESPONSE_CMD ;

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
