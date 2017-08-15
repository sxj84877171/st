package com.sunvote.xpadcomm;

import com.sunvote.xpadcomm.XPadApiInterface.BaseInfo;
import com.sunvote.xpadcomm.XPadApiInterface.CmdDataInfo;
import com.sunvote.xpadcomm.XPadApiInterface.KeypadInfo;
import com.sunvote.xpadcomm.XPadApiInterface.ModelInfo;
import com.sunvote.xpadcomm.XPadApiInterface.OnLineInfo;
import com.sunvote.xpadcomm.XPadApiInterface.VoteInfo;

public interface ComListener {
	public void onComData(byte[] data,int len);
	public void onSendData(byte[] data, int len);
	
	 public void onModelEvent(ModelInfo info); 
	 public void onBaseEvent( BaseInfo info);
	 public void onVoteEvent(VoteInfo info);
	 public void onVoteSubmitSuccess();
	 public void onVoteSubmitError();
	 public void onVoteSubmitAllOkSuccess();
	 public void onKeyPadEvent(KeypadInfo info);
	 public void onOnLineEvent(OnLineInfo info);
	 public void onCmdData(CmdDataInfo info);
	 public void onMultiPackageData(byte[] data, int len);
	 
	 public void onFirmUpdate(int percent);
	 public void onFirmUpdateResult(boolean success , String msg);
	 public void onFirmUpdateInfo(String info);
	 
}
