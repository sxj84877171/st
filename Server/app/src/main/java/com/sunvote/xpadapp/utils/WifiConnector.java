package com.sunvote.xpadapp.utils;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.*;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WifiConnector {
	private String TAG = "WifiConnector";
	Handler mHandler;
	WifiManager wifiManager;

	/**
	 * 向UI发送消息
	 *
	 * @param info
	 *            消息
	 */
	public void sendMsg(String info) {
		if (mHandler != null) {
			Message msg = new Message();
			msg.obj = info;
			mHandler.sendMessage(msg);// 向Handler发送消息
		} else {
			Log.e("wifi", info);
		}
	}

	// WIFICIPHER_WEP是WEP ，WIFICIPHER_WPA是WPA，WIFICIPHER_NOPASS没有密码
	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

	// 构造函数
	public WifiConnector(WifiManager wifiManager) {
		this.wifiManager = wifiManager;
	}

	// 提供一个外部接口，传入要连接的无线网
	public void connect(String ssid, String password, WifiCipherType type) {
		Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
		thread.start();
	}

	public void disableWifi() {
		if (wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		}
	}

	// 查看以前是否也配置过这个网络
	private WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
		if (existingConfigs == null) {
			return null;
		}
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		// nopass
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {

			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
			config.priority = 20000;
		}
		// wep
		if (Type == WifiCipherType.WIFICIPHER_WEP) {
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		}
		// wpa
		if (Type == WifiCipherType.WIFICIPHER_WPA) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// 此处需要修改否则不能自动重联
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;

		}
		return config;
	}

	// 打开wifi功能
	private boolean openWifi() {
		boolean bRet = true;
		Log.d(TAG, "open wifi");
		if (!wifiManager.isWifiEnabled()) {
			bRet = wifiManager.setWifiEnabled(true);
		}
		return bRet;
	}

	class ConnectRunnable implements Runnable {
		private String ssid;

		private String password;

		private WifiCipherType type;

		public ConnectRunnable(String ssid, String password, WifiCipherType type) {
			this.ssid = ssid;
			this.password = password;
			this.type = type;
		}

		@Override
		public void run() {
			try {
				// 打开wifi
				openWifi();
				sendMsg("opened");
				Thread.sleep(500);
				// 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
				// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
				while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
					try {
						// 为了避免程序一直while循环，让它睡个100毫秒检测……
						Thread.sleep(100);
					} catch (InterruptedException ie) {
					}
				}

				try {
					// 为了避免程序一直while循环，让它睡个100毫秒检测……
					Thread.sleep(500);
				} catch (InterruptedException ie) {
				}

				WifiConfiguration wifiConfig = createWifiInfo(ssid, password, type);
				//
				if (wifiConfig == null) {
					sendMsg("wifiConfig is null!");
					return;
				}

				List<WifiConfiguration> wlist = wifiManager.getConfiguredNetworks();
				if (wlist != null) {
					for (int i = 0; i < wlist.size(); i++) {
						WifiConfiguration wifiCfg = wlist.get(i);
						wifiManager.removeNetwork(wifiCfg.networkId);
					}
				}
				WifiConfiguration tempConfig = isExsits(ssid);

				if (tempConfig != null) {
					wifiManager.removeNetwork(tempConfig.networkId);
				}



				int netID = wifiManager.addNetwork(wifiConfig);
				if (netID == -1) {
					Log.d("ConnectRunnable", "addNetwork fail");
				}
				boolean enabled = wifiManager.enableNetwork(netID, true);
				// sendMsg("enableNetwork status enable=" + enabled);

				boolean connected = wifiManager.reconnect();
				if (connected) {
					// sendMsg("enableNetwork connected=" + connected);
					// sendMsg("连接成功!");
				}
			} catch (Exception e) {
				// TODO: handle exception

				// sendMsg(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static boolean isHexWepKey(String wepKey) {
		final int len = wepKey.length();

		// WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
		if (len != 10 && len != 26 && len != 58) {
			return false;
		}

		return isHex(wepKey);
	}

	private static boolean isHex(String key) {
		for (int i = key.length() - 1; i >= 0; i--) {
			final char c = key.charAt(i);
			if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
				return false;
			}
		}

		return true;
	}

	public boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}
}
