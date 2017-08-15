package com.sunvote.xpadapp.fragments;

import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.MyFragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class KeypadTestFragment extends MyFragment {
	private TextView tvId;
	private TextView tvSn;
	private TextView tvVer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("KeypadTestFragment", "onCreateView");
		View view = inflater.inflate(R.layout.fragment_keypad_test, container, false);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				;
			}
		});
		
		tvId = (TextView) view.findViewById(R.id.keypad_test_keypad_id);
		tvSn = (TextView) view.findViewById(R.id.keypad_test_keypad_sn);
		tvVer = (TextView) view.findViewById(R.id.keypad_test_keypad_ver);
 
		if(mMainActivity.mOnlineInfo!=null ){
			tvId.setText(getString(R.string.keypad_id)+ mMainActivity.mOnlineInfo.keyId);
			tvSn.setText("SN:"+ mMainActivity.mOnlineInfo.keySn);
			
		}
		if(mMainActivity.mModelInfo!=null && mMainActivity.mModelInfo.sVer !=null){
			tvVer.setText(getString(R.string.firmware_ver)+ mMainActivity.mModelInfo.sVer);

		}
		
		ImageButton btnBack = (ImageButton) view.findViewById(R.id.keypad_test_btnback);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goBack();
			}
		});
		
		return view;
	}
	
	private void goBack() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.remove(KeypadTestFragment.this);
		tx.commit();

	}

}
