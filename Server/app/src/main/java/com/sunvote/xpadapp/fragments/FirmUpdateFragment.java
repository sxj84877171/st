package com.sunvote.xpadapp.fragments;

import java.io.File;

import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.utils.FileUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FirmUpdateFragment extends BaseFragment {
	public TextView tvTitle;
	public TextView tvVer;
	TextView tvInfo;
	TextView tvVer2;
	public TextView txtLog;
	static String TAG = "FirmUpdateFragment";
	public String oldFirmVer;
	private boolean  success ;
	private Button btnRetry;
	private ScrollView scrView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_firm_update, container, false);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(success){
					mMainActivity.presenter.getBaseStatus();
				}
			}
		});

		tvTitle = (TextView) view.findViewById(R.id.firm_update_title);
		tvVer = (TextView) view.findViewById(R.id.firm_update_ver);
		tvInfo = (TextView) view.findViewById(R.id.firm_update_info);
		tvTitle.setText("固件升级");
		if(mMainActivity!=null && mMainActivity.mModelInfo!=null) {
			tvVer.setText(getString(R.string.firmware_ver) + ":" + mMainActivity.mModelInfo.sVer);
		}
		txtLog = (TextView) view.findViewById(R.id.firm_update_txt_log);
		scrView = (ScrollView)view.findViewById(R.id.firm_update_scrview);

		IntentFilter filter = new IntentFilter("MyReceiver_Action");

		getActivity().registerReceiver(mBroadcastReceiver, filter);
		success = false;
		firmUpdate();

		btnRetry = (Button)view.findViewById(R.id.firm_update_btn_retry);
		btnRetry.setVisibility(View.INVISIBLE);
		btnRetry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "onClick: retry");
				appendLog("重试");
				firmUpdate();
				btnRetry.setVisibility(View.INVISIBLE);
			}
		});


		return view;
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy: clear");
		clearUpdateFile();
		super.onDestroy();
	}

	public static File checkFirmFile() {
		String filePath = Environment.getExternalStorageDirectory().getPath() + "/sunvote/system/";
		File file = new File(filePath);
		if (!file.exists()) {
			Log.i(TAG, "checkFirmFile: dir not exists");
			return null;
		}
		File[] files = file.listFiles();

		if (files == null || files.length == 0) {
			Log.i(TAG, "checkFirmFile:bin not exists");
			return null;
		}
		return files[0];
	}

	private void firmUpdate() {

		File file = checkFirmFile();
		if(file==null){
			Toast.makeText(mMainActivity, "升级文件不存在", Toast.LENGTH_LONG).show();
		}
		if(!file.getName().endsWith(".bin")){
			tvInfo.setText("升级文件格式错误");
			Toast.makeText(mMainActivity, "升级文件格式错误", Toast.LENGTH_LONG).show();
			return;
		}
			

		tvInfo.setText("正在升级固件...");

		byte[] buf = FileUtil.getByteArrayFromFile(file);
		if (buf != null) {
			mMainActivity.presenter.startFirmUpdate(buf);
		}
	}

	private int Msg_onFirmUpdate = 1;
	private int Msg_onFirmUpdateResult = 2;

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				tvInfo.setText("正在更新固件" + msg.arg1 + "%");
				break;
			case 2:
				if (msg.arg1 == 1) {
					Log.i(TAG, "handleMessage: 固件更新成功");
					tvInfo.setText("固件更新成功");
					appendLog("固件更新成功");
					Toast.makeText(mMainActivity, "固件更新成功", Toast.LENGTH_LONG).show();

				} else {
					String errmsg = (String) msg.obj;
					tvInfo.setText(errmsg);
					appendLog("升级失败："+ errmsg);
					btnRetry.setVisibility(View.VISIBLE);

				}
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public static void clearUpdateFile(){
		Log.i(TAG, "clearUpdateFile: ");
		String filePath = Environment.getExternalStorageDirectory().getPath() + "/sunvote/system/";
		File file = new File(filePath);
		FileUtil.deleteFile(file);
		filePath = Environment.getExternalStorageDirectory().getPath() + "/sunvote/system.zip";
		file = new File(filePath);
		FileUtil.deleteFile(file);

	}

	public void onFirmUpdate(int percent) {

		Message message = new Message();
		message.what = 1;
		message.arg1 = percent;
		myHandler.sendMessage(message);

	}

	public void onFirmUpdateResult(boolean success, String msg) {
		this.success = success;
		Message message = new Message();
		message.what = 2;
		message.arg1 = success ? 1 : 0;
		message.obj = msg;
		myHandler.sendMessage(message);

	}

	public void onFirmUpdateInfo(String info){
		appendLog(info);
	}

	public void setFirmVer(String ver) {
		tvVer2.setText("更新后版本：" + ver);
	}
	
	private BroadcastReceiver  mBroadcastReceiver = new BroadcastReceiver(){ 
        @Override 
        public void onReceive(Context context, Intent intent) { 
            String action = intent.getAction(); 
            if(action.equals("com.xpad.firm")){ 
            	Log.d(TAG, "onReceive recv com.xpad.firm");
            	String version = intent.getExtras().getString("ver");
            	setFirmVer(version);
            } 
        } 
         
    };

	public void appendLog(final String str){

		txtLog.post( new Runnable() {
			public void run() {
				if(txtLog.getText().length()==0){
					if(txtLog.getText().length()>=3000){
						txtLog.setText("");
					}
					txtLog.setText(str);
					System.out.println("set text");
				}else{
					txtLog.append("\n"+str);
					System.out.println("append");
				}
				scrollToBottom();
			}
		});


	}

	private void scrollToBottom()
	{
		scrView.post(new Runnable()
		{
			public void run()
			{
				scrView.smoothScrollTo(0, txtLog.getBottom());
				scrView.refreshDrawableState();
			}
		});
	}

}
