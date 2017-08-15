package com.sunvote.xpadapp.fragments;

import java.util.Timer;
import java.util.TimerTask;

import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadcomm.ScreenUtil;
import com.sunvote.xpadcomm.XPadSystem;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class AdminFragment extends BaseFragment {
	public TextView tvSn;
	TextView tvModalInfo;
	String TAG = "AdminFragment";

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin, container, false);
		tvModalInfo = (TextView) view.findViewById(R.id.admin_modal_info);
		tvSn = (TextView) view.findViewById(R.id.admin_modal_sn);
		setKeySn();
		try {
			tvModalInfo.setText(getString(R.string.app_version) + getVersionName() +" "+ getString(R.string.hardware_ver) + mMainActivity.mModelInfo.hModel +" "+getString(R.string.firmware_ver)
					+ mMainActivity.mModelInfo.sVer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ImageButton btnBack = (ImageButton) view.findViewById(R.id.admin_btnback);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				timer.cancel();
				goBack();
			}
		});

		Button btnMatch = (Button) view.findViewById(R.id.admin_match);
		btnMatch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("AdminFragment", "btnMatch");
				mMainActivity.presenter.execKeypadMatch(0, 0);
			}
		});

		Button btnConfig = (Button) view.findViewById(R.id.admin_config_mode);
		btnConfig.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("AdminFragment", "btnConfig");
				mMainActivity.presenter.configMode();
				
			}
		});
		
		Button btnBright = (Button)view.findViewById(R.id.btnBright);
		btnBright.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					if(ScreenUtil.isAutoBrightness(getActivity())){
						ScreenUtil.stopAutoBrightness(getActivity());
					}
				   int  bval =	ScreenUtil.getScreenBrightness(getActivity());
				   Log.d(TAG, "brightness:"+bval);
				  ScreenUtil.setBrightness(getActivity(), 100);
				  ScreenUtil.saveBrightness(getActivity(), 100);
				   bval =	ScreenUtil.getScreenBrightness(getActivity());
				   Log.d(TAG, "brightness:"+bval);
				} catch (Exception e) {
                   e.printStackTrace();
                }
			}
		});
		
		Button btnDark = (Button)view.findViewById(R.id.btnDark);
		btnDark.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				XPadSystem.goToSleep(getActivity());
//				if(true){
//					return;
//				}
				
				try {
					if(ScreenUtil.isAutoBrightness(getActivity())){
						ScreenUtil.stopAutoBrightness(getActivity());
					}
				   int  bval =	ScreenUtil.getScreenBrightness(getActivity());
				   Log.d(TAG, "brightness:"+bval);
				  ScreenUtil.setBrightness(getActivity(), 0);
				  ScreenUtil.saveBrightness(getActivity(), 0);
				   bval =	ScreenUtil.getScreenBrightness(getActivity());
				   Log.d(TAG, "brightness:"+bval);
				} catch (Exception e) {
                   e.printStackTrace();
                }
			}
		});
		
		 
		
		timer.schedule(task, 1000, 1000); 
		
		
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		XPadSystem.setNavgationVisible(mMainActivity);
		super.onResume();
	}
	
	@Override
	public void onStop() {
		Log.d("adminFr", "onstop");
		XPadSystem.setNavgationGone(mMainActivity);
		super.onStop();
	}
	
	private final Timer timer = new Timer(); 
	
	Handler handler = new Handler() { 
	    @Override 
	    public void handleMessage(Message msg) { 
	        if(msg.what == 111){
	        	setKeySn();
	        }
	        super.handleMessage(msg); 
	    }
	};
	
	private TimerTask task = new TimerTask() { 
	    @Override 
	    public void run() { 
	        // TODO Auto-generated method stub 
	        Message message = new Message(); 
	        message.what = 111; 
	        handler.sendMessage(message); 
	    } 
	}; 
	

	public void setKeySn() {
		tvSn.setText("SN:" + mMainActivity.mOnlineInfo.keySn);
	}

	private String getVersionName() throws Exception {
		String version = "";

		PackageManager packageManager = getActivity().getPackageManager();

		PackageInfo packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
		version = packInfo.versionName;// + "." + packInfo.versionCode;

		return version;
	}

	private void goBack() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out); 
		//tx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); 
		tx.remove(AdminFragment.this);
		tx.commit();

	}
	
	
}
