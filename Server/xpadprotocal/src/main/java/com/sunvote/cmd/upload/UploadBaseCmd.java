package com.sunvote.cmd.upload;

import com.sunvote.cmd.BaseCmd;

/**
 * Created by Elvis on 2017/8/14.
 * Email:Eluis@psunsky.com
 * Description:
 */

public abstract class UploadBaseCmd extends BaseCmd {

    public static final byte REQUEST_CMD = 0x73 ;
    public static final byte RESPONSE_CMD = (byte) 0xF3;

    public static UploadBaseCmd parseRequest(byte[] bytes, int length){
        return null;
    }


    public static UploadBaseCmd parseResponse(byte[] bytes, int length){
        return null;
    }
}
