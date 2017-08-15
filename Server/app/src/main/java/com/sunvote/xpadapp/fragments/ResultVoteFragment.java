package com.sunvote.xpadapp.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadapp.db.modal.MultiTitleItem;
import com.sunvote.xpadcomm.XPadApiInterface.VoteInfo;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultVoteFragment extends BaseFragment {

	public BillInfo bill;
	public ArrayList<MultiTitleItem> subInfo;
	public VoteInfo voteInfo;
	public String resultStr;
	String[] options;

	String TAG = "ResultVoteFragment";

	private int page = 0;

	TextView tvBillTitle;
	TextView tvSubTitle;
	TextView tvOption1;
	TextView tvValue1;
	TextView tvOption2;
	TextView tvValue2;
	TextView tvOption3;
	TextView tvValue3;
	TextView tvOption4;
	TextView tvValue4;
	TextView tvMemo;
	TextView tvPage;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_result_vote, container, false);
		final MainActivity mact = (MainActivity) getActivity();

		tvBillTitle = (TextView) view.findViewById(R.id.fragment_result_bill_title);
		tvSubTitle = (TextView) view.findViewById(R.id.fragment_result_sub_title);
		tvOption1 = (TextView) view.findViewById(R.id.fragment_result_option1);
		tvValue1 = (TextView) view.findViewById(R.id.fragment_result_value1);
		tvOption2 = (TextView) view.findViewById(R.id.fragment_result_option2);
		tvValue2 = (TextView) view.findViewById(R.id.fragment_result_value2);
		tvOption3 = (TextView) view.findViewById(R.id.fragment_result_option3);
		tvValue3 = (TextView) view.findViewById(R.id.fragment_result_value3);
		tvOption4 = (TextView) view.findViewById(R.id.fragment_result_option4);
		tvValue4 = (TextView) view.findViewById(R.id.fragment_result_value4);

		tvMemo = (TextView) view.findViewById(R.id.fragment_result_memo);

		RelativeLayout pagePanel = (RelativeLayout) view.findViewById(R.id.fragment_result_page_panel);

		tvPage = (TextView) view.findViewById(R.id.fragment_result_page);

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				;
			}
		});

		Button btnPageUp = (Button) view.findViewById(R.id.fragment_result_pageup);
		Button btnPageDown = (Button) view.findViewById(R.id.fragment_result_pagedown);
		btnPageUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (page > 0) {
					page--;
					showResultByPage();
					tvPage.setText(String.format("%d/%d", page + 1, subInfo.size()));
				}

			}
		});

		btnPageDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (page < subInfo.size() - 1) {
					page++;
					showResultByPage();
					tvPage.setText(String.format("%d/%d", page + 1, subInfo.size()));
				}

			}
		});

		ImageButton btnBack = (ImageButton) view.findViewById(R.id.fragment_result_vote_btn_back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction tx = fm.beginTransaction();
				tx.remove(ResultVoteFragment.this);
				tx.commit();
				// mact.resultFragment = null;
			}
		});

		pagePanel.setVisibility(View.INVISIBLE);
		// if (bill != null && bill.title != null) {
		// //tvBillTitle.setText(bill.title);
		// }
	//	tvBillTitle.setText(getString(R.string.ceping_result));

		int pass = voteInfo.resultInfo.bits & 0xF;
		int xiaoShuWei = (voteInfo.resultInfo.bits >> 4) & 0xF;
		int fenmu = voteInfo.resultInfo.num0;
		Log.d(TAG, "xiaoshu:" + xiaoShuWei + "  fenmu:" + fenmu);
		if (voteInfo.resultInfo.resultType == 0) {// ???
			tvBillTitle.setText(getString(R.string.signin_result));
			// tvSubTitle.setText("???????");
			tvOption1.setText(getString(R.string.yindao) + getString(R.string.maohao));
			tvValue1.setText(String.valueOf(voteInfo.resultInfo.num2) + getString(R.string.ren));
			tvOption2.setText(getString(R.string.shidao) + getString(R.string.maohao));
			tvValue2.setText(String.valueOf(voteInfo.resultInfo.num3) + getString(R.string.ren));
			tvOption3.setText(getString(R.string.weidao) + getString(R.string.maohao));
			tvValue3.setText(String.valueOf(voteInfo.resultInfo.num1) + getString(R.string.ren));

			if (pass == 0xf) {
				tvMemo.setText("");
			} else if (pass == 0) {
				tvMemo.setText(getString(R.string.bufuherenshu));
			} else {
				tvMemo.setText(getString(R.string.fuherenshu));
			}
		} else if (voteInfo.resultInfo.resultType == 1) {
		//	tvBillTitle.setText(getString(R.string.biaojue_result));
			String strYDSD = "";
			if (voteInfo.resultInfo.num1 != 0xffff) {
				strYDSD = strYDSD + getString(R.string.yindao) + ":" + voteInfo.resultInfo.num1;
			}
			if (voteInfo.resultInfo.num2 != 0xffff) {
				strYDSD = strYDSD + "               " + getString(R.string.shidao) + ":" + voteInfo.resultInfo.num2;
			}
			tvBillTitle.setText(strYDSD);

			tvOption1.setText(getString(R.string.agree));
			tvValue1.setText(String.valueOf(voteInfo.resultInfo.num4) + getString(R.string.piao));
			tvOption2.setText(getString(R.string.oppose));
			tvValue2.setText(String.valueOf(voteInfo.resultInfo.num5) + getString(R.string.piao));
			tvOption3.setText(getString(R.string.weian));
			tvValue3.setText(String.valueOf(voteInfo.resultInfo.num3) + getString(R.string.piao));

			if (fenmu > 0 && fenmu != 0xffff) {

				double pzc = voteInfo.resultInfo.num4 * 1.0 / fenmu * 100;
				String szc = formatDoubleToString(pzc, xiaoShuWei, false);

				double pfd = voteInfo.resultInfo.num5 * 1.0 / fenmu * 100;
				String sfd = formatDoubleToString(pfd, xiaoShuWei, false);

				double pwa = voteInfo.resultInfo.num3 * 1.0 / fenmu * 100;
				String swa = formatDoubleToString(pwa, xiaoShuWei, false);

				tvValue1.setText(String.valueOf(voteInfo.resultInfo.num4) + " (" + szc + "%)");
				tvValue2.setText(String.valueOf(voteInfo.resultInfo.num5) + " (" + sfd + "%)");
				tvValue3.setText(String.valueOf(voteInfo.resultInfo.num3) + " (" + swa + "%)");
			}

			if (voteInfo.resultInfo.num4 == 0xffff) {
				tvOption1.setText("");
				tvValue1.setText(String.valueOf(""));
			}
			if (voteInfo.resultInfo.num5 == 0xffff) {
				tvOption2.setText("");
				tvValue2.setText(String.valueOf(""));
			}
			if (voteInfo.resultInfo.num3 == 0xffff) {
				tvOption3.setText("");
				tvValue3.setText(String.valueOf(""));
			}

			if (pass == 0xf) {
				tvMemo.setText("");
			} else if (pass == 0) {
				tvMemo.setText(getString(R.string.biaojue_result)+getString(R.string.maohao)+getString(R.string.no_pass));
			} else {
				tvMemo.setText(getString(R.string.biaojue_result)+getString(R.string.maohao)+getString(R.string.pass));
			}
		} else {

			if (voteInfo.resultInfo.resultType == 2) {
	//			tvBillTitle.setText(getString(R.string.biaojue_result));
				tvOption1.setText(getString(R.string.agree));
				tvOption2.setText(getString(R.string.oppose));
				tvOption3.setText(getString(R.string.abstant));

				if (pass == 0) {
					tvMemo.setText(getString(R.string.biaojue_result)+getString(R.string.maohao)+getString(R.string.no_pass));
				} else {
					tvMemo.setText(getString(R.string.biaojue_result)+getString(R.string.maohao)+getString(R.string.pass));
				}

			} else if (voteInfo.resultInfo.resultType == 4) {
				tvOption1.setText(getString(R.string.manyi));
				tvOption2.setText(getString(R.string.jbmanyi));
				tvOption3.setText(getString(R.string.bumanyi));
				if (pass == 0) {
					tvMemo.setText(getString(R.string.ceping_result)+getString(R.string.maohao)+getString(R.string.manyi));
				} else if (pass == 1) {
					tvMemo.setText(getString(R.string.ceping_result)+getString(R.string.maohao)+getString(R.string.jbmanyi));
				} else {
					tvMemo.setText(getString(R.string.ceping_result)+getString(R.string.maohao)+getString(R.string.bumanyi));
				}
			} else if (voteInfo.resultInfo.resultType == 12) {
				tvOption1.setText(getString(R.string.manyi));
				tvOption2.setText(getString(R.string.bumanyi));
				tvOption3.setText(getString(R.string.fcbumanyi));
				if (pass == 0) {
					tvMemo.setText(getString(R.string.ceping_result)+getString(R.string.maohao)+getString(R.string.manyi));
				} else if (pass == 1) {
					tvMemo.setText(getString(R.string.ceping_result)+getString(R.string.maohao)+getString(R.string.bumanyi));
				} else {
					tvMemo.setText(getString(R.string.ceping_result)+getString(R.string.maohao)+getString(R.string.fcbumanyi));
				}
			}

			if (pass == 0xf) {
				tvMemo.setText("");
			}
			// bg:0,69,134
			String strYDSD = "";
			if (voteInfo.resultInfo.num1 != 0xffff) {
				strYDSD = strYDSD + getString(R.string.yindao) + ":" + voteInfo.resultInfo.num1;
			}
			if (voteInfo.resultInfo.num2 != 0xffff) {
				strYDSD = strYDSD + "               " + getString(R.string.shidao) + ":" + voteInfo.resultInfo.num2;
			}
			tvBillTitle.setText(strYDSD);

			tvOption4.setText(getString(R.string.weian));
			tvValue4.setText(String.valueOf(voteInfo.resultInfo.num3) + getString(R.string.piao));

			tvValue1.setText(String.valueOf(voteInfo.resultInfo.num4) + getString(R.string.piao));
			tvValue2.setText(String.valueOf(voteInfo.resultInfo.num5) + getString(R.string.piao));
			tvValue3.setText(String.valueOf(voteInfo.resultInfo.num6) + getString(R.string.piao));

			if (fenmu > 0 && fenmu != 0xffff) {

				double pzc = voteInfo.resultInfo.num4 * 1.0 / fenmu * 100;
				String strOpt1 = formatDoubleToString(pzc, xiaoShuWei, false);

				double pfd = voteInfo.resultInfo.num5 * 1.0 / fenmu * 100;
				String strOpt2 = formatDoubleToString(pfd, xiaoShuWei, false);

				double opt3 = voteInfo.resultInfo.num6 * 1.0 / fenmu * 100;
				String strOpt3 = formatDoubleToString(opt3, xiaoShuWei, false);

				double opt4 = voteInfo.resultInfo.num3 * 1.0 / fenmu * 100;
				String strOpt4 = formatDoubleToString(opt4, xiaoShuWei, false);

				tvValue1.setText(String.valueOf(voteInfo.resultInfo.num4) + " (" + strOpt1 + "%)");
				tvValue2.setText(String.valueOf(voteInfo.resultInfo.num5) + " (" + strOpt2 + "%)");
				tvValue3.setText(String.valueOf(voteInfo.resultInfo.num6) + " (" + strOpt3 + "%)");
				tvValue4.setText(String.valueOf(voteInfo.resultInfo.num3) + " (" + strOpt4 + "%)");

			}

			if (voteInfo.resultInfo.num4 == 0xffff) {
				tvOption1.setText("");
				tvValue1.setText(String.valueOf(""));
			}
			if (voteInfo.resultInfo.num5 == 0xffff) {
				tvOption2.setText("");
				tvValue2.setText(String.valueOf(""));
			}
			if (voteInfo.resultInfo.num6 == 0xffff) {
				tvOption3.setText("");
				tvValue3.setText(String.valueOf(""));
			}
			if (voteInfo.resultInfo.num3 == 0xffff) {
				tvOption4.setText("");
				tvValue4.setText(String.valueOf(""));
			}

		}

		return view;
	}

	private void showResultByPage() {
		Log.d("ResultVoteFragment", "page:" + page + "  resultStr:" + resultStr);
		if (options != null) {
			if (options.length > 0) {
				tvOption1.setText(options[0]);
			}
			if (options.length > 1) {
				tvOption2.setText(options[1]);
			}
			if (options.length == 2) {
				tvOption3.setVisibility(View.INVISIBLE);
				tvValue3.setVisibility(View.INVISIBLE);
			} else if (options.length > 2) {
				tvOption3.setText(options[2]);
			}
		}
		if (resultStr == null || resultStr.length() == 0) {
			return;
		}

		if (subInfo == null) {
			Log.d(TAG, "subInfo is null");
			MultiTitleItem it = subInfo.get(page);
			tvBillTitle.setText(it.title);
		}

		String[] results = resultStr.split(";");
		Log.d("ResultVoteFragment", "results:" + results[page]);

		String[] ary = results[page].split(",");
		if (ary.length > 0) {
			tvValue1.setText(ary[0]);
		}
		if (ary.length > 1) {
			tvValue2.setText(ary[1]);
		}

		if (ary.length > 2) {
			tvValue3.setText(ary[2]);
		}

		if (ary.length > options.length) {
			if (ary[ary.length - 1].equals("0")) {
				tvMemo.setText(getString(R.string.no_pass));
			} else {
				tvMemo.setText(getString(R.string.pass));
			}
		}

	}

	public static String formatDoubleToString(double value, Integer digits, boolean remove) {

		if (value == 0 || value == 100) {
			DecimalFormat df = new DecimalFormat("0");
			return df.format(value);
		}

		if (digits == null || digits < 0) {
			return String.valueOf(value);
		} else if (digits == 0) {
			DecimalFormat df = new DecimalFormat("0");
			return df.format(value);
		} else {
			String temp = "0";
			if (remove) {
				temp = "#";
			}
			StringBuffer buffer = new StringBuffer("0.");
			for (int i = 0; i < digits; i++) {
				buffer.append(temp);
			}
			DecimalFormat df = new DecimalFormat(buffer.toString());
			return df.format(value);
		}
	}

}
