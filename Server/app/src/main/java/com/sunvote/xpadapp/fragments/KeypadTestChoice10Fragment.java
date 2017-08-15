package com.sunvote.xpadapp.fragments;

import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadcomm.XPadApi;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class KeypadTestChoice10Fragment extends BaseFragment implements View.OnClickListener {
	Button btn1;
	Button btn2;
	Button btn3;
	Button btn4;
	Button btn5;
	Button btn6;
	Button btn7;
	Button btn8;
	Button btn9;
	Button btn10;
	
	TextView tvInfo;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 
		Log.d("KeypadTestFragment", "onCreateView");
		View view = inflater.inflate(R.layout.fragment_choice10_test, container, false);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				;
			}
		});
		
		tvInfo = (TextView)view.findViewById(R.id.keypad_test_choice10_info);

		btn1 = (Button) view.findViewById(R.id.keypad_test_choice10_btn_1);
		btn1.setOnClickListener(this);

		btn2 = (Button) view.findViewById(R.id.keypad_test_choice10_btn_2);
		btn2.setOnClickListener(this);

		btn3 = (Button) view.findViewById(R.id.keypad_test_choice10_btn_3);
		btn3.setOnClickListener(this);

		btn4 = (Button) view.findViewById(R.id.keypad_test_choice10_btn_4);
		btn4.setOnClickListener(this);

		btn5 = (Button) view.findViewById(R.id.keypad_test_choice10_btn_5);
		btn5.setOnClickListener(this);

		btn6 = (Button) view.findViewById(R.id.keypad_test_choice10_btn_6);
		btn6.setOnClickListener(this);

		btn7 = (Button) view.findViewById(R.id.keypad_test_choice10_btn_7);
		btn7.setOnClickListener(this);

		btn8 = (Button) view.findViewById(R.id.keypad_test_choice10_btn_8);
		btn8.setOnClickListener(this);

		btn9 = (Button) view.findViewById(R.id.keypad_test_choice10_btn_9);
		btn9.setOnClickListener(this);

		btn10 = (Button) view.findViewById(R.id.keypad_test_choice10_btn_10);
		btn10.setOnClickListener(this);
		
		ImageButton btnBack = (ImageButton) view.findViewById(R.id.choice10_btnback);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goBack();
			}
		});
		

		return view;
	}

	@Override
	public void onClick(View v) {
		Button btn =(Button) v;
		
		tvInfo.setText( getString(R.string.sent) +  " " + btn.getText().toString());
		 
		String str =btn.getText().toString() +","+ String.valueOf(System.currentTimeMillis()-mMainActivity.startVoteTime) ;
		
		mMainActivity.presenter.submitVote( XPadApi.AnsType_Select,str);
		
	  
	}
	
	private void goBack() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out); 
		//tx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); 
		tx.remove(this);
		tx.commit();

	}

}
