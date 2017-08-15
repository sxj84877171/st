package com.sunvote.xpadapp.fragments;

import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.MyFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShowIdFragment extends MyFragment {
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("ShowIdFragment", "onCreateView");
		View view = inflater.inflate(R.layout.fragment_show_id, container, false);
		
		TextView  tvId = (TextView) view.findViewById(R.id.fragment_show_id_title);
		if(mMainActivity.mOnlineInfo!=null ){
			tvId.setText(""+ mMainActivity.mOnlineInfo.keyId);
		}
		
		return view;
	}
	
}
