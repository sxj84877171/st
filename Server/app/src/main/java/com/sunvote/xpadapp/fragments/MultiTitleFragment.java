package com.sunvote.xpadapp.fragments;

import java.util.ArrayList;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadapp.db.modal.MultiTitleItem;
import com.sunvote.xpadapp.fragments.MutilTitleDetailFragment.TitleVoteOnBack;
import com.sunvote.xpadcomm.XPadApi;
import com.sunvote.xpadcomm.XPadApiInterface.VoteInfo;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnScrollChangeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.sunvote.xpadapp.utils.MyStringUtil.replaceBlank;

public class MultiTitleFragment extends BaseFragment implements TitleVoteOnBack {
	private String TAG = "MultiTitleFragment";
	private ArrayList<MultiTitleItem> aryContent = null;
	BillInfo bill;
	private TextView tvTitle;
	private ListView listview;
	private TextView tvInfo;
	private TextView tvPage;
	private Button btnConfirm;
	private Button btnModify;

	MutilTitleDetailFragment fDetail;

	private RelativeLayout bottomLayout;
	private RelativeLayout confirmLayout;
	private TextView tvConfirmText;
	private int currIndex;
	private MyAdapter mAdapter;
	private int page = 1;
	private int totalPage;
	private int votedCount;

	// private int modifyable;
	// private int secret;
	// private int less;// 1 不可缺选
	private String[] options;
	private TextView btnAgreeAll;
	private TextView btnOpposeAll;
	private TextView btnAbstainAll;

	public VoteInfo voteInfo;

	public void setInfo(BillInfo info, VoteInfo vtInfo, ArrayList<MultiTitleItem> subInfo) {
		voteInfo = vtInfo;
		bill = info;
		if (bill.billOptions != null) {
			options = bill.billOptions.split("/");
			for(int i=0;i<options.length;i++){
				options[i] = replaceBlank(options[i]);
			}
		}
		aryContent = subInfo;
		votedCount = 0;
		totalPage = aryContent.size() / 3;
		if (aryContent.size() % 3 > 0) {
			totalPage++;
		}

	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_multi_title, container, false);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				;// 屏蔽点击到上层fragment
			}
		});
		final MainActivity mact = (MainActivity) getActivity();

		listview = (ListView) view.findViewById(R.id.multivote_listview);
		listview.setFastScrollAlwaysVisible(false);
		mAdapter = new MyAdapter(getActivity());
		listview.setAdapter(mAdapter);
		listview.setFastScrollAlwaysVisible(false);
		tvTitle = (TextView) view.findViewById(R.id.multivote_title);
		tvTitle.setText(replaceBlank(bill.title));

		tvInfo = (TextView) view.findViewById(R.id.multivote_info);
		tvInfo.setText("");
		bottomLayout = (RelativeLayout) view.findViewById(R.id.multivote_pannal_bottom);
		confirmLayout = (RelativeLayout) view.findViewById(R.id.multivote_confirm_panel);
		tvConfirmText = (TextView) view.findViewById(R.id.multivote_confirm_text);

		// tvPage = (TextView) view.findViewById(R.id.multivote_page);
		Button btnPageUp = (Button) view.findViewById(R.id.multivote_pageup);
		Button btnPageDown = (Button) view.findViewById(R.id.multivote_pagedown);

		btnPageUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page >= 1) {
					page--;
					listview.setSelection(page * 3);
					// tvPage.setText(page + "/" + totalPage);
				}
			}
		});

		btnPageDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page < totalPage) {
					page++;
					listview.setSelection((page - 1) * 3);
					// tvPage.setText(page + "/" + totalPage);
				}

			}
		});

		btnConfirm = (Button) view.findViewById(R.id.multivote_btn_submit);
		btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				int remain = aryContent.size() - votedCount;
				if (voteInfo.less == 1) {// 不可缺选
					if (remain > 0) {
						Toast.makeText(mMainActivity, "您还有" + remain + "项未表决", Toast.LENGTH_SHORT).show();
						return;
					}
					if (voteInfo.mode2_modify == 1) {
						doSubmitAllOk();
					} else {
						tvConfirmText.setText("投票后不可修改，确定提交吗？");
						confirmLayout.setVisibility(View.VISIBLE);
					}

				} else {
					if (remain > 0) {
						tvConfirmText.setText("您还有" + remain + "项未表决，确定提交吗？");
						confirmLayout.setVisibility(View.VISIBLE);
					} else {
						if (voteInfo.mode2_modify == 1) {
							doSubmitAllOk();
						} else {
							tvConfirmText.setText("投票后不可修改，确定提交吗？");
							confirmLayout.setVisibility(View.VISIBLE);
						}
					}
				}

			}


		});

		btnModify = (Button) view.findViewById(R.id.multivote_btn_modify);
		btnModify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// mMainActivity.presenter.cancelSubmitVoteAllOK();
				showVote(true);
			}
		});

		Button btnConfirmOk = (Button) view.findViewById(R.id.multivote_confirm_btn_ok);
		btnConfirmOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSubmitAllOk();
				confirmLayout.setVisibility(View.GONE);
			}
		});

		Button btnConfirmCancel = (Button) view.findViewById(R.id.multivote_confirm_btn_cancel);
		btnConfirmCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmLayout.setVisibility(View.GONE);
			}
		});

		btnAgreeAll = (TextView) view.findViewById(R.id.multi_title_btn_agree_all);
		btnOpposeAll = (TextView) view.findViewById(R.id.multi_title_btn_oppose_all);
		btnAbstainAll = (TextView) view.findViewById(R.id.multi_title_btn_abstain_all);
		btnAgreeAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectAllWithValue(1);
			}
		});
		btnOpposeAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectAllWithValue(2);
			}
		});
		btnAbstainAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectAllWithValue(3);
			}
		});

		listview.setOnScrollChangeListener(new OnScrollChangeListener() {

			@Override
			public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
				// TODO Auto-generated method stub
				// int index = listview.getFirstVisiblePosition();
				// page = index / 3 + 1;
				// if ((index + 3) >= aryContent.size()) {
				// page = totalPage;
				// }
				// tvPage.setText(page + "/" + totalPage);

			}
		});

		// tvPage.setText(page + "/" + totalPage);
		tvInfo.setText("");
		showVote(false);
		return view;
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onStart");
		super.onPause();
	}

	private void doSubmitAllOk() {
		tvInfo.setText("正在提交...");
		btnConfirm.setEnabled(false);
		mMainActivity.presenter.submitVoteAllOK();
	}

	private void selectAllWithValue(int val) {
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			it.result = val;
			mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, it.No + ":" + val);
		}
		mAdapter.notifyDataSetChanged();
		checkVoted();
	}

	private void hideVote() {
		btnAgreeAll.setVisibility(View.GONE);
		btnOpposeAll.setVisibility(View.GONE);
		btnAbstainAll.setVisibility(View.GONE);
		btnConfirm.setVisibility(View.INVISIBLE);
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			it.startVote = false;
		}
		mAdapter.notifyDataSetChanged();
		if (fDetail != null) {
			fDetail.hideVote();
		}
	}

	private void disableVote() {
		btnAgreeAll.setVisibility(View.INVISIBLE);
		btnOpposeAll.setVisibility(View.INVISIBLE);
		btnAbstainAll.setVisibility(View.INVISIBLE);
		if (fDetail != null) {
			fDetail.disableVote();
		}
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			it.startVote = false;
		}
		mAdapter.notifyDataSetChanged();
		btnConfirm.setEnabled(false);
	}

	private void showVote(boolean isModify) {

		if (options.length > 0 ) {
			btnAgreeAll.setVisibility(View.VISIBLE);
			btnAgreeAll.setText(getString(R.string.all) + options[0]);
		}
		if (options.length > 1 ) {
			btnOpposeAll.setVisibility(View.VISIBLE);
			btnOpposeAll.setText(getString(R.string.all) + options[1]);
		}
		if (options.length == 3) {
			btnAbstainAll.setText(getString(R.string.all) + options[2]);
			btnAbstainAll.setVisibility(View.VISIBLE);
		} else {
			btnAbstainAll.setVisibility(View.INVISIBLE);
		}
		btnConfirm.setEnabled(true);
		btnConfirm.setVisibility(View.VISIBLE);

		btnModify.setVisibility(View.INVISIBLE);
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			if (!isModify) {
				it.result = 0;
			}
			it.startVote = true;
		}
		if (fDetail != null) {
			fDetail.showVote();
		}
		mAdapter.notifyDataSetChanged();
		if (voteInfo.less == 1) {// 迫选
			tvTitle.setText(replaceBlank(bill.title) + "(总共" + aryContent.size() + "项), 不可缺选");
		} else {
			tvTitle.setText(replaceBlank(bill.title) + "(总共" + aryContent.size() + "项)");
		}
		checkVoted();
	}

	@Override
	public void onVoteEvent(VoteInfo info) {
		voteInfo = info;
		if (info.mode == XPadApi.VoteType_BatchVote) {

			showVote(false);
		} else if (info.mode == XPadApi.VoteType_Stop) {
			hideVote();
			btnModify.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onVoteSubmitSuccess() {

		if (fDetail != null) {
			fDetail.onVoteSubmitSuccess();
		}

	}

	@Override
	public void onVoteSubmitAllOkSuccess() {
		tvInfo.setText("已提交");

		hideVote();
		if (voteInfo.mode2_modify == 1) {
			btnModify.setVisibility(View.VISIBLE);
		} else {
			disableVote();
		}
	}

	@Override
	public void onVoteSubmitError() {
		super.onVoteSubmitError();
		showVote(false);
	}

	/*
	 * MyAdapter
	 * 
	 */
	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater; //

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return aryContent.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return aryContent.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub

			return super.isEnabled(position);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			Log.v("BaseAdapterTest", "getView " + position + " " + convertView);

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_multi_title_item, null);
				holder = new ViewHolder();

				holder.tvNum = (TextView) convertView.findViewById(R.id.list_multivote_item_num);
				holder.tvContent = (TextView) convertView.findViewById(R.id.list_multivote_item_content);
				holder.btnVote1 = (TextView) convertView.findViewById(R.id.list_multivote_item_btnvote1);
				holder.btnVote2 = (TextView) convertView.findViewById(R.id.list_multivote_item_btnvote2);// to
				holder.btnVote3 = (TextView) convertView.findViewById(R.id.list_multivote_item_btnvote3);
				holder.ivResult = (TextView) convertView.findViewById(R.id.list_multivote_item_tv_result);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}



			MultiTitleItem it = aryContent.get(position);
			holder.tvNum.setText(String.valueOf(it.No));
			holder.tvContent.setText(it.title);
			if (it.result > 0) {
				holder.ivResult.setVisibility(View.VISIBLE);
				if (voteInfo.mode3_secret == 0) {

					holder.ivResult.setText(options[it.result - 1]);
					holder.ivResult.setBackgroundResource(R.drawable.voted_empty);

				} else {
					holder.ivResult.setText("");
					holder.ivResult.setBackgroundResource(R.drawable.voted);
				}
			} else {
				holder.ivResult.setVisibility(View.INVISIBLE);
			}

			if (it.startVote) {
				holder.btnVote1.setText(options[0]);
				holder.btnVote2.setText(options[1]);
				holder.btnVote1.setVisibility(View.VISIBLE);
				holder.btnVote2.setVisibility(View.VISIBLE);

				if (options.length == 3) {
					holder.btnVote3.setText(options[2]);
					holder.btnVote3.setVisibility(View.VISIBLE);
				} else {
					holder.btnVote3.setVisibility(View.INVISIBLE);
				}

			} else {
				holder.btnVote1.setVisibility(View.INVISIBLE);
				holder.btnVote2.setVisibility(View.INVISIBLE);
				holder.btnVote3.setVisibility(View.INVISIBLE);
			}


			holder.btnVote1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currIndex = position;
					voteWithValue(position, 1);
				}
			});

			holder.btnVote2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currIndex = position;
					voteWithValue(position, 2);
				}
			});
			holder.btnVote3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currIndex = position;
					voteWithValue(position, 3);
				}
			});

			return convertView;
		}

		private void voteWithValue(int pos, int val) {
			MultiTitleItem it = aryContent.get(pos);
			it.result = val;
			mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, it.No + ":" + val);
			mAdapter.notifyDataSetChanged();
			checkVoted();
		}

		private void showDetail(MultiTitleItem item) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			fDetail = new MutilTitleDetailFragment();
			fDetail.setInfo(item, MultiTitleFragment.this);
			fDetail.options = options;
			fDetail.bill = bill;
			tx.add(R.id.frame_content, fDetail, "fDetail");
			tx.addToBackStack(null);
			tx.commit();
		}


		public final class ViewHolder {
			public TextView tvNum;
			public TextView tvContent;
			public TextView btnVote1;
			public TextView btnVote2;
			public TextView btnVote3;
			public TextView ivResult;
		}
	}

	private void checkVoted() {
		votedCount = 0;
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			if (it.result > 0) {
				votedCount++;
			}
		}

	}

	@Override
	public void onBack() {
		mAdapter.notifyDataSetChanged();
		checkVoted();
	}



}
