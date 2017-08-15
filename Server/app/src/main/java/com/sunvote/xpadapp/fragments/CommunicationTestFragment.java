package com.sunvote.xpadapp.fragments;

import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.MyFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CommunicationTestFragment extends MyFragment {
	public byte[] data;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_admin, container, false);
		
		 
		TextView tvResutl = (TextView)view.findViewById(R.id.comm_test_tv_result);
		tvResutl.setText(
				getString(R.string.communication_test)+data[0]+"  "+ data[1] +"  "+ data[2] +"  "+ data[3]);		 
		
		return view;
	}
}
