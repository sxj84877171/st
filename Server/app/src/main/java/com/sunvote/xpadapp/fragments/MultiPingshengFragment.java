package com.sunvote.xpadapp.fragments;

import java.util.ArrayList;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadapp.db.modal.MultiTitleItem;
import com.sunvote.xpadapp.fragments.MultiContentDetailFragment.ContentVoteOnBack;
import com.sunvote.xpadcomm.XPadApi;
import com.sunvote.xpadcomm.XPadApiInterface.BatchVoteInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnScrollChangeListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MultiPingshengFragment extends BaseFragment implements ContentVoteOnBack{
	private String TAG = "MultiPingshengFragment";
	private ArrayList<MultiTitleItem> aryContent = null;
	BillInfo bill;
	private TextView tvTitle;
	private ListView listview;
	private TextView tvInfo;
	private TextView tvPage;
	private Button btnConfirm;
	private Button btnModify;

	private TextView btnAgreeAll;
	private TextView btnOpposeAll;
	private TextView btnAbstainAll;

	MultiContentDetailFragment fDetail;

	private RelativeLayout bottomLayout;
	private RelativeLayout confirmLayout;
	private TextView tvConfirmText;

	private int currIndex;
	private MyAdapter mAdapter;
	private int page = 1;
	private int totalPage;
	private int votedCount;

	private int modifyable;
	private int secret;
	private int less;
	private String[] options;
	private String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	private boolean isStartVote;

	public void setInfo(BillInfo info, ArrayList<MultiTitleItem> subInfo) {

		bill = info;
		options = bill.billOptions.split("/");
		aryContent = subInfo;
		isStartVote = false;
		totalPage = aryContent.size() / 2;
		if (aryContent.size() % 2 > 0) {
			totalPage++;
		}

	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_multi_pingsheng, container, false);
		final MainActivity mact = (MainActivity) getActivity();

		listview = (ListView) view.findViewById(R.id.multi_pingsheng_listview);

		mAdapter = new MyAdapter(getActivity());
		listview.setAdapter(mAdapter);

		tvTitle = (TextView) view.findViewById(R.id.multi_pingsheng_title);


		tvInfo = (TextView) view.findViewById(R.id.multi_pingsheng_info);

		bottomLayout = (RelativeLayout) view.findViewById(R.id.multi_pingsheng_pannal_bottom);
		confirmLayout = (RelativeLayout) view.findViewById(R.id.multi_pingsheng_confirm_panel);
		tvConfirmText = (TextView) view.findViewById(R.id.multi_pingsheng_confirm_text);

		tvPage = (TextView) view.findViewById(R.id.multi_pingsheng_page);
		Button btnPageUp = (Button) view.findViewById(R.id.multi_pingsheng_pageup);
		Button btnPageDown = (Button) view.findViewById(R.id.multi_pingsheng_pagedown);

		btnPageUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page > 1) {
					page--;
					listview.setSelection((page - 1) * 2);
					tvPage.setText(page + "/" + totalPage);
				}
			}
		});

		btnPageDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page < totalPage) {
					page++;
					listview.setSelection((page - 1) * 2);
					tvPage.setText(page + "/" + totalPage);
				}

			}
		});

		btnAgreeAll = (TextView) view.findViewById(R.id.multi_pingsheng_btn_agree_all);
		btnAgreeAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < aryContent.size(); i++) {
					MultiTitleItem it = aryContent.get(i);
					it.result = 1;
					mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, it.No + ":" + it.result);
				}
				checkVoted();
				mAdapter.notifyDataSetChanged();
			}
		});

		btnOpposeAll = (TextView) view.findViewById(R.id.multi_pingsheng_btn_oppose_all);
		btnOpposeAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < aryContent.size(); i++) {
					MultiTitleItem it = aryContent.get(i);
					it.result = 2;
					mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, it.No + ":" + it.result);
				}
				checkVoted();
				mAdapter.notifyDataSetChanged();
			}
		});


		btnAbstainAll = (TextView) view.findViewById(R.id.multi_pingsheng_btn_abstain_all);
		btnAbstainAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < aryContent.size(); i++) {
					MultiTitleItem it = aryContent.get(i);
					it.result = 3;
					mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, it.No + ":" + it.result);
				}
				checkVoted();
				mAdapter.notifyDataSetChanged();
			}
		});

		btnConfirm = (Button) view.findViewById(R.id.multi_pingsheng_btn_submit);
		btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int remain = aryContent.size() - votedCount;
				if (less == 1) {// 不可缺选
					if (remain > 0) {
						Toast.makeText(mMainActivity, "您还有" + remain + "项未表决", Toast.LENGTH_SHORT).show();
						return;
					}
					tvInfo.setText("正在提交...");
					mMainActivity.presenter.submitVoteAllOK();
				} else {
					if (remain > 0) {
						tvConfirmText.setText("您还有" + remain + "项未表决，确定提交吗？");
						confirmLayout.setVisibility(View.VISIBLE);
					} else {
						tvInfo.setText("正在提交...");
						mMainActivity.presenter.submitVoteAllOK();
					}
				}
			}
		});
		Button btnConfirmOk = (Button) view.findViewById(R.id.multi_pingsheng_confirm_btn_ok);
		btnConfirmOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	//			tvInfo.setText("正在提交...");
				mMainActivity.presenter.submitVoteAllOK();
				confirmLayout.setVisibility(View.GONE);
			}
		});

		Button btnConfirmCancel = (Button) view.findViewById(R.id.multi_pingsheng_confirm_btn_cancel);
		btnConfirmCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmLayout.setVisibility(View.GONE);
			}
		});

		btnModify = (Button)view.findViewById(R.id.multi_pingsheng_btn_modify);
		btnModify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMainActivity.presenter.cancelSubmitVoteAllOK();
				showVote(true);
			}
		});

		listview.setOnScrollChangeListener(new OnScrollChangeListener() {

			@Override
			public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
				// TODO Auto-generated method stub
				int index = listview.getFirstVisiblePosition();
				page = index / 2 + 1;
				if ((index + 2) >= aryContent.size()) {
					page = totalPage;
				}
				tvPage.setText(page + "/" + totalPage);

			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "onItemClick:" + position);
				MultiTitleItem it = aryContent.get(position);
				mAdapter.showDetail(it);
			}

		});

		tvPage.setText(page + "/" + totalPage);
		tvInfo.setText("");
		hideVote();
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

	private void hideVote() {
		//
		btnConfirm.setVisibility(View.INVISIBLE);
		btnAgreeAll.setVisibility(View.INVISIBLE);
		btnOpposeAll.setVisibility(View.INVISIBLE);
		btnAbstainAll.setVisibility(View.INVISIBLE);
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			it.startVote = false;
		}
		mAdapter.notifyDataSetChanged();
		if (fDetail != null) {
			fDetail.hideVote();
		}
	}

	private void showVote(boolean isModify) {
		btnConfirm.setEnabled(true);
		btnConfirm.setVisibility(View.VISIBLE);

		btnAgreeAll.setVisibility(View.VISIBLE);
		btnOpposeAll.setVisibility(View.VISIBLE);
		btnAbstainAll.setVisibility(View.VISIBLE);

		btnModify.setVisibility(View.INVISIBLE);

		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			it.startVote = true;
			if(!isModify){
				it.result = 0;
			}
		}
		mAdapter.notifyDataSetChanged();
		if (fDetail != null) {
			fDetail.showVote();
		}

	}



	private void disableVote() {
		if (fDetail != null) {
			fDetail.disableVote();
		}
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			it.startVote = false;
		}
		mAdapter.notifyDataSetChanged();
		btnConfirm.setEnabled(false);
		btnAgreeAll.setEnabled(false);
		btnOpposeAll.setEnabled(false);
		btnAbstainAll.setEnabled(false);

	}

	@Override
	public void onVoteEvent(int baseId, int iMode, Object info) {
		if (iMode == XPadApi.VoteType_BatchVote) {
			BatchVoteInfo vt = (BatchVoteInfo) info;
			modifyable = vt.modify;
			secret = vt.secret;
			less = vt.less;
			isStartVote = true;
			showVote(false);
			checkVoted();
			tvInfo.setText("");
		} else if (iMode == XPadApi.VoteType_Stop) {
			hideVote();
			btnModify.setVisibility(View.INVISIBLE);
			isStartVote = false;
		}

	}

	@Override
	public void onVoteSubmitSuccess() {

		if (fDetail != null) {
			fDetail.onVoteSubmitSuccess();
		}
		super.onVoteSubmitSuccess();
	}

	@Override
	public void onVoteSubmitAllOkSuccess() {

		hideVote();
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			it.startVote = false;
			it.submited = true;
		}
		btnModify.setVisibility(View.VISIBLE);
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
				convertView = mInflater.inflate(R.layout.list_multi_pingsheng_item, null);
				holder = new ViewHolder();

				holder.tvNum = (TextView) convertView.findViewById(R.id.list_multipingsheng_item_num);
				holder.tvName = (TextView) convertView.findViewById(R.id.list_multipingsheng_item_name);
				holder.tvXianren = (TextView) convertView.findViewById(R.id.list_multipingsheng_item_xianren);
				holder.tvNiren = (TextView) convertView.findViewById(R.id.list_multipingsheng_item_niren);
				holder.tvNimian = (TextView) convertView.findViewById(R.id.list_multipingsheng_item_nimian);// to
				holder.btnAgree = (TextView) convertView.findViewById(R.id.list_multipingsheng_item_btn_agree);
				holder.btnOppose = (TextView) convertView.findViewById(R.id.list_multipingsheng_item_btn_oppose);
				holder.btnAbstain = (TextView) convertView.findViewById(R.id.list_multipingsheng_item_btn_abstain);

				holder.ivResult = (TextView) convertView.findViewById(R.id.list_multipingsheng_item_tv_result);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}



			MultiTitleItem it = aryContent.get(position);
			holder.tvNum.setText(String.valueOf(it.No));
			holder.tvName.setText(it.title);


			if (it.result > 0) {
				holder.ivResult.setVisibility(View.VISIBLE);
				if (secret == 0) { // 不保密
					if (bill.billType == BillInfo.BillType_Vote) {
						holder.ivResult.setText("");
						holder.ivResult.setBackgroundResource(getResultResourceByTitle(options[it.result - 1]));
					} else if (bill.billType == BillInfo.BillType_Evaluate) {
						holder.ivResult.setText(options[it.result - 1]);
						holder.ivResult.setBackgroundResource(R.drawable.voted_empty);
					}
				} else {
					holder.ivResult.setText("");
					holder.ivResult.setBackgroundResource(R.drawable.voted);
				}
			} else {
				holder.ivResult.setVisibility(View.INVISIBLE);
			}

			if (it.startVote) {
					holder.btnAgree.setVisibility(View.VISIBLE);
					holder.btnOppose.setVisibility(View.VISIBLE);
					holder.btnAbstain.setVisibility(View.VISIBLE);
			} else {
				holder.btnAgree.setVisibility(View.GONE);
				holder.btnOppose.setVisibility(View.GONE);
				holder.btnAbstain.setVisibility(View.GONE);
			}


			holder.btnAgree.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.v("BaseAdapterTest", "agree:" + position);
					currIndex = position;
					MultiTitleItem it = aryContent.get(position);
					it.result = 1;
					mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, it.No + ":" + it.result);
					mAdapter.notifyDataSetChanged();
					checkVoted();
				}
			});


			holder.btnOppose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.v("BaseAdapterTest", "Oppose:" + position);
					currIndex = position;
					MultiTitleItem it = aryContent.get(position);
					it.result = 2;
					mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, it.No + ":" + it.result);
					mAdapter.notifyDataSetChanged();
					checkVoted();
				}
			});

			holder.btnAbstain.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.v("BaseAdapterTest", "Abstain:" + position);
					currIndex = position;
					MultiTitleItem it = aryContent.get(position);
					it.result = 3;
					mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, it.No + ":" + it.result);
					mAdapter.notifyDataSetChanged();
					checkVoted();
				}
			});

			return convertView;
		}

		public void showDetail(MultiTitleItem item) {
//			FragmentManager fm = getFragmentManager();
//			FragmentTransaction tx = fm.beginTransaction();
//			fDetail = new MultiContentDetailFragment(item, MultiPingshengFragment.this, isStartVote);
//			fDetail.options = options;
//			fDetail.bill = bill;
//			fDetail.secret = secret;
//			fDetail.modifyable = modifyable;
//			tx.add(R.id.frame_content, fDetail, "fDetail");
//			tx.addToBackStack(null);
//			tx.commitAllowingStateLoss();
		}


		public final class ViewHolder {
			public TextView tvNum;
			public TextView tvName;
			public TextView tvXianren;
			public TextView tvNiren;
			public TextView tvNimian;
			public TextView btnAgree;
			public TextView btnOppose;
			public TextView btnAbstain;
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
