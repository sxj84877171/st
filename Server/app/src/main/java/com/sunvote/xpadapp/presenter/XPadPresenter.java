package com.sunvote.xpadapp.presenter;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadcomm.ComListener;
import com.sunvote.xpadcomm.XPadApi;
import com.sunvote.xpadcomm.XPadApiInterface.BaseInfo;
import com.sunvote.xpadcomm.XPadApiInterface.CmdDataInfo;
import com.sunvote.xpadcomm.XPadApiInterface.KeypadInfo;
import com.sunvote.xpadcomm.XPadApiInterface.ModelInfo;
import com.sunvote.xpadcomm.XPadApiInterface.OnLineInfo;
import com.sunvote.xpadcomm.XPadApiInterface.VoteInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class XPadPresenter implements ComListener {
	private String TAG = "XPadPresenter";
	private ComListener cl;
	private XPadApi XPad;
	private int batteryLevel=0;

	public XPadPresenter(Context context) {
		cl = (ComListener) context;
		XPad = XPadApi.getInstance();
		XPad.setComListener(this);
		checkOnlineThread.start();

		monitorBatteryState();
	}

	public void getWorkMode() {
		XPad.getWorkMode();
	}

	public void setWorkMode(int iMode) {
		XPad.setWorkMode(iMode);
	}

	public void getBaseStatus() {
		Log.d(TAG, "getBaseStatus------");
		XPad.getBaseStatus();
	}

	public void getVoteStatus() {
		Log.d(TAG, "getVoteStatus-------");
		XPad.getVoteStatus();
	}

	public void getKeypadParam() {
		XPad.getKeypadParam();
	}

	public void setKeypadParam(int keyId, byte[] KEYSN) {
		XPad.setKeypadParam(keyId, KEYSN);
	}

	public void checkOnLine(int volt, int keyinStatus) {
		XPad.checkOnLine(volt, keyinStatus);
	}

	public void submitVote(int ansType, String value) {
		XPad.submitVote(ansType, value);
	}

	public void submitVoteAllOK() {
		XPad.submitVoteAllOK();
	}
	
	public void cancelSubmitVoteAllOK(){
		XPad.cancelSubmitVoteAllOK();
	}

	private long keypadMatchTIme=0;
	public void execKeypadMatch(int iMode, int channel) {
		keypadMatchTIme = System.currentTimeMillis();
		XPad.execKeypadMatch(iMode, channel);
	}

	public void configMode() {
		XPad.configMode();
	}
	
	public void startFirmUpdate(byte[] fileBuffer){
		XPad.startFirmUpdate(fileBuffer);
	}

	private long lastRecvHeartBeatTime= System.currentTimeMillis();
	private Thread checkOnlineThread = new Thread("heartbit") {
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
					long curr = System.currentTimeMillis();
					if(curr - keypadMatchTIme < 3000){
						Log.d(TAG, "keypad match abort check online...");
						continue;
					}
					XPad.checkOnLine(batteryLevel, 0);
					long diff = (curr - lastRecvHeartBeatTime)/1000;
					if(diff >= 10*1000){
						Log.d(TAG, "lastRecvHeartBeatTime before(ms):"+diff);
						OnLineInfo ol= new OnLineInfo();
						ol.baseId = 0;
						ol.chan = 0;
						ol.keyId = 0;
						ol.onLine = 0;
						ol.rssi = 100;
						ol.rx =0;
						ol.tx =0;
						ol.comError = 1;
						cl.onOnLineEvent(ol);
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	};

	private final int REPEAT_INTERVAL = 500;

	@Override
	public void onComData(byte[] data, int len) {
		// Log.d(TAG, XPad.getDataBufString(data,len,"Recv"));
	}

	@Override
	public void onSendData(byte[] data, int len) {
		// Log.d(TAG, XPad.getDataBufString(data,len,"Send"));
	}

	@Override
	public void onModelEvent(ModelInfo info) {
		cl.onModelEvent(info);
	}

	private long lastBaseEventTime = 0;

	@Override
	public void onBaseEvent(BaseInfo info) {

		if (System.currentTimeMillis() - lastBaseEventTime < REPEAT_INTERVAL) {
			Log.d(TAG, "ignore onBaseEvent");
			return;
		}
		lastBaseEventTime = System.currentTimeMillis();
		cl.onBaseEvent(info);
	}

	private long lastVoteEventTime = 0;

	@Override
	public void onVoteEvent(VoteInfo info) {
		if (System.currentTimeMillis() - lastVoteEventTime < REPEAT_INTERVAL) {
			Log.d(TAG, "repeat onVoteEvent");
			//return;
		}
		lastVoteEventTime = System.currentTimeMillis();
		cl.onVoteEvent(info);
	}

	@Override
	public void onVoteSubmitSuccess() {
		cl.onVoteSubmitSuccess();

	}

	@Override
	public void onKeyPadEvent(KeypadInfo info) {
		cl.onKeyPadEvent(info);

	}

	@Override
	public void onOnLineEvent(OnLineInfo info) {
		lastRecvHeartBeatTime = System.currentTimeMillis();
		cl.onOnLineEvent(info);

	}

	@Override
	public void onCmdData(CmdDataInfo info) {
		cl.onCmdData(info);

	}

	@Override
	public void onMultiPackageData(byte[] data, int len) {
		cl.onMultiPackageData(data, len);

	}

	@Override
	public void onVoteSubmitAllOkSuccess() {
		cl.onVoteSubmitAllOkSuccess();
	}

	private IntentFilter batteryLevelFilter;

	private void monitorBatteryState() {
		MainActivity mact = (MainActivity) cl;
		mact.batteryLevelRcvr = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
					int rawlevel = intent.getIntExtra("level", -1);
					int scale = intent.getIntExtra("scale", -1);
					int status = intent.getIntExtra("status", -1);
					int health = intent.getIntExtra("health", -1);
					int voltage = intent.getIntExtra("voltage", 0);
					int level = -1; // percentage, or -1 for unknown
					if (rawlevel >= 0 && scale > 0) {
						level = (rawlevel * 100) / scale;
					}

					float val = (float) voltage / 1000;
					batteryLevel = (int)(val/0.04);

				}
			}
		};
		batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		mact.registerReceiver(mact.batteryLevelRcvr, batteryLevelFilter);
	}

	@Override
	public void onVoteSubmitError() {
		cl.onVoteSubmitError();
	}

	@Override
	public void onFirmUpdate(int percent) {
		cl.onFirmUpdate(percent);
	}

	@Override
	public void onFirmUpdateResult(boolean success, String msg) {
		cl.onFirmUpdateResult(success, msg);
	}

	@Override
	public void onFirmUpdateInfo(String info) {
		cl.onFirmUpdateInfo(info);
	}


}
