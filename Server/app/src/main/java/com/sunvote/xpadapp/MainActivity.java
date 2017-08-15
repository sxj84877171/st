package com.sunvote.xpadapp;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.sunvote.xpadapp.base.BaseActivity;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.db.DBManager;
import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadapp.db.modal.MeetingInfo;
import com.sunvote.xpadapp.db.modal.MultiTitleItem;
import com.sunvote.xpadapp.fragments.ComErrorFragment;
import com.sunvote.xpadapp.fragments.CommunicationTestFragment;
import com.sunvote.xpadapp.fragments.ContentVoteFragment;
import com.sunvote.xpadapp.fragments.DocumentBrowserFragment;
import com.sunvote.xpadapp.fragments.DownloadFragment;
import com.sunvote.xpadapp.fragments.ElectionFragment;
import com.sunvote.xpadapp.fragments.FirmUpdateFragment;
import com.sunvote.xpadapp.fragments.KeypadTestChoice10Fragment;
import com.sunvote.xpadapp.fragments.KeypadTestFragment;
import com.sunvote.xpadapp.fragments.MeetingWelcomeFragment;
import com.sunvote.xpadapp.fragments.MultiContentDetailFragment;
import com.sunvote.xpadapp.fragments.MultiContentFragment;
import com.sunvote.xpadapp.fragments.MultiPingshengFragment;
import com.sunvote.xpadapp.fragments.MultiTitleFragment;
import com.sunvote.xpadapp.fragments.NoFileFragment;
import com.sunvote.xpadapp.fragments.OfflineFragment;
import com.sunvote.xpadapp.fragments.OnLineFragment;
import com.sunvote.xpadapp.fragments.ResultMultiVoteFragment;
import com.sunvote.xpadapp.fragments.ResultVoteFragment;
import com.sunvote.xpadapp.fragments.ShowIdFragment;
import com.sunvote.xpadapp.fragments.SigninFragment;
import com.sunvote.xpadapp.fragments.SingleTitleFragment;
import com.sunvote.xpadapp.presenter.XPadPresenter;
import com.sunvote.xpadapp.utils.FileUtil;
import com.sunvote.xpadapp.utils.SharedPreferencesUtil;
import com.sunvote.xpadcomm.ComListener;
import com.sunvote.xpadcomm.FileRecver;
import com.sunvote.xpadcomm.ScreenUtil;
import com.sunvote.xpadcomm.XPadApi;
import com.sunvote.xpadcomm.XPadApiInterface.BaseInfo;
import com.sunvote.xpadcomm.XPadApiInterface.CmdDataInfo;
import com.sunvote.xpadcomm.XPadApiInterface.KeypadInfo;
import com.sunvote.xpadcomm.XPadApiInterface.ModelInfo;
import com.sunvote.xpadcomm.XPadApiInterface.OnLineInfo;
import com.sunvote.xpadcomm.XPadApiInterface.VoteInfo;
import com.sunvote.xpadcomm.XPadSystem;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements ComListener {

	private final int Msg_ComError = 0;
	private final int Msg_Offline = 1;
	private final int Msg_Online = 2;
	private final int Msg_Download = 3;
	private final int Msg_Welcome = 4;

//	private final int Msg_Signin = 5;
//	private final int Msg_QuickVote = 6;
//	private final int Msg_SingleTitleVote = 7;
//	private final int Msg_MultiTitleVote = 8;
//	private final int Msg_SingleContentVote = 9;
//	private final int Msg_MultiContentVote = 10;
//	private final int Msg_ElectionVote = 11;
	private final int Msg_StopDownload = 12;

//	private final int Msg_KeypadTest = 13;
	private final int Msg_CommunicationTest = 14;//100次成功率
	private final int Msg_ShowID = 15;
	private final int Msg_HideShowID = 17;

//	private final int Msg_MultiPingShengVote = 16;

	private final int Msg_onVoteEvent = 20;
	private final int Msg_onCommitSuccessEvent = 21;
	private final int Msg_onCommitAllOkSuccessEvent = 22;
	private final int Msg_onMultiPackageData = 23;//显示结果
	private final int Msg_HideVoteResult = 24;
	private final int Msg_ShowMultiVoteResult = 25;
	private final int Msg_DBfileNotExist = 30;

	private final int Msg_MatchInfo = 31;
	private final int Msg_CommunicationTestHideResult = 32;

	private final int Msg_onCommitErrorEvent = 33;

	private final int Msg_DocumentBroswer = 40;
	private final int Msg_ShowBill = 41;

	private final int Msg_ScreenDark = 50;
	private final int Msg_ChangeBright = 51;
//	private final int Msg_ChangeOffTime = 52;

	private final int Msg_FirmUpdate = 53;
	private final int Msg_HideFirmUpdate = 54;

	public BroadcastReceiver batteryLevelRcvr;

	// WifiManager wifiManager;
	// WifiConnector wac;
	private String TAG = "MainActivity";
	FragmentManager fm = getFragmentManager();
	private FileRecver cs = null;
	private String ip = "192.168.0.105";
	private int port = 15154;

	private BaseFragment currFragment;
	private DownloadFragment downloadFragment;
	private DocumentBrowserFragment docFragment;
	private Fragment keypadTestFragment;
	private Fragment showIdFragment;
	public Fragment resultFragment;
	private FirmUpdateFragment firmFragment;

	private String wifiSsid;
	private String wifiPwd;
	private String serverIp;
	private int serverPort;

	public XPadPresenter presenter;
	public OnLineInfo mOnlineInfo;
	public BaseInfo mBaseInfo;
	public KeypadInfo mKeypadInfo;
	public VoteInfo mVoteInfo;
	public ModelInfo mModelInfo;

	public String meetingDir;

	public MeetingInfo meetingInfo;
	public BillInfo currBillInfo;
	public BillInfo subBillInfo;
	public DBManager dbm;
	public int meetingId;
	public int roleType;//1 正式代表 , 0列席代表

	private boolean inKeyTesting = false;

	private long lastTouchTime;
	private Timer screenTimer;
	private long scrTimerCnt;
	private Integer brigntLevel;
	private Integer darkTime;// minit

	public long startVoteTime;// 启动投票时间 用于测试10选1 功能

	// private NetworkConnectChangedReceiver wifiReciver = new
	// NetworkConnectChangedReceiver();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Window window = getWindow();
		// WindowManager.LayoutParams params = window.getAttributes();
		// params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION ;
		// window.setAttributes(params);

		setContentView(R.layout.activity_main);

		mOnlineInfo = new OnLineInfo();
		mOnlineInfo.onLine = 0;

		presenter = new XPadPresenter(this);
		setOfflineFragment();


		darkTime = (Integer) SharedPreferencesUtil.getData(MainActivity.this, "darkTime", 3);
		brigntLevel = (Integer) SharedPreferencesUtil.getData(MainActivity.this, "bright", 50);
		ScreenUtil.setNormalMode(MainActivity.this, brigntLevel);
		screenTimer = new Timer(true);
		screenTimer.schedule(screenTask, 1000, 1000); // 延时1000ms后执行，1000ms执行一次
		
		clearApkFile();
		FirmUpdateFragment.clearUpdateFile();

	}

	@Override
	public void onAttachedToWindow() {
		// if(true) {
		// this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		// }
		super.onAttachedToWindow();
	}

	protected void hideBottomUIMenu() {
		// 隐藏虚拟按键，并且全屏
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower
																		// api
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else if (Build.VERSION.SDK_INT >= 19) {
			// for new api versions.
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}

	}

	@Override
	protected void onResume() {
		hideBottomUIMenu();
		XPadSystem.setNavgationGone(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onstop");
		XPadSystem.setNavgationVisible(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		XPadSystem.setNavgationVisible(this);
		if (batteryLevelRcvr != null) {
			unregisterReceiver(batteryLevelRcvr);
		}
	}

	Handler myHandler = new Handler() {
		@SuppressLint("ShowToast")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Msg_ComError:
				setComErrorFragment();
				break;
			case Msg_Offline:
				setOfflineFragment();
				break;
			case Msg_Online:
				setOnlineFragment();
				break;

			case Msg_Download:
				clearApkFile();
				FirmUpdateFragment.clearUpdateFile();
				setDownloadFragment();
				break;
			case Msg_StopDownload:
				if (downloadFragment != null) {
					downloadFragment.stopDownload();
					hideDownloadFragment();
				}

				if (dbm != null) {
					try {
						dbm.closeDB();
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				docFragment = null;
				File apkFile = checkUpdateApkFile();
				File firmFile = FirmUpdateFragment.checkFirmFile();
				String str = String.format("Msg_StopDownload apkFile:%s ,firmFile:%s",apkFile!=null?"Y":"N",firmFile!=null?"Y":"N");
				//Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();
				Log.d(TAG, "handleMessage: "+ str);

				if ( firmFile != null) {
					Log.i(TAG, "handleMessage: firm update");
					setFirmUpdateFragment();
				} else if (apkFile != null) {
					Log.i(TAG, "handleMessage: install apk");
					installApk(apkFile);
				} else {
					Log.i(TAG, "handleMessage: stop download and getBaseStatus");
					presenter.getBaseStatus();
				}
				// dbm.closeDB();
				// if (meetingInfo != null) {
				// setWelcomeFragment();
				// } else {
				// setOnlineFragment();
				// }
				break;
			case Msg_Welcome:
				setWelcomeFragment();
				break;
			case Msg_ShowBill:
				showBill(null);
				break;
			case Msg_DocumentBroswer:
				setDocumentBrowserFragment();
				break;

			case Msg_onVoteEvent:
				VoteInfo vote = (VoteInfo) msg.obj;
				doVote(vote);

				break;
			case Msg_onCommitSuccessEvent:
				if (currFragment != null) {
					currFragment.onVoteSubmitSuccess();
				}
				break;
			case Msg_onCommitErrorEvent:

				if (currFragment != null) {
					currFragment.onVoteSubmitError();
				}
				break;
			case Msg_onCommitAllOkSuccessEvent:
				if (currFragment != null) {
					currFragment.onVoteSubmitAllOkSuccess();
				}
				break;
			case Msg_onMultiPackageData:// 显示结果

				break;
			case Msg_ShowMultiVoteResult:

				showMultiVoteResultFragment((byte[]) msg.obj);
				break;
			case Msg_HideVoteResult:// 隐藏结果
				hideResultFragment();
				break;
			case Msg_DBfileNotExist:
				setNoFileFragment();
				break;

			case Msg_CommunicationTest:
				showCommucationFragment((byte[]) msg.obj);
				break;
			case Msg_ShowID:
				setShowIdFragment();
				break;
			case Msg_HideShowID:
				hideShowIdFragment();
				break;
			case Msg_MatchInfo:
				KeypadInfo info = (KeypadInfo) msg.obj;
				ShowMatchInfo(info);
				break;
			case Msg_CommunicationTestHideResult:
				dlg.dismiss();
				break;
			case Msg_ScreenDark:
				if (!App.isBackground(MainActivity.this)) {
					ScreenUtil.setDarkMode(MainActivity.this);
				}
				break;
			case Msg_ChangeBright:
				brigntLevel = msg.arg1;
				Log.d(TAG, "-----change bright percent :" + brigntLevel);
				ScreenUtil.setNormalMode(MainActivity.this, brigntLevel);
				break;
			case Msg_FirmUpdate:
				setFirmUpdateFragment();
				break;
			case Msg_HideFirmUpdate:
				hideFirmUpdateFragment();
				break;
			default:

			}

			super.handleMessage(msg);
		}
	};

	private void ShowMatchInfo(KeypadInfo info) {
		if (info.cmd1 == 8) { // 配对
			String strMsg = null;
			if (info.ok == 1) {
				strMsg = "配对成功，频点：" + info.chan + " 键盘编号：" + info.keyId;
			} else {
				strMsg = "配对失败";
			}
			Toast.makeText(this, strMsg, Toast.LENGTH_LONG).show();
		} else if (info.cmd1 == 9) {
			String strMsg = null;
			if (info.ok == 1) {
				strMsg = "配置成功，频点：" + info.chan + " 键盘编号：" + info.keyId;
			} else {
				strMsg = "配置失败";
			}
			Toast.makeText(this, strMsg, Toast.LENGTH_LONG).show();
		}
	}

	private void hideResultFragment() {
		if (resultFragment == null) {
			return;
		}
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		tx.remove(resultFragment);
		tx.commit();
		resultFragment = null;
	}

	private ProgressDialog dlg = null;
	private CommunicationTestFragment commFragment;

	private void showCommucationFragment(byte[] info) {
		if (dlg == null) {
			dlg = new ProgressDialog(MainActivity.this);
			dlg.setCancelable(true);// 设置是否可以通过点击Back键取消
			dlg.setCanceledOnTouchOutside(true);// 设置在点击Dialog外是否取消Dialog进度条
		}
		if (!dlg.isShowing()) {
			dlg.show();
		}
		dlg.setTitle("通讯测试");
		dlg.setMessage("通讯测试 次数:" + info[0] + "  成功次数:" + info[1] + "  基站信号:" + info[2] + "  键盘信号:" + info[3]);
		myHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Message message = new Message();
				message.what = Msg_CommunicationTestHideResult;
				myHandler.sendMessage(message);

			}
		}, 60000);

	}

	private void showVoteResultFragment(VoteInfo vote) {
		Log.e(TAG, "showVoteResultFragment");
		if (currBillInfo == null) {
			Log.e(TAG, "showVoteResultFragment   currBillInfo is null");

		}

		FragmentTransaction transaction = fm.beginTransaction();
		ResultVoteFragment fr = new ResultVoteFragment();
		fr.voteInfo = vote;
		fr.bill = currBillInfo;
		resultFragment = fr;
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		transaction.add(R.id.frame_content, fr);
		transaction.addToBackStack(null);
		transaction.commit();

	}

	private void showMultiVoteResultFragment(byte[] buffer) {

		Log.e(TAG, "showMultiVoteResultFragment");
		int voteId = buffer[3] & 0xff;
		if (dbm == null) {
			Toast.makeText(this, "请先开始会议", Toast.LENGTH_LONG).show();
			return;
		}

		currBillInfo = dbm.getBillInfo(meetingId, voteId);
		if (currBillInfo == null) {
			Toast.makeText(this, "显示批次结果失败，没有找到会议资料", Toast.LENGTH_LONG).show();
			return;
		}
		if (currBillInfo.billOptions == null) {
			Toast.makeText(this, "显示批次结果失败，选项为空", Toast.LENGTH_LONG).show();
			return;
		}

		hideCurrFragment();
		hideResultFragment();

		FragmentTransaction transaction = fm.beginTransaction();
		ResultMultiVoteFragment fr = new ResultMultiVoteFragment();
		fr.bill = currBillInfo;
		fr.data = buffer;
		resultFragment = fr;
		// transaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
		transaction.add(R.id.frame_content, fr);
		transaction.addToBackStack(null);
		transaction.commit();


	}

	private void setFirmUpdateFragment() {
//		if (firmFragment != null) {
//			Log.d(TAG, "setFirmUpdateFragment: firmFragment != null,return");
//			return;
//		}
		Log.d(TAG, "setFirmUpdateFragment: ");
		FragmentTransaction transaction = fm.beginTransaction();
		firmFragment = new FirmUpdateFragment();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		transaction.replace(R.id.frame_content, firmFragment);
		// transaction.addToBackStack(null);
		transaction.commitAllowingStateLoss();
	}

	private void hideFirmUpdateFragment() {
		if (firmFragment != null) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			tx.remove(firmFragment);
			tx.commitAllowingStateLoss();
			firmFragment = null;
		}
	}

	private void showNoFileMessage() {
		FragmentTransaction transaction = fm.beginTransaction();
		Fragment fr = new MeetingWelcomeFragment();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		transaction.replace(R.id.frame_content, fr);
		transaction.commitAllowingStateLoss();
	}

	private void setOfflineFragment() {
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		Fragment fr = new OfflineFragment();
		transaction.replace(R.id.frame_content, fr);
		transaction.commitAllowingStateLoss();
	}

	private void setComErrorFragment() {
		FragmentTransaction transaction = fm.beginTransaction();
		Fragment fr = new ComErrorFragment();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		transaction.replace(R.id.frame_content, fr);
		transaction.commitAllowingStateLoss();
	}

	private void setOnlineFragment() {
		FragmentTransaction transaction = fm.beginTransaction();
		Fragment fr = new OnLineFragment();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		transaction.replace(R.id.frame_content, fr);
		transaction.commitAllowingStateLoss();
	}

	private void setShowIdFragment() {
		if (showIdFragment == null) {
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			showIdFragment = new ShowIdFragment();
			transaction.add(R.id.frame_content, showIdFragment);
			transaction.addToBackStack(null);
			transaction.commitAllowingStateLoss();
		}
	}

	private void hideShowIdFragment() {
		if (showIdFragment != null) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			tx.remove(showIdFragment);
			tx.commitAllowingStateLoss();
			showIdFragment = null;
		}
	}

	private void setKeypadTestFragment() {

		inKeyTesting = true;
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		keypadTestFragment = new KeypadTestFragment();
		tx.add(R.id.frame_content, keypadTestFragment, "keytest");
		tx.addToBackStack(null);
		tx.commitAllowingStateLoss();
	}

	private void setChoice10Fragment() {
		inKeyTesting = true;
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		keypadTestFragment = new KeypadTestChoice10Fragment();
		tx.add(R.id.frame_content, keypadTestFragment, "keytest");
		tx.addToBackStack(null);
		tx.commitAllowingStateLoss();
	}

	private void hideKeypadTestFragment() {
		inKeyTesting = false;
		if (keypadTestFragment != null) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			tx.remove(keypadTestFragment);
			tx.commitAllowingStateLoss();
		}
	}

	private long lastSetDownloadTime = 0;

	private void setDownloadFragment() {
		if (System.currentTimeMillis() - lastSetDownloadTime < 1000) {
			Log.d(TAG, "ignore redownload");
			return;
		}
		if (mKeypadInfo == null) {
			presenter.getKeypadParam();
		}
		if (wifiSsid == null && wifiSsid.length() == 0) {
			Toast.makeText(this, "wifiSsid is null	", Toast.LENGTH_SHORT);
			return;
		}
		if (wifiPwd == null && wifiPwd.length() == 0) {
			Toast.makeText(this, "wifiPwd is null	", Toast.LENGTH_SHORT);
			return;
		}
		if (serverIp == null && serverIp.length() == 0) {
			Toast.makeText(this, "serverIp is null	", Toast.LENGTH_SHORT);
			return;
		}
		// if(serverPort == 0){
		// Toast.makeText(this, "serverPort is 0 ", Toast.LENGTH_SHORT);
		// return;
		// }
		Log.d(TAG, "start show Download UI------");
		lastSetDownloadTime = System.currentTimeMillis();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		downloadFragment = new DownloadFragment();
		downloadFragment.setInfo(wifiSsid, wifiPwd, serverIp, serverPort);
		transaction.add(R.id.frame_content, downloadFragment);
		transaction.commitAllowingStateLoss();

	}

	private void hideDownloadFragment() {
		if (downloadFragment != null) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			tx.remove(downloadFragment);
			tx.commitAllowingStateLoss();
			downloadFragment = null;
		}
	}

	private void setWelcomeFragment() {
		// if (mBaseInfo == null) {
		// mBaseInfo = new BaseInfo();
		// mBaseInfo.confId = 1;
		// }
		if (meetingInfo != null) {
			Log.d(TAG, "setWelcomeFragment   " + meetingInfo.meetingTitle);
		}
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		Fragment fr = new MeetingWelcomeFragment();
		transaction.replace(R.id.frame_content, fr);
		transaction.commitAllowingStateLoss();
	}

	private void setNoFileFragment() {
		// if (mBaseInfo == null) {
		// mBaseInfo = new BaseInfo();
		// mBaseInfo.confId = 1;
		// }
		if (meetingInfo != null) {
			Log.d(TAG, "setNoFileFragment   " + meetingInfo.meetingTitle);
		}

		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		NoFileFragment fr = new NoFileFragment();
		fr.meetingId = meetingId;
		transaction.replace(R.id.frame_content, fr);
		transaction.commitAllowingStateLoss();
	}

	private void setSigninFragment(VoteInfo info) {
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		SigninFragment fr = new SigninFragment();
		fr.setInfo(info);
		currFragment = fr;
		transaction.add(R.id.frame_content, currFragment);
		transaction.addToBackStack("sign");
		transaction.commitAllowingStateLoss();
	}

	private void setQuickVoteFragment() {
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		SingleTitleFragment fr = new SingleTitleFragment();
		fr.setInfo(currBillInfo);
		currFragment = fr;
		transaction.replace(R.id.frame_content, currFragment);
		transaction.commitAllowingStateLoss();
	}

	private void setTitleVoteFragment(VoteInfo vote) {
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		SingleTitleFragment fr = new SingleTitleFragment();
		fr.setInfo(currBillInfo,vote);
		currFragment = fr;
		transaction.add(R.id.frame_content, currFragment);
		transaction.addToBackStack("titleVote");
		transaction.commitAllowingStateLoss();
	}

	private void setMultiTitleVoteFragment(VoteInfo vote) {
		Log.d(TAG, "setMultiTitleVoteFragment");
		if (currBillInfo == null) {
			Toast.makeText(this, "没找到议案信息", Toast.LENGTH_LONG).show();
			return;
		}
		ArrayList<MultiTitleItem> list = dbm.getMultiTitleItems(meetingId, currBillInfo.billId);
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		MultiTitleFragment fr = new MultiTitleFragment();
		fr.setInfo(currBillInfo, vote, list);
		currFragment = fr;
		transaction.add(R.id.frame_content, currFragment);
		transaction.addToBackStack("multititleVote");
		transaction.commitAllowingStateLoss();
	}

	private void setSingleContentVoteFragment() {
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		ContentVoteFragment fr = new ContentVoteFragment();
		fr.setInfo(currBillInfo);
		currFragment = fr;
		transaction.replace(R.id.frame_content, currFragment);
		transaction.commitAllowingStateLoss();
	}

	private void setMultiContentVoteFragment() {
		ArrayList<MultiTitleItem> list = dbm.getMultiTitleItems(meetingId, currBillInfo.billId);
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		MultiContentFragment fr = new MultiContentFragment();
		fr.setInfo(currBillInfo,list);
		currFragment = fr;

		transaction.replace(R.id.frame_content, currFragment);
		transaction.commit();
	}

	private void setDocumentBrowserFragment() {

		ArrayList<BillInfo> list = dbm.getBillItems(meetingId);
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		if (docFragment == null) {
			docFragment = new DocumentBrowserFragment();
			docFragment.setInfo(list,false);
		}
		transaction.replace(R.id.frame_content, docFragment);
		transaction.commitAllowingStateLoss();
	}

	private void showBill(VoteInfo vInfo) {

		if (currBillInfo.billFile == null) {
			return;
		}

		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		if (vInfo != null) {
			MultiContentDetailFragment fr = new MultiContentDetailFragment();
			fr.setInfo(currBillInfo, vInfo, false);
			currFragment = fr;
			tx.add(R.id.frame_content, currFragment, "fDetail");
		} else {
			MultiContentDetailFragment fDetail = new MultiContentDetailFragment();
			fDetail.setInfo(currBillInfo,vInfo,false);
			tx.add(R.id.frame_content, fDetail, "fDetail");
		}
		tx.addToBackStack(null);
		tx.commit();
	}

	private void setMultiPinsShengFragment() {
		ArrayList<MultiTitleItem> list = dbm.getMultiTitleItems(meetingId, currBillInfo.billId);
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		 MultiPingshengFragment fr = new MultiPingshengFragment();
		fr.setInfo(currBillInfo, list);
		currFragment = fr;
		transaction.replace(R.id.frame_content, currFragment);
		transaction.commitAllowingStateLoss();
	}

	private void setElectionVoteFragment() {
		ArrayList<MultiTitleItem> list = dbm.getCandidateList(meetingId, currBillInfo.billId);
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		ElectionFragment fr = new ElectionFragment();
		fr.setInfo(currBillInfo,list);
		currFragment = fr;
		transaction.replace(R.id.frame_content, currFragment);
		transaction.commitAllowingStateLoss();
	}

	public void getBillInfo(int billId) {
		currBillInfo = dbm.getBillInfo(meetingId, billId);
	}

	@Override
	public void onComData(byte[] data, int len) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendData(byte[] data, int len) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onModelEvent(ModelInfo info) {
		Log.d(TAG, "onModelEvent");
		mModelInfo = info;
		if (firmFragment != null) {
			Intent intent = new Intent();
			intent.setAction("com.xpad.firm");
			intent.putExtra("ver", info.sVer);
			MainActivity.this.sendBroadcast(intent);
			Log.d(TAG, "send broadcast");
			// firmFragment.setFirmVer(mModelInfo.sVer);
		}

	}

	@Override
	public void onBaseEvent(BaseInfo info) {
		mBaseInfo = info;
		Log.d(TAG, "baseEvent ------------------- confId:" + info.confId + " bill ID:" + info.billId);
		meetingId = info.confId;

		if (info.confId != 0) {

			if (dbm != null && dbm.confId != info.confId) {
				dbm.closeDB();
				docFragment = null;
			}
			if (dbm == null || !dbm.checkDB() || dbm.confId != info.confId) {
				dbm = new DBManager(this, info.confId);// 如果没打开，则打开数据库
			}
			if (!dbm.checkDB()) {
				Message message = new Message();
				message.what = Msg_DBfileNotExist;
				myHandler.sendMessage(message);
				// return;
			}
			if (meetingInfo == null || info.billId <= 1) {
				meetingInfo = dbm.getMettingInfo(info.confId);

			}

			if (info.billId == 0) { // 没有议案的情况，显示欢迎界面
				if (mOnlineInfo.onLine == 1) {
					Message message = new Message();
					message.what = Msg_Welcome;
					myHandler.sendMessage(message);
				}
			} else if (info.billId == 255) { // 自由浏览
				if (dbm.checkDB()) {
					Message message = new Message();
					message.what = Msg_DocumentBroswer;
					myHandler.sendMessage(message);
				}

			} else {// 进入议案

				currBillInfo = dbm.getBillInfo(info.confId, info.billId);
				Message message = new Message();
				message.what = Msg_ShowBill;
				myHandler.sendMessage(message);
				// processBaseEvent();
			}

			presenter.getVoteStatus();

			// 显示会议标题
		} else {// confId为0 ，表示停止会议
			if (mOnlineInfo.onLine == 1) {
				Message message = new Message();
				message.what = Msg_Online;
				myHandler.sendMessage(message);
			}
			if (dbm != null) {
				dbm.closeDB();
			}
		}



	}

	@Override
	public void onVoteEvent(VoteInfo info) {
		startVoteTime = System.currentTimeMillis();
		Log.d(TAG, "onVoteEvent ========== mode=" + info.mode + " voteId:" + info.voteid);
//		if(meetingId == 0 && info.voteid != 0){
//			Log.d(TAG, "onVoteEvent: meetingid=0,voteid="+info.voteid + " ,abort ! getBaseStatus");
//			//Toast.makeText(this, "启动表决失败,未取到会议ID", Toast.LENGTH_LONG).show();
//			try{
//				Thread.sleep(500);
//			}catch (Exception e){
//				e.printStackTrace();
//			}
//			presenter.getBaseStatus();
//			return;
//		}
		Message message = new Message();
		message.what = Msg_onVoteEvent;
		message.obj = info;
		myHandler.sendMessage(message);

	}

	private void hideCurrFragment() {

		if (currFragment != null) {
			FragmentTransaction tx = fm.beginTransaction();
			tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			tx.remove(currFragment);
			tx.commit();
			currFragment = null;
		}
	}

	private void doVote(VoteInfo voteInfo) {

		if (voteInfo.mode == XPadApi.VoteType_KeypadTest) {
			setKeypadTestFragment();
			return;
		} else if (voteInfo.mode == XPadApi.VoteType_Choice && voteInfo.mode5 == 10) {
			setChoice10Fragment();
			return;
		} else if (voteInfo.mode == XPadApi.VoteType_Stop) {
			if (inKeyTesting) {
				hideKeypadTestFragment();
				return;
			}
		}

		hideResultFragment();
		/*
		 * 暂时去掉角色 if (mKeypadInfo != null && dbm != null) { roleType =
		 * dbm.getKeypadRole(mKeypadInfo.keyId); Log.d(TAG, "roleType:" +
		 * roleType); } if (roleType == 0) {// 列席代表不处理 return; }
		 */
		tmpMulResultBuffer = null;
		if (!(voteInfo.mode == XPadApi.VoteType_Stop && voteInfo.mode1_msgType == 2
				&& voteInfo.resultInfo.resultType == XPadApi.VoteType_BatchVote)) {
			hideCurrFragment();
		}
		// voteInfo.voteid = 1;//debug

		if (voteInfo.voteid > 0 && voteInfo.voteid < 255) {
			try {
				currBillInfo = dbm.getBillInfo(meetingId, voteInfo.voteid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (currBillInfo == null) {
			Log.e(TAG, "BillInfo not found!!!");
		}

		if (voteInfo.mode == XPadApi.VoteType_Stop) {
			Log.d(TAG, "getBackStackEntryCount:" + fm.getBackStackEntryCount());

			if (voteInfo.mode1_msgType == 2) { // 结果显示
				if (voteInfo.resultInfo.resultType == XPadApi.VoteType_BatchVote) { // 多项
					if (currFragment != null) {
						currFragment.onVoteEvent(voteInfo);
					}
				} else {
					showVoteResultFragment(voteInfo);
				}
			}
		} else if (voteInfo.mode == XPadApi.VoteType_Signin) {
			setSigninFragment(voteInfo);
		} else {
			if (voteInfo.mode == XPadApi.VoteType_BatchVote) {
				if (dbm != null && dbm.checkDB()) {
					setMultiTitleVoteFragment(voteInfo);
				} else {
					Log.d(TAG, "doVote: "+"启动批次表决失败,会议" + meetingId + "不存在,请下载会议资料");
					Toast.makeText(this, "启动批次表决失败,会议" + meetingId + "不存在,请下载会议资料", Toast.LENGTH_LONG).show();
					//presenter.getBaseStatus();
				}
			} else {
				if (voteInfo.init == 0 || checkHasBillFile(currBillInfo) == false) {
					setTitleVoteFragment(voteInfo);
				} else {
					showBill(voteInfo);
				}
			}
		}
		// if (currFragment != null) {
		// currFragment.onVoteEvent(msg.arg1, msg.arg2, msg.obj);
		// }

	}

	private boolean checkHasBillFile(BillInfo bill) {
		return (bill != null && bill.billFile != null && bill.billFile.length() > 0);
	}

	@Override
	public void onVoteSubmitSuccess() {
		Message message = new Message();
		message.what = Msg_onCommitSuccessEvent;
		myHandler.sendMessage(message);

	}

	@Override
	public void onVoteSubmitAllOkSuccess() {
		Message message = new Message();
		message.what = Msg_onCommitAllOkSuccessEvent;
		myHandler.sendMessage(message);
	}

	@Override
	public void onKeyPadEvent(KeypadInfo info) {
		Log.d(TAG, "onKeyPadEvent");
		mKeypadInfo = info;
		XPadSystem.setStatusBarPadID(this, info.keyId);
		XPadSystem.setStatusBarChannel(this, info.chan);
		if (info.cmd1 == 8 || info.cmd1 == 9) {
			Message message = new Message();
			message.what = Msg_MatchInfo;
			message.obj = info;
			myHandler.sendMessage(message);
		}

	}

	@Override
	public void onOnLineEvent(OnLineInfo info) {

		if (info.comError == 1) {
			if (info.comError != mOnlineInfo.comError) {
				Message message = new Message();
				message.what = Msg_ComError;
				myHandler.sendMessage(message);
			}

		} else {
			if (info.onLine != mOnlineInfo.onLine) {
				if (info.onLine == 1) {
					Message message = new Message();
					message.what = Msg_Online;
					myHandler.sendMessage(message);
					presenter.getBaseStatus();

					if (mModelInfo == null  ) {
						myHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								Log.i(TAG, "onOnLineEvent: getKeypadParam()");
								presenter.getKeypadParam();

							}
						},500);

					}
					if (mKeypadInfo == null  ) {
						myHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								Log.i(TAG, "onOnLineEvent: getWorkMode()");
								presenter.getWorkMode();
							}
						},1000);
					}

				} else {
					Message message = new Message();
					message.what = Msg_Offline;
					myHandler.sendMessage(message);
				}
			}



		}

		mOnlineInfo = info;
		XPadSystem.setStatusBarDataIcon(this, info.tx, info.rx);
		XPadSystem.setStatusBarChannel(this, info.chan);
		XPadSystem.setStatusBarBaseId(this, getString(R.string.base_id) + ":" + info.baseId);
		XPadSystem.setStatusBarSingal(this, info.rssi);


	}

	private CmdDataInfo cmdInfo;
	private long lastRecvCmdDataTime;

	@Override
	public void onCmdData(CmdDataInfo info) {

		if (cmdInfo != null && info.cmd == cmdInfo.cmd && Arrays.equals(info.data, cmdInfo.data)
				&& (System.currentTimeMillis() - lastRecvCmdDataTime < 1000)) {
			Log.i(TAG, "onCmdData,recv same data,  ignore!");
			return;
		}
		lastRecvCmdDataTime = System.currentTimeMillis();
		cmdInfo = info;
		XPadApi.printDataBuf(info.data, info.data.length, "module.cmd:" + info.cmd + "  data:");
		if (info.cmd == 7) { // 键盘测试
			Message message = new Message();
			message.what = Msg_CommunicationTest;
			message.obj = info.data;
			myHandler.sendMessage(message);
		} else if (info.cmd == 10) {// 显示编号
			if (info.data[0] == 0) {
				Log.d(TAG, "Msg_HideShowID");
				Message message = new Message();
				message.what = Msg_HideShowID;
				myHandler.sendMessage(message);
			} else {
				Log.d(TAG, "Msg_ShowID");
				Message message = new Message();
				message.what = Msg_ShowID;
				myHandler.sendMessage(message);
			}
		} else if (info.cmd == 5) {// 关机
			XPadSystem.powerOffXPad(this);
		} else if (info.cmd == 50) {// 主控透传
			byte cmd = info.data[0];
			Log.i(TAG, "module.cmd:" + cmd);
			byte[] data = Arrays.copyOfRange(info.data, 1, 19);
			Message message = null;
			switch (cmd) {
				case 0x31:// wifi ssid

					wifiSsid = new String(data).trim();
					Log.d(TAG, "receve 0x31 wifi ssid:" + wifiSsid);
					break;
				case 0x32:// wifi pwd

					wifiPwd = new String(data).trim();
					Log.d(TAG, "receve 0x32 wifi pwd:" + wifiPwd);
					break;

				case 0x33: // ip

					serverIp = new String(data).trim();
					Log.d(TAG, "receve 0x33 server ip:" + serverIp);
					break;
				case 0x34: // start download
					Log.d(TAG, "receve start download");
					message = new Message();
					message.what = Msg_Download;
					myHandler.sendMessage(message);

					break;
				case 0x35: // stop download
					Log.d(TAG, "receve stop download");
					message = new Message();
					message.what = Msg_StopDownload;
					myHandler.sendMessage(message);
					break;
				case 0x36: // 暂去掉角色控制
					Log.d(TAG, "not support");
					break;
				// roleType = info.data[1] == 0x30 ? 0 : 1; // 1是正式代表，0是列席代表,不响应投票
				// dbm.writeKeypadRole(mKeypadInfo.keyId, roleType);
				// Log.d(TAG, "role:" + roleType);
				// break;
				case 0x37:
					XPadSystem.rebootXPad(this);
					break;
				case 0x38:
					message = new Message();
					message.what = Msg_HideVoteResult;
					myHandler.sendMessage(message);
					break;
				case 0x39:
					message = new Message();
					message.what = Msg_ChangeBright;
					message.arg1 = info.data[1];
					myHandler.sendMessage(message);
					SharedPreferencesUtil.saveData(MainActivity.this, "bright", Integer.valueOf(info.data[1]));
					break;
				case 0x40:
					if (info.data[1] == 0) {
						message = new Message();
						message.what = Msg_ScreenDark;
						myHandler.sendMessage(message);
					} else {
						message = new Message();
						message.what = Msg_ChangeBright;
						message.arg1 = brigntLevel;
						myHandler.sendMessage(message);
					}
					break;
				case 0x41:
					darkTime = Integer.valueOf(info.data[1]);
					scrTimerCnt = 0;
					Log.d(TAG, "set darkTime:" + darkTime);
					SharedPreferencesUtil.saveData(MainActivity.this, "darkTime", darkTime);
					break;

				case 0x42:
					// message = new Message();
					// message.what = Msg_FirmUpdate;
					// myHandler.sendMessage(message);
					break;
				case 0x43:
					// message = new Message();
					// message.what = Msg_HideFirmUpdate;
					// myHandler.sendMessage(message);
					break;
				default:
					break;
			}
		}

	}

	private byte[] tmpMulResultBuffer;

	@Override
	public void onMultiPackageData(byte[] data, int len) {

		XPadApi.printDataBuf(data, len, "onMultiPackageData:");
		byte[] buf = new byte[len];
		Arrays.fill(buf, (byte) 0x0);
		System.arraycopy(data, 0, buf, 0, len);

		if ((data[0] & 0xff) == 0xF2 && data[1] == 20) {

			if (tmpMulResultBuffer != null && Arrays.equals(buf, tmpMulResultBuffer)) {
				Log.e(TAG , "onMultiPackageData same data,abort");
				return;
			}
			tmpMulResultBuffer = buf;

			Message message = new Message();
			message.what = Msg_ShowMultiVoteResult;
			message.obj = buf;
			myHandler.sendMessage(message);

			return;
		}

		String str;
		try {
			str = new String(buf, "GB2312").trim();
			Log.d(TAG, "onMultiPackageInfo:" + str);
			Message message = new Message();
			message.what = Msg_onMultiPackageData;
			message.arg1 = len;
			message.obj = str;
			myHandler.sendMessage(message);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onVoteSubmitError() {
		Message message = new Message();
		message.what = Msg_onCommitErrorEvent;
		myHandler.sendMessage(message);

	}

	TimerTask screenTask = new TimerTask() {
		public void run() {
			scrTimerCnt++;
			// Log.d(TAG, "scrTimerCnt:" + scrTimerCnt);
			if (scrTimerCnt == darkTime * 60) {
				scrTimerCnt = 0;
				Message message = new Message();
				message.what = Msg_ScreenDark;
				myHandler.sendMessage(message);

			}
		}
	};

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			scrTimerCnt = 0;
			ScreenUtil.setNormalMode(MainActivity.this, brigntLevel);
			Log.i("CustomBtnonTouchEvent", "MotionEvent.ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.i("CustomButton--onTouchEvent", "MotionEvent.ACTION_MOVE");
			break;
		case MotionEvent.ACTION_UP:
			// Log.i("CustomButton--onTouchEvent", "MotionEvent.ACTION_UP");
			break;

		default:
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public void onFirmUpdate(int percent) {
		if (firmFragment != null) {
			firmFragment.onFirmUpdate(percent);
		}

	}

	@Override
	public void onFirmUpdateResult(boolean success, String msg) {

		if (firmFragment != null) {
			firmFragment.onFirmUpdateResult(success, msg);
		}
	}

	@Override
	public void onFirmUpdateInfo(String info) {
		if (firmFragment != null) {
			firmFragment.onFirmUpdateInfo(info);
		}
	}

	private File checkUpdateApkFile() {
		String filePath = Environment.getExternalStorageDirectory().getPath() + "/sunvote/apk/";
		File file = new File(filePath);
		if(file==null){
			Log.i(TAG, "checkUpdateApkFile: no apk dir");
			return null;
		}
		if (!file.exists()) {
			Log.i(TAG, "checkUpdateApkFile: no apk dir2");
			return null;
		}
		File[] files = file.listFiles();

		if (files == null || files.length == 0) {
			Log.i(TAG, "checkUpdateApkFile: no apk file");
			return null;
		}
		return files[0];
	}

	private void clearApkFile() {
		try {
			String filePath = Environment.getExternalStorageDirectory().getPath() + "/sunvote/apk/";
			File file = new File(filePath);
			FileUtil.deleteFile(file);

			filePath = Environment.getExternalStorageDirectory().getPath() + "/sunvote/apk.zip";
			file = new File(filePath);
			FileUtil.deleteFile(file);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}


	protected void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivity(intent);
	}
}
