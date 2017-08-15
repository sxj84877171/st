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
import android.widget.TextView;

public class ComErrorFragment extends MyFragment {
	long  lastTapTime;
	int tapCount;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_com_error, container, false);
		
		TextView tvtitle = (TextView)view.findViewById(R.id.com_error_title);
		View adminView = (View)view.findViewById(R.id.com_error_btn_admin);
		adminView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(System.currentTimeMillis()-lastTapTime > 500){
					tapCount =0;
					Log.d("OfflineFragment", "tapCount:0");
				}else{
					tapCount++;
					Log.d("OfflineFragment", "tapCount:"+tapCount);
				}
				lastTapTime = System.currentTimeMillis();
				if(tapCount>=9){
					tapCount=0;
					showAdmin();
				}
			}
		});
		return view;
	}
	
	private void showAdmin(){
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		AdminFragment fAdmin = new AdminFragment();
		tx.add(R.id.frame_content, fAdmin, "admin");
		tx.addToBackStack(null);
		tx.commit();
	}

}
