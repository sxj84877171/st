package com.sunvote.cmd.upgrade;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.push.PushBaseCmd;

/**
 * Created by Elvis on 2017/8/14.
 * Email:Eluis@psunsky.com
 * Description:
 */

public abstract class UpgradeBaseCmd extends BaseCmd {

    public static final byte REQUEST_CMD = 0x78;
    public static final byte RESPONSE_CMD = (byte) 0xF8;

    public static UpgradeBaseCmd parseRequest(byte[] bytes, int length){

        switch (bytes[1]){
            case 0x01:
            case 0x02:
                UpgradEntryStateRequest upgradEntryStateRequest = new UpgradEntryStateRequest();
                upgradEntryStateRequest.parseCmd(bytes);
                return upgradEntryStateRequest;
            case 0x03:
                CleanFlashStateRequest cleanFlashStateRequest = new CleanFlashStateRequest();
                cleanFlashStateRequest.parseCmd(bytes);
                return cleanFlashStateRequest;
            case 0x04:
                WriteFlashStateRequest writeFlashStateRequest = new WriteFlashStateRequest();
                writeFlashStateRequest.parseCmd(bytes);
                return writeFlashStateRequest;
            case 0x05:
                UpgradeExitStateRequest upgradeExitStateRequest = new UpgradeExitStateRequest();
                upgradeExitStateRequest.parseCmd(bytes);
                return upgradeExitStateRequest;
        }
        return null;
    }


    public static UpgradeBaseCmd parseResponse(byte[] bytes, int length){

        switch (bytes[1]){
            case 0x01:
            case 0x02:
                UpgradEntryStateResponse upgradEntryStateResponse = new UpgradEntryStateResponse();
                upgradEntryStateResponse.parseCmd(bytes);
                return upgradEntryStateResponse;
            case 0x03:
                CleanFlashStateResponse cleanFlashStateResponse = new CleanFlashStateResponse();
                cleanFlashStateResponse.parseCmd(bytes);
                return cleanFlashStateResponse;
            case 0x04:
                WriteFlashStateResponse writeFlashStateResponse = new WriteFlashStateResponse();
                writeFlashStateResponse.parseCmd(bytes);
                return writeFlashStateResponse;
            case 0x05:
                UpgradeExitStateResponse upgradeExitStateResponse = new UpgradeExitStateResponse();
                upgradeExitStateResponse.parseCmd(bytes);
                return upgradeExitStateResponse;
        }
        return null;
    }
}
