package com.sunvote.xpadapp.fragments;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.db.modal.BillInfo;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MultiContentDetailFragment extends BaseFragment {
	private String TAG = "MultiContentDetailFr";
	private BillInfo bill;
	private VoteInfo voteInfo;
	// private MultiTitleItem data;
	private TextView tvTitle;
	private TextView tvTips;
	private TextView ivReuslt;
	private Button btnA;
	private Button btnB;
	private Button btnC;
	private WebView webContent;

	private RelativeLayout panelVote;
	private RelativeLayout panelModify;
	private RelativeLayout panelTips;

	public String[] options;
	private boolean showBackBtn;
	// public int modifyable;
	// public int secret;

	private int scale = 0;
	private int originSize = 200;

	private int voteValue;
	private RelativeLayout confirmLayout;
	private TextView tvConfirmText;

	public String[] voteOptions = new String[13];

	public ContentVoteOnBack contentBackListener;
	private String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

	public interface ContentVoteOnBack {
		void onBack();
	}

	public void setInfo(BillInfo billInfo, VoteInfo vote, boolean isShowBack) {
		bill = billInfo;
		voteInfo = vote;
		showBackBtn = isShowBack;
		
	}

	private void initOptions() {

		voteOptions[0] = getString(R.string.zc_fd);
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

		if (voteInfo == null) {
			return;
		}

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
		}
		Log.d(TAG, "options:" + options.toString());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_multi_content_detail, container, false);
		initOptions();
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				;//
			}
		});
		final MainActivity mact = (MainActivity) getActivity();

		tvTitle = (TextView) view.findViewById(R.id.content_detail_title);
		if (bill.title != null) {
			tvTitle.setText(bill.title);
		}

		webContent = (WebView) view.findViewById(R.id.content_detail_webview);

		String filename = "file:///" + DATABASE_PATH + "/sunvote/" + mact.meetingId + "/" + bill.billFile;
		webContent.loadUrl(filename);
		WebSettings mWebSettings = webContent.getSettings();
		webContent.setInitialScale(originSize);
		mWebSettings.setJavaScriptEnabled(true);
		 mWebSettings.setBuiltInZoomControls(true); //
		 mWebSettings.setDisplayZoomControls(false);
		mWebSettings.setSupportZoom(true);

		panelVote = (RelativeLayout) view.findViewById(R.id.content_detail_pannal_vote);
		panelModify = (RelativeLayout) view.findViewById(R.id.content_detail_pannal_modify);
		tvTips = (TextView) view.findViewById(R.id.content_detail_tv_tips);
		ivReuslt = (TextView) view.findViewById(R.id.content_detail_tv_result);
		ivReuslt.setVisibility(View.INVISIBLE);

		btnA = (Button) view.findViewById(R.id.content_detail_btnA);
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

		btnB = (Button) view.findViewById(R.id.content_detail_btnB);
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

		btnC = (Button) view.findViewById(R.id.content_detail_btnC);
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

		ImageButton btnBack = (ImageButton) view.findViewById(R.id.content_detail_btnback);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goBack();
			}
		});

		if (!showBackBtn) {
			btnBack.setVisibility(View.INVISIBLE);
		}

		Button btnModify = (Button) view.findViewById(R.id.content_detail_btn_modify);
		btnModify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				panelModify.setVisibility(View.GONE);
				showVote();
				ivReuslt.setVisibility(View.VISIBLE);
			}
		});

		ImageButton btnBigger = (ImageButton) view.findViewById(R.id.content_detail_bigger);
		btnBigger.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scale++;
				if (scale > 10) {
					scale = 10;
				}
				int newsize = originSize + scale * 20;
				// Log.d(TAG, "old size = " + originSize + " new size = " +
				// newsize);
				// tvContent.setTextSize(newsize);
				webContent.setInitialScale(newsize);

			}
		});

		ImageButton btnSmaller = (ImageButton) view.findViewById(R.id.content_detail_smaller);
		btnSmaller.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				scale--;
				if (scale < 0) {
					scale = 0;
				}

				int newsize = (originSize + scale * 20);
				// Log.d(TAG, "old size = " + originSize + " new size = " +
				// newsize);
				webContent.setInitialScale(newsize);

			}
		});

		tvConfirmText = (TextView) view.findViewById(R.id.content_detail_confirm_textview);
		confirmLayout = (RelativeLayout) view.findViewById(R.id.content_detail_confirm_panel);
		Button btnConfirmOK = (Button) view.findViewById(R.id.content_detail_btn_confirm_ok);
		btnConfirmOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideConfirm();
				doVoteWithIndex(voteValue);
			}
		});

		Button btnConfirmCancel = (Button) view.findViewById(R.id.content_detail_btn_confirm_cancel);
		btnConfirmCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideConfirm();
			}
		});

		if (voteInfo != null) {
			if (options.length == 2) {
				btnA.setText(options[0]);
				btnB.setVisibility(View.INVISIBLE);
				btnC.setText(options[1]);
			} else if (options.length == 3) {
				btnA.setText(options[0]);
				btnB.setText(options[1]);
				btnC.setText(options[2]);
			}
			showVote();
			showResult();
		} else {
			hideVote();
		}

		return view;
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

		tvTips.setText(getString(R.string.submiting));
		bill.voteResult = index;
		mMainActivity.presenter.submitVote(XPadApi.AnsType_Single, String.valueOf(index));
		// mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle,
		// bill.billNo + ":" + index);
		showResult();

	}

	// private int result;

	private void showResult() {
		if (bill.voteResult > 0) {
			if (voteInfo.mode3_secret == 0) {
				ivReuslt.setText(options[bill.voteResult - 1]);
				ivReuslt.setBackgroundResource(R.drawable.voted_empty);
			} else {
				ivReuslt.setText("");
				ivReuslt.setBackgroundResource(R.drawable.voted);
			}
			ivReuslt.setVisibility(View.VISIBLE);
			if (voteInfo.mode2_modify == 1) {
				showModify();
			} else {
				disableVote();
			}
		} else {
			ivReuslt.setVisibility(View.INVISIBLE);
		}

	}

	private void goBack() {
		if (contentBackListener != null) {
			contentBackListener.onBack();
		}
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.remove(MultiContentDetailFragment.this);
		tx.commit();

	}

	public void hideVote() {
		// isStartVote = false;
		panelVote.setVisibility(View.GONE);
		panelModify.setVisibility(View.GONE);
		// btnA.setVisibility(View.INVISIBLE);
		// btnB.setVisibility(View.INVISIBLE);
		// btnC.setVisibility(View.INVISIBLE);
		// ivReuslt.setVisibility(View.INVISIBLE);
		// tvTips.setVisibility(View.INVISIBLE);
	}

	public void showVote() {
		// isStartVote = true;
		panelVote.setVisibility(View.VISIBLE);

		// btnA.setVisibility(View.VISIBLE);
		// btnB.setVisibility(View.VISIBLE);
		// btnC.setVisibility(View.VISIBLE);
		// ivReuslt.setVisibility(View.VISIBLE);
		// tvTips.setVisibility(View.VISIBLE);
	}

	public void disableVote() {
		btnA.setEnabled(false);
		btnB.setEnabled(false);
		btnC.setEnabled(false);
	}

	private void showModify() {
		panelModify.setVisibility(View.VISIBLE);

	}

	public void onVoteSubmitSuccess() {
		tvTips.setText(getString(R.string.submited));
		// goBack();
	}

	@Override
	public void onVoteSubmitError() {
		super.onVoteSubmitError();
		showVote();
	}
}
