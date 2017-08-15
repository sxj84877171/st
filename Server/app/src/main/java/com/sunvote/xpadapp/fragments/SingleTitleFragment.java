package com.sunvote.xpadapp.fragments;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadapp.fragments.MultiContentDetailFragment.ContentVoteOnBack;
import com.sunvote.xpadcomm.XPadApi;
import com.sunvote.xpadcomm.XPadApiInterface.VoteInfo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SingleTitleFragment extends BaseFragment implements ContentVoteOnBack {

	private TextView tvTitle;
	private TextView tvTips;
	private TextView ivReuslt;
	private Button btnA;
	private Button btnB;
	private Button btnC;
	private Button btnModify;
	private String[] options;
	private BillInfo bill;
	private VoteInfo voteInfo;

	private String TAG = "SingleTitleFragment";
	MultiContentDetailFragment fDetail;

	private int voteValue;
	private RelativeLayout confirmLayout;
	private TextView tvConfirmText;

	public String[] voteOptions = new String[13];

	public void setInfo(BillInfo info) {
		bill = info;
		options = bill.billOptions.split("/");
	}

	public void setInfo(BillInfo info, VoteInfo vote) {

		bill = info;

		if (bill == null) {
			bill = new BillInfo();
		}
		voteInfo = vote;

	}

	private void initOptions() {
		String str = getString(R.string.zc_fd);
		voteOptions[0] = str;
		voteOptions[1] = getString(R.string.zc_fd_qq);
		voteOptions[2] = getString(R.string.ty_fd_qq);
		voteOptions[3] = getString(R.string.my_jbmy_bmy);
		voteOptions[4] = getString(R.string.my_jbmy_bmy_blj);
		voteOptions[5] = getString(R.string.my_jbmy_yb_bmy);
		voteOptions[6] = getString(R.string.fcmy_my_blj_bmy_fcbmy);
		voteOptions[7] = getString(R.string.fcty_ty_bqd_bty_fcbty);
		voteOptions[8] = getString(R.string.yx_cz_bcz);
		voteOptions[9] = getString(R.string.yx_cz_jbcz_bcz);
		voteOptions[10] = getString(R.string.y_l_z_c);
		voteOptions[11] = getString(R.string.my_bmy_fcbmy);

		if (voteInfo.mode == XPadApi.VoteType_Vote) {
			if (voteInfo.mode1_msgType == 1) {
				options = voteOptions[1].split("/");
			} else if (voteInfo.mode1_msgType == 2) {
				options = voteOptions[0].split("/");
			} else if (voteInfo.mode1_msgType == 3) {
				options = voteOptions[voteInfo.mode4 - 1].split("/");
			}
		} else if (voteInfo.mode == XPadApi.VoteType_Evaluate) {
			if (voteInfo.mode1_msgType > 0) {
				options = voteOptions[voteInfo.mode1_msgType - 1].split("/");
			}
		}else{
			options = voteOptions[1].split("/");
		}
//		Log.d(TAG, "options:" + options.toString());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("SigninFragment", "onCreateView");

		View view = inflater.inflate(R.layout.fragment_singlevote, container, false);
		initOptions();
		final MainActivity mact = (MainActivity) getActivity();
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				;
			}
		});

		tvTitle = (TextView) view.findViewById(R.id.singlevote_content);
		if (bill != null && bill.title != null && bill.title.length() > 0) {
			tvTitle.setText(bill.title);
		}

		tvTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bill != null && bill.billFile != null && bill.billFile.length() > 0) {
					if (voteInfo != null && voteInfo.file == 1) {
						FragmentManager fm = getFragmentManager();
						FragmentTransaction tx = fm.beginTransaction();
						fDetail = new MultiContentDetailFragment();
						fDetail.setInfo(bill, voteInfo, true);
						fDetail.contentBackListener = SingleTitleFragment.this;
						tx.add(R.id.frame_content, fDetail, "fDetail");
						tx.addToBackStack(null);
						tx.commit();
					}
				}
			}
		});

		ImageView iv = (ImageView) view.findViewById(R.id.singlevote_img_detail);
		if (bill != null && bill.billFile != null && bill.billFile.length() > 0
				&& (voteInfo != null && voteInfo.file == 1)) {
			iv.setVisibility(View.VISIBLE);
		} else {
			iv.setVisibility(View.INVISIBLE);
		}

		tvTips = (TextView) view.findViewById(R.id.singlevote_tv_tips);
		tvTips.setText("");
		ivReuslt = (TextView) view.findViewById(R.id.singlevote_tv_result);
		ivReuslt.setVisibility(View.INVISIBLE);

		btnA = (Button) view.findViewById(R.id.singlevote_btnA);
		btnA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (voteInfo.mode2_modify == 1) {
					doVoteWithIndex(1);
				} else {
					showConfirmWithValue(1);
				}
			}
		});

		btnB = (Button) view.findViewById(R.id.singlevote_btnB);
		btnB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (voteInfo.mode2_modify == 1) {
					doVoteWithIndex(2);
				} else {
					showConfirmWithValue(2);
				}

			}
		});

		btnC = (Button) view.findViewById(R.id.singlevote_btnC);
		btnC.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (options.length == 2) {
					if (voteInfo.mode2_modify == 1) {
						doVoteWithIndex(2);
					} else {
						showConfirmWithValue(2);
					}
				} else {
					if (voteInfo.mode2_modify == 1) {
						doVoteWithIndex(3);
					} else {
						showConfirmWithValue(3);
					}
				}
			}
		});

		btnModify = (Button) view.findViewById(R.id.singlevote_btn_modify);
		btnModify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnModify.setVisibility(View.INVISIBLE);
				showVote();
				ivReuslt.setVisibility(View.VISIBLE);
			}
		});

		tvConfirmText = (TextView) view.findViewById(R.id.singlevote_confirm_textview);
		confirmLayout = (RelativeLayout) view.findViewById(R.id.singlevote_confirm_panel);
		Button btnConfirmOK = (Button) view.findViewById(R.id.singlevote_btn_confirm_ok);
		btnConfirmOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideConfirm();
				doVoteWithIndex(voteValue);
			}
		});

		Button btnConfirmCancel = (Button) view.findViewById(R.id.singlevote_btn_confirm_cancel);
		btnConfirmCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideConfirm();
			}
		});

		// options = bill.billOptions.split("/");
		if (options.length == 2) {
			btnA.setText(options[0]);
			btnB.setVisibility(View.INVISIBLE);
			btnC.setText(options[1]);
		} else if (options.length == 3) {
			btnA.setText(options[0]);
			btnB.setText(options[1]);
			btnC.setText(options[2]);
		}

		onVoteEvent(voteInfo);
		// hideVote();
		return view;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onDetach() {

		super.onDetach();
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	@Override
	public void onStop() {
		closeDetail();
		super.onStop();
	}

	private void showConfirmWithValue(int value) {

		voteValue = value;
		tvConfirmText.setText(
				getString(R.string.you_selected) + options[value - 1] + getString(R.string.you_selected_confirm));
		confirmLayout.setVisibility(View.VISIBLE);

		btnA.setVisibility(View.INVISIBLE);
		btnB.setVisibility(View.INVISIBLE);
		btnC.setVisibility(View.INVISIBLE);
	}

	private void hideConfirm() {
		confirmLayout.setVisibility(View.GONE);
		btnA.setVisibility(View.VISIBLE);
		if (options.length == 3) {
			btnB.setVisibility(View.VISIBLE);
		}
		btnC.setVisibility(View.VISIBLE);
	}

	private void doVoteWithIndex(int index) {

		if (bill != null) {
			tvTips.setText(getString(R.string.submiting));
			bill.voteResult = index;
		}
		mMainActivity.presenter.submitVote(XPadApi.AnsType_Single, String.valueOf(index));
		disableVote();
		showResult();

	}

	private void showResult() {
		if (bill.voteResult > 0) {
			if (voteInfo.mode3_secret == 0) {//
				ivReuslt.setText(options[bill.voteResult - 1]);
				ivReuslt.setBackgroundResource(R.drawable.voted_empty);
			} else {
				ivReuslt.setText("");
				ivReuslt.setBackgroundResource(R.drawable.voted);
			}
			ivReuslt.setVisibility(View.VISIBLE);

		} else {
			ivReuslt.setVisibility(View.INVISIBLE);
		}

	}

	private void hideVote() {
		btnA.setVisibility(View.INVISIBLE);
		btnB.setVisibility(View.INVISIBLE);
		btnC.setVisibility(View.INVISIBLE);

		// tvTips.setVisibility(View.INVISIBLE);
		btnModify.setVisibility(View.INVISIBLE);
	}

	private void showVote() {
		btnA.setVisibility(View.VISIBLE);
		if (options.length == 3) {
			btnB.setVisibility(View.VISIBLE);
		}
		btnC.setVisibility(View.VISIBLE);
		ivReuslt.setVisibility(View.INVISIBLE);
		// tvTips.setVisibility(View.VISIBLE);
		btnModify.setVisibility(View.INVISIBLE);

		btnA.setEnabled(true);
		btnB.setEnabled(true);
		btnC.setEnabled(true);

	}

	private void showModify() {
		hideVote();
		btnModify.setVisibility(View.VISIBLE);
		// ivReuslt.setVisibility(View.VISIBLE);
	}

	private void disableVote() {
		btnA.setEnabled(false);
		btnB.setEnabled(false);
		btnC.setEnabled(false);
	}

	private void closeDetail() {
		if (fDetail != null) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			tx.remove(fDetail);
			tx.commit();
		}
	}

	@Override
	public void onVoteEvent(int baseId, int iMode, Object info) {
		if (iMode == XPadApi.VoteType_Stop) {
			hideVote();

		} else {
			voteInfo = (VoteInfo) info;
			if (voteInfo.mode2_modify == 1) {
				tvTips.setText(getString(R.string.please_vote_can_modify));
			} else {
				tvTips.setText(getString(R.string.please_vote_can_not_modify));
			}
			showVote();
		}

	}

	@Override
	public void onVoteSubmitSuccess() {

		if (voteInfo.mode2_modify == 1) {
			showModify();
			tvTips.setText(getString(R.string.submited));
		} else {
			disableVote();
			tvTips.setText(getString(R.string.submited_no_modify));
		}
		super.onVoteSubmitSuccess();

	}

	@Override
	public void onVoteSubmitError() {
		super.onVoteSubmitError();
		showVote();
	}

	@Override
	public void onBack() {
		// TODO Auto-generated method stub
		showResult();
	}

}
