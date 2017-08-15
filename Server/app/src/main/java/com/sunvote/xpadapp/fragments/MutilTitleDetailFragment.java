package com.sunvote.xpadapp.fragments;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.MyFragment;
import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadapp.db.modal.MultiTitleItem;
import com.sunvote.xpadcomm.XPadApi;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MutilTitleDetailFragment extends MyFragment {

	private String TAG = "MutileTitleDetailFragment";
	private MultiTitleItem data;
	private TextView tvTitle;
	private TextView tvTips;
	private TextView ivReuslt;
	private Button btnA;
	private Button btnB;
	private Button btnC;

	public String[] options;
	public int modifyable;
	public int secret;
	public BillInfo bill;
	boolean submited = false;

	private TitleVoteOnBack ls;

	public interface TitleVoteOnBack {
		void onBack();
	}

	public void setInfo(MultiTitleItem item, TitleVoteOnBack listener) {
		data = item;
		ls = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_multi_title_detail, container, false);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				;
			}
		});
		final MainActivity mact = (MainActivity) getActivity();

		tvTitle = (TextView) view.findViewById(R.id.mutli_title_detail_content);
		if (data.title != null) {
			tvTitle.setText(data.title);
		}

		tvTips = (TextView) view.findViewById(R.id.mutli_title_detail_tv_tips);
		ivReuslt = (TextView) view.findViewById(R.id.mutli_title_detail_tv_result);
		ivReuslt.setVisibility(View.INVISIBLE);

		btnA = (Button) view.findViewById(R.id.mutli_title_detail_btnA);
		btnA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				doVoteWithIndex(1);

			}
		});

		btnB = (Button) view.findViewById(R.id.mutli_title_detail_btnB);
		btnB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doVoteWithIndex(2);
			}
		});

		btnC = (Button) view.findViewById(R.id.mutli_title_detail_btnC);
		btnC.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (options.length == 2) {
					doVoteWithIndex(2);
				} else {
					doVoteWithIndex(3);
				}
			}
		});

		ImageButton btnBack = (ImageButton) view.findViewById(R.id.mutli_title_detail_btnback);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goBack();
			}
		});

		if (options.length == 2) {
			btnA.setText(options[0]);
			btnB.setVisibility(View.INVISIBLE);
			btnC.setText(options[1]);
		} else if (options.length == 3) {
			btnA.setText(options[0]);
			btnB.setText(options[1]);
			btnC.setText(options[2]);
		}

		if (data.startVote) {
			showVote();
//			if(!data.submited){
//				showVote();
//			}else{
//				hideVote();
//			}
			
		} else {
			hideVote();
		}
		
		 
		showResult();
		return view;
	}

	private void doVoteWithIndex(int index) {


		data.result = index;
		mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, data.No + ":" + index);
		Log.d(TAG, "vote:  No:"+ data.No + "val:"+ index);
		showResult();

		// new Thread(new Runnable() {
		// public void run() {
		// disableVote();
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// goBack();
		// }
		// }).start();

		// if (modifyable == 1) {
		// // showModify();
		// } else {
		disableVote();
		// }
	}
	
	private void showResult(){
		if(data.result>0){
			if (secret == 0) {
				if (bill.billType == BillInfo.BillType_Vote) {
					ivReuslt.setText("");
					ivReuslt.setBackgroundResource(getResultResourceByTitle(options[data.result-1]));
				} else if (bill.billType == BillInfo.BillType_Evaluate) {
					ivReuslt.setText(options[data.result-1]);
					ivReuslt.setBackgroundResource(R.drawable.voted_empty);
					 
				}
			} else {
				ivReuslt.setText("");
				ivReuslt.setBackgroundResource(R.drawable.voted);
			}
			ivReuslt.setVisibility(View.VISIBLE);
		}else{
			ivReuslt.setVisibility(View.INVISIBLE);
		}
		
	}

	private void goBack() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.remove(MutilTitleDetailFragment.this);
		tx.commit();
		ls.onBack();
	}

	public void hideVote() {
		btnA.setVisibility(View.INVISIBLE);
		btnB.setVisibility(View.INVISIBLE);
		btnC.setVisibility(View.INVISIBLE);
		ivReuslt.setVisibility(View.INVISIBLE);
		tvTips.setVisibility(View.INVISIBLE);
	}

	public void showVote() {
		
		btnA.setVisibility(View.VISIBLE);
		if(options.length==3){
			btnB.setVisibility(View.VISIBLE);
		}else{
			btnB.setVisibility(View.INVISIBLE);
		}
		btnC.setVisibility(View.VISIBLE);

		tvTips.setVisibility(View.VISIBLE);
	}

	public void disableVote() {
		btnA.setEnabled(false);
		btnB.setEnabled(false);
		btnC.setEnabled(false);
	}

	public void onVoteSubmitSuccess() {
		goBack();
	}

	
}
