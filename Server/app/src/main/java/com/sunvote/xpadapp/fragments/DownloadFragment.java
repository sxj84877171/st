package com.sunvote.xpadapp.fragments;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.utils.WifiConnector;
import com.sunvote.xpadapp.utils.WifiConnector.WifiCipherType;
import com.sunvote.xpadcomm.FileRecver;
import com.sunvote.xpadcomm.FileRecver.FileReciverInterface;
import com.sunvote.xpadcomm.XPadApiInterface.OnLineInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class DownloadFragment extends BaseFragment implements FileReciverInterface {
	private final String TAG = "DownloadFragment";
	private WifiManager wifiManager;
	private WifiConnector wac;

	private String serverIp = "127.0.0.1";
	private int serverPort = 15154;

	private OnLineInfo mOnlineInfo;

	private String ssid = "";
	private String pwd = "";
	private boolean isCennectWifi;
	private FileRecver fileReciver = null;
	private TextView tv;

	private final int msgConnectError = 1;
	private final int msgOnDownload = 2;
	private final int msgDownloadOver = 3;
	private final int msgDownloadDataError = 4;

	private NetworkConnectChangedReceiver wifiReciver = new NetworkConnectChangedReceiver();

	public void setInfo(String wifiSsid, String wifiPassword, String ip, int port) {
		if (wifiSsid != null) {
			ssid = wifiSsid;
		}
		if (wifiPassword != null) {
			pwd = wifiPassword;
		}
		if (ip != null) {
			serverIp = ip;
		}
		if (port != 0) {
			serverPort = port;
		}


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_download, container, false);

		tv = (TextView) view.findViewById(R.id.download_title);

		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// connectWifiThread.run();
				// connectWifi();
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		wac = new WifiConnector(wifiManager);

		if (wifiManager.isWifiEnabled()) {
			Log.d(TAG, "onActivityCreated  isWifiEnabled= true ");
			WifiInfo info = wifiManager.getConnectionInfo();
			String tmpSsid = info.getSSID();
			int netId = info.getNetworkId();
			Log.d(TAG, "connected:" + tmpSsid);
			if (info != null && !tmpSsid.equals(ssid)) {
				Log.d(TAG, "disconnect and removeNetwork!");
				wifiManager.disableNetwork(netId);
				wifiManager.disconnect();
				wifiManager.removeNetwork(netId);
			}

			disableWifi();
		} else {

		}

		setWifiListener();
		Log.d(TAG, "timer.schedule");
		timer.schedule(tmtsk, 200, 30000);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(wifiReciver);
		Log.d(TAG, "unregisterReceiver(wifiReciver)");
		super.onDestroy();

	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case msgConnectError:
				cancelTimer();
				disableWifi();
				tv.setText(getString(R.string.connect_server_fail));
				break;
			case msgOnDownload:
				Log.d(TAG, getString(R.string.downloading) + msg.arg1 + "%");
				tv.setText(getString(R.string.downloading) + msg.arg1 + "%");
				tv.invalidate();
				break;
			case msgDownloadOver:
				try {
					Log.d(TAG, "on msgDownloadOver");
					tv.setText(getString(R.string.download_over));
					cancelTimer();
					disableWifi();
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case msgDownloadDataError:
				tv.setText(getString(R.string.download_fail));
				break;
			}

			super.handleMessage(msg);
		};
	};

	private void setWifiListener() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		getActivity().registerReceiver(wifiReciver, intentFilter);

	}

	private Timer timer = new Timer(true);
	private TimerTask tmtsk = new TimerTask() {

		@Override
		public void run() {
			Log.i(TAG, "TimerTask........");
			if (isCennectWifi) {
				Log.d(TAG, "tmtsk  isCennectWifi:true,  timer cancel ");
				cancelTimer();
			} else {
				Log.d(TAG, "tmtsk  isCennectWifi:false");
				Log.d(TAG, "try connect wifi");

				if (wifiManager.isWifiEnabled()) {
					disableWifi();
				}

				wac.connect(ssid, pwd,
						pwd.equals("") ? WifiCipherType.WIFICIPHER_NOPASS : WifiCipherType.WIFICIPHER_WPA);
			}

		}
	};

	private void cancelTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		;
	}

	private void disableWifi() {
		try {

			WifiInfo info = wifiManager.getConnectionInfo();
			String tmpSsid = info.getSSID();
			int netId = info.getNetworkId();
			if (info != null) {
				Log.d(TAG, "disconnect & removeNetwork current!");
				wifiManager.disableNetwork(netId);
				wifiManager.disconnect();
				wifiManager.removeNetwork(netId);
			}

			Log.d(TAG, "remove all");
			isCennectWifi = false;
			List<WifiConfiguration> wlist = wifiManager.getConfiguredNetworks();
			if (wlist != null) {
				for (int i = 0; i < wlist.size(); i++) {
					WifiConfiguration wifiCfg = wlist.get(i);
					Log.d(TAG, "removeNetwork:" + wifiCfg.SSID);
					wifiManager.removeNetwork(wifiCfg.networkId);
				}
			}
			wifiManager.saveConfiguration();
			Log.d(TAG, "disableWifi");
			wac.disableWifi();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getMeetingFilesFromServer() {
		Log.d(TAG, "getMeetingFilesFromServer");
		fileReciver = new FileRecver(DownloadFragment.this, serverIp, serverPort);

		fileReciver.tryGetMeetingFiles(((MainActivity) getActivity()).mKeypadInfo.keyId);

	}

	public class NetworkConnectChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			ConnectivityManager connectMgr = (ConnectivityManager) getActivity().getSystemService("connectivity");
			NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (wifiNetInfo.isConnected()) {
				Log.i(TAG, "wifi connected");

				WifiInfo info = wifiManager.getConnectionInfo();
				String tmpSsid = info.getSSID();
				int netId = info.getNetworkId();
				Log.d(TAG, "connected:" + tmpSsid);

				isCennectWifi = true;
				cancelTimer();
				connectServer();
			} else {
				Log.d(TAG, "not connect");
			}
		}
	}

	private void connectServer() {
		tv.setText(getString(R.string.connect_server_ing));
		Log.d(TAG, getString(R.string.connect_server_ing));
		new Thread("socketThread") {
			public void run() {
				try {
					Thread.sleep(1000);
					getMeetingFilesFromServer();
				} catch (InterruptedException e) {
				}
			}
		}.start();
	}

	@Override
	public void onConnectServerError() {
		Message message = new Message();
		message.what = msgConnectError;
		mHandler.sendMessage(message);
	}

	private class DownloadInfo {
		public long percent;
	}

	@Override
	public void onDownload(long percent) {
		Message message = new Message();
		message.what = msgOnDownload;
		message.arg1 = (int) percent;
		mHandler.sendMessage(message);

	}

	@Override
	public void onDownloadSuccess() {
		Message message = new Message();
		message.what = msgDownloadOver;
		mHandler.sendMessage(message);
	}

	@Override
	public void stopDownload() {
		Log.i(TAG, "stopDownload...");
		cancelTimer();
		disableWifi();
	}

	@Override
	public void onDownloadDataError() {
		Log.i(TAG, "onDownloadDataError...");
		cancelTimer();
		disableWifi();

		Message message = new Message();
		message.what = msgDownloadDataError;
		mHandler.sendMessage(message);
	}

}
