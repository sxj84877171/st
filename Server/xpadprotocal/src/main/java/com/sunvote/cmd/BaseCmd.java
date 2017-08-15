package com.sunvote.cmd;

import com.sunvote.cmd.push.BaseStatusChangeRequest;
import com.sunvote.cmd.push.BaseStatusChangeResponse;
import com.sunvote.cmd.push.PushBaseCmd;
import com.sunvote.cmd.state.StateBaseCmd;
import com.sunvote.cmd.upgrade.UpgradeBaseCmd;
import com.sunvote.cmd.upload.UploadBaseCmd;

/**
 * Created by Elvis on 2017/8/11.
 * Email:Eluis@psunsky.com
 * Description:
 */

public abstract class BaseCmd implements ICmd {

    @Override
    public ICmd parseCmd(byte[] source) {
        return parseCmd(source,0);
    }

    public static ICmd parseRequest(byte[] datas,int length){
        if(length > 5){

            byte[] ds = new byte[length - 4];
            for(int i = 0 ; i < length - 4; i ++){
                ds[i] = datas[i+4];
            }
            switch (ds[0]) {
                case StateBaseCmd.REQUEST_CMD:
                    return StateBaseCmd.parseRequest(datas, length);
                case BaseStatusChangeRequest.REQUEST_CMD:
                    return PushBaseCmd.parseRequest(datas,length);
                case UpgradeBaseCmd.REQUEST_CMD:
                    return UpgradeBaseCmd.parseRequest(datas,length);
                case UploadBaseCmd.REQUEST_CMD:
                    return UploadBaseCmd.parseRequest(datas,length);
                default:
                    return null;
            }
        }
        return null;
    }

    public static ICmd parseResponse(byte[] datas,int length){
        if(length > 5){
            byte[] ds = new byte[length - 4];
            for(int i = 0 ; i < length - 4; i ++){
                ds[i] = datas[i+4];
            }
            switch (ds[0]) {
                case StateBaseCmd.RESPONSE_CMD:
                    return StateBaseCmd.parseResponse(datas, length-4);
                case BaseStatusChangeResponse.RESPONSE_CMD:
                    return PushBaseCmd.parseResponse(datas,length-4);
                case UpgradeBaseCmd.RESPONSE_CMD:
                    return UpgradeBaseCmd.parseResponse(datas,length-4);
                case UploadBaseCmd.RESPONSE_CMD:
                    return UploadBaseCmd.parseResponse(datas,length-4);
                default:
                    return null;
            }
        }
        return null;
    }

    public static ICmd parse(byte[] datas,int length){
        if(length > 5){
            byte[] ds = new byte[length - 4];
            for(int i = 0 ; i < length - 4; i ++){
                ds[i] = datas[i+4];
            }
            switch (ds[0]) {
                case StateBaseCmd.REQUEST_CMD:
                    return StateBaseCmd.parseRequest(ds, length);
                case BaseStatusChangeRequest.REQUEST_CMD:
                    return PushBaseCmd.parseRequest(ds,length);
                case UpgradeBaseCmd.REQUEST_CMD:
                    return UpgradeBaseCmd.parseRequest(ds,length);
                case UploadBaseCmd.REQUEST_CMD:
                    return UploadBaseCmd.parseRequest(ds,length);
                case StateBaseCmd.RESPONSE_CMD:
                    return StateBaseCmd.parseResponse(ds, length-4);
                case BaseStatusChangeResponse.RESPONSE_CMD:
                    return PushBaseCmd.parseResponse(ds,length-4);
                case UpgradeBaseCmd.RESPONSE_CMD:
                    return UpgradeBaseCmd.parseResponse(ds,length-4);
                case UploadBaseCmd.RESPONSE_CMD:
                    return UploadBaseCmd.parseResponse(ds,length-4);
                default:
                    return null;
            }
        }
        return null;
    }
}
