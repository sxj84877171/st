package com.sunvote.cmd.state;

import com.sunvote.cmd.BaseCmd;

/**
 * Created by Elvis on 2017/8/14.
 * Email:Eluis@psunsky.com
 * Description:
 */

public abstract class StateBaseCmd extends BaseCmd {

    public static final byte REQUEST_CMD = 0x70;
    public static final byte RESPONSE_CMD = (byte) 0xF0;

    public static StateBaseCmd parseRequest(byte[] bytes, int length) {

        switch (bytes[1]) {
            case 0x01:
            case 0x02:
                WorkPattenStateRequest workPattenStateRequest = new WorkPattenStateRequest();
                workPattenStateRequest.parseCmd(bytes);
                return workPattenStateRequest;
            case 0x03:
            case 0x04:
                QueryBeaconStateRequest queryBeaconStateRequest = new QueryBeaconStateRequest();
                queryBeaconStateRequest.parseCmd(bytes, length);
                return queryBeaconStateRequest;
            case 0x05:
            case 0x06:
                KeyboardParameterStateRequest keyboardParameterStateRequest = new KeyboardParameterStateRequest();
                keyboardParameterStateRequest.parseCmd(bytes);
                return keyboardParameterStateRequest;
            case 0x07:
                QueryOnlineStateRequest queryOnlineStateRequest = new QueryOnlineStateRequest();
                queryOnlineStateRequest.parseCmd(bytes);
                return queryOnlineStateRequest;
            case 0x08:
            case 0x09:
                ModeOperationStateRequest modeOperationStateRequest = new ModeOperationStateRequest();
                modeOperationStateRequest.parseCmd(bytes);
                return modeOperationStateRequest;
            default:
                return null;
        }
    }

    public static StateBaseCmd parseResponse(byte[] bytes, int length) {
        switch (bytes[1]) {
            case 0x01:
            case 0x02:
                WorkPattenStateResponse workPattenStateResponse = new WorkPattenStateResponse();
                workPattenStateResponse.parseCmd(bytes);
                return workPattenStateResponse;
            case 0x03:
            case 0x04:
                QueryBeaconStateResponse queryBeaconStateResponse = new QueryBeaconStateResponse();
                queryBeaconStateResponse.parseCmd(bytes, length);
                return queryBeaconStateResponse;
            case 0x05:
            case 0x06:
                KeyboardParameterStateResponse keyboardParameterStateResponse = new KeyboardParameterStateResponse();
                keyboardParameterStateResponse.parseCmd(bytes);
                return keyboardParameterStateResponse;
            case 0x07:
                QueryOnlineStateResponse queryOnlineStateResponse = new QueryOnlineStateResponse();
                queryOnlineStateResponse.parseCmd(bytes);
                return queryOnlineStateResponse;
            case 0x08:
            case 0x09:
                ModeOperationStateResponse modeOperationStateResponse = new ModeOperationStateResponse();
                modeOperationStateResponse.parseCmd(bytes);
                return modeOperationStateResponse;
            default:
                return null;
        }
    }
}
