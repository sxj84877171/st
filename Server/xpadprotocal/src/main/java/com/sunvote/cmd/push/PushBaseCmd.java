package com.sunvote.cmd.push;

import com.sunvote.cmd.BaseCmd;

/**
 * Created by Elvis on 2017/8/14.
 * Email:Eluis@psunsky.com
 * Description:
 */

public abstract class PushBaseCmd extends BaseCmd {




    public static PushBaseCmd parseRequest(byte[] bytes, int length) {
        BaseStatusChangeRequest baseStatusChangeRequest = new BaseStatusChangeRequest();
        baseStatusChangeRequest.parseCmd(bytes);
        return baseStatusChangeRequest;

    }

    public static PushBaseCmd parseResponse(byte[] bytes, int length) {
        BaseStatusChangeResponse baseStatusChangeResponse = new BaseStatusChangeResponse();
        baseStatusChangeResponse.parseCmd(bytes,length);
        return baseStatusChangeResponse;
    }
}
