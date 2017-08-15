package com.sunvote.udptransfer.work;

import com.sunvote.cmd.BaseCmd;
import com.sunvote.cmd.ICmd;
import com.sunvote.cmd.push.BaseStatusChangeRequest;
import com.sunvote.cmd.push.BaseStatusChangeResponse;
import com.sunvote.cmd.push.DownloadSingletonPkg;
import com.sunvote.cmd.push.VoteStatusChangeRequest;
import com.sunvote.cmd.push.VoteStatusChangeResponse;
import com.sunvote.cmd.state.KeyboardParameterStateRequest;
import com.sunvote.cmd.state.KeyboardParameterStateResponse;
import com.sunvote.cmd.state.ModeOperationStateRequest;
import com.sunvote.cmd.state.ModeOperationStateResponse;
import com.sunvote.cmd.state.QueryBeaconStateRequest;
import com.sunvote.cmd.state.QueryBeaconStateResponse;
import com.sunvote.cmd.state.QueryOnlineStateRequest;
import com.sunvote.cmd.state.QueryOnlineStateResponse;
import com.sunvote.cmd.state.WorkPattenStateRequest;
import com.sunvote.cmd.state.WorkPattenStateResponse;
import com.sunvote.cmd.upgrade.CleanFlashStateRequest;
import com.sunvote.cmd.upgrade.CleanFlashStateResponse;
import com.sunvote.cmd.upgrade.UpgradEntryStateRequest;
import com.sunvote.cmd.upgrade.UpgradEntryStateResponse;
import com.sunvote.cmd.upgrade.UpgradeExitStateRequest;
import com.sunvote.cmd.upgrade.UpgradeExitStateResponse;
import com.sunvote.cmd.upgrade.WriteFlashStateRequest;
import com.sunvote.cmd.upgrade.WriteFlashStateResponse;
import com.sunvote.cmd.upload.NumberingModeResult;
import com.sunvote.cmd.upload.SequenceFormatResult;
import com.sunvote.cmd.upload.TransferResult;
import com.sunvote.protocal.Protocal;
import com.sunvote.udptransfer.UDPModule;
import com.sunvote.udptransfer.core.LocalUDPDataSender;
import com.sunvote.udptransfer.utils.LogUtil;

/**
 * Created by Elvis on 2017/8/14.
 * Email:Eluis@psunsky.com
 * Description:
 *
 * 业务类
 * 所有的业务在此类完成
 */

public class ProtocalFactory {

    public final static long DELAY = 5 * 1000 ;

    public static WorkThread.MessageBean execute(byte[] bytes,int length){
        WorkThread.MessageBean bean = new WorkThread.MessageBean();
        bean.datas = bytes;
        boolean isHeaderRight = Protocal.checkHeader(bytes);
        if(isHeaderRight) {
            ICmd cmd = BaseCmd.parse(bytes, length);
            Protocal<ICmd> protocal = new Protocal<ICmd>();
            protocal.setCmd(cmd);
            bean.protocal = protocal;
            bean.executeMethod = getExecuteMethod(cmd);
        }
        return bean;
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(ICmd cmd){
        if(cmd instanceof BaseStatusChangeRequest){
            return getExecuteMethod((BaseStatusChangeRequest)cmd);
        }

        if(cmd instanceof BaseStatusChangeResponse){
            return getExecuteMethod((BaseStatusChangeResponse)cmd);
        }

        if(cmd instanceof DownloadSingletonPkg){
            return getExecuteMethod((DownloadSingletonPkg)cmd);
        }

        if(cmd instanceof VoteStatusChangeRequest){
            return getExecuteMethod((VoteStatusChangeRequest)cmd);
        }

        if(cmd instanceof VoteStatusChangeResponse){
            return getExecuteMethod((VoteStatusChangeResponse)cmd);
        }

        if(cmd instanceof KeyboardParameterStateRequest){
            return getExecuteMethod((KeyboardParameterStateRequest)cmd);
        }

        if(cmd instanceof KeyboardParameterStateResponse){
            return getExecuteMethod((KeyboardParameterStateResponse)cmd);
        }

        if(cmd instanceof ModeOperationStateRequest){
            return getExecuteMethod((ModeOperationStateRequest)cmd);
        }

        if(cmd instanceof ModeOperationStateResponse){
            return getExecuteMethod((ModeOperationStateResponse)cmd);
        }

        if(cmd instanceof QueryOnlineStateRequest){
            return getExecuteMethod((QueryOnlineStateRequest)cmd);
        }

        if(cmd instanceof QueryOnlineStateResponse){
            return getExecuteMethod((QueryOnlineStateResponse)cmd);
        }

        if(cmd instanceof WorkPattenStateRequest){
            return getExecuteMethod((WorkPattenStateRequest)cmd);
        }

        if(cmd instanceof WorkPattenStateResponse){
            return getExecuteMethod((WorkPattenStateResponse)cmd);
        }

        if(cmd instanceof  CleanFlashStateRequest){
            return getExecuteMethod((CleanFlashStateRequest)cmd);
        }

        if(cmd instanceof CleanFlashStateResponse){
            return getExecuteMethod((CleanFlashStateResponse)cmd);
        }

        if(cmd instanceof UpgradeExitStateRequest){
            return getExecuteMethod((UpgradeExitStateRequest)cmd);
        }

        if(cmd instanceof UpgradeExitStateResponse){
            return getExecuteMethod((UpgradeExitStateResponse)cmd);
        }

        if(cmd instanceof UpgradEntryStateRequest){
            return getExecuteMethod((UpgradEntryStateRequest)cmd);
        }

        if(cmd instanceof UpgradEntryStateResponse){
            return getExecuteMethod((UpgradEntryStateResponse)cmd);
        }

        if(cmd instanceof WriteFlashStateRequest){
            return getExecuteMethod((WriteFlashStateRequest)cmd);
        }

        if(cmd instanceof WriteFlashStateResponse){
            return getExecuteMethod((WriteFlashStateResponse)cmd);
        }

        if(cmd instanceof NumberingModeResult){
            return getExecuteMethod((NumberingModeResult)cmd);
        }

        if(cmd instanceof SequenceFormatResult){
            return getExecuteMethod((SequenceFormatResult)cmd);
        }

        if(cmd instanceof TransferResult){
            return getExecuteMethod((TransferResult)cmd);
        }

        if(cmd instanceof QueryBeaconStateRequest){
            return getExecuteMethod((QueryBeaconStateRequest)cmd);
        }

        return null;
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final QueryBeaconStateRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"QueryBeaconStateRequest",messageBean.datas);
                QueryBeaconStateResponse queryBeaconStateResponse = new QueryBeaconStateResponse(cmd);
                byte[] datas = queryBeaconStateResponse.toBytes();
                SDKProcessWork.getInstance().postData(datas,datas.length);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final BaseStatusChangeRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"BaseStatusChangeRequest",messageBean.datas);
                BaseStationProcessWork.getInstance().setBaddh("" + cmd.getBaddh());
                BaseStatusChangeResponse baseStatusChangeResponse = new BaseStatusChangeResponse(cmd);
                byte[] datas = baseStatusChangeResponse.toBytes();
                BaseStationProcessWork.getInstance().postData(datas);
                messageBean.callback = new WorkThread.Callback() {
                    @Override
                    public void onMsgHasSend(WorkThread.MessageBean messageBean) {
                        //do nothing.
                    }
                };
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final BaseStatusChangeResponse cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"BaseStatusChangeResponse",messageBean.datas);
                //do nothiing.
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final DownloadSingletonPkg cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"DownloadSingletonPkg",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final VoteStatusChangeRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"VoteStatusChangeRequest",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final VoteStatusChangeResponse cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"VoteStatusChangeResponse",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final KeyboardParameterStateRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"KeyboardParameterStateRequest",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final KeyboardParameterStateResponse cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"KeyboardParameterStateResponse",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final ModeOperationStateRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"ModeOperationStateRequest",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final ModeOperationStateResponse cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"ModeOperationStateResponse",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final QueryOnlineStateRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"QueryOnlineStateRequest",messageBean.datas);
                QueryOnlineStateResponse queryOnlineStateResponse = new QueryOnlineStateResponse();
                queryOnlineStateResponse.setCmd1((byte)0x07);
                queryOnlineStateResponse.setOnline(BaseStationProcessWork.getInstance().getOnline());
                queryOnlineStateResponse.setBaseID(BaseStationProcessWork.getInstance().getBaseID());
                queryOnlineStateResponse.setKeyId(new byte[]{0x00,0x01});
                queryOnlineStateResponse.setKeySn(new byte[]{0x00,0x01,0x02,0x03,0x04,0x05});
                Protocal<QueryOnlineStateResponse> protocal = new Protocal<>();
                protocal.setCmd(queryOnlineStateResponse);
                byte[] datas = protocal.toBytes();
                SDKProcessWork.getInstance().postData(datas,datas.length);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final QueryOnlineStateResponse cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"QueryOnlineStateResponse",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final WorkPattenStateRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"WorkPattenStateRequest",messageBean.datas);
                WorkPattenStateResponse workPattenStateResponse = new WorkPattenStateResponse();
                workPattenStateResponse.setCmd((byte)0xF0);
                workPattenStateResponse.setCmd1(cmd.getCmd1());
                workPattenStateResponse.setMode((byte)0x02);////1 基站模式 2 键盘模式
                workPattenStateResponse.setHmodel((byte)0xA1);
                workPattenStateResponse.setSver(new byte[]{0x00,0x01,0x00});
                byte[] datas = workPattenStateResponse.toBytes();
                SDKProcessWork.getInstance().postData(datas,datas.length);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final WorkPattenStateResponse cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"WorkPattenStateResponse",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final CleanFlashStateRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"CleanFlashStateRequest",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final CleanFlashStateResponse cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"CleanFlashStateResponse",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final UpgradeExitStateRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"UpgradeExitStateRequest",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final UpgradeExitStateResponse cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"UpgradeExitStateResponse",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final UpgradEntryStateRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"UpgradEntryStateRequest",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final UpgradEntryStateResponse cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"UpgradEntryStateResponse",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final WriteFlashStateRequest cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"WriteFlashStateRequest",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final WriteFlashStateResponse cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"WriteFlashStateResponse",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final NumberingModeResult cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"NumberingModeResult",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final SequenceFormatResult cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"SequenceFormatResult",messageBean.datas);
            }
        };
    }

    public static WorkThread.ExecuteMethod getExecuteMethod(final TransferResult cmd){
        return new WorkThread.ExecuteMethod() {
            @Override
            public void execute(WorkThread.MessageBean messageBean) {
                LogUtil.i(UDPModule.TAG,"TransferResult",messageBean.datas);
            }
        };
    }
}
