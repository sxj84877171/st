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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MultiContentFragment extends BaseFragment implements ContentVoteOnBack {
	private String TAG = "MultiContentFragment";
	private ArrayList<MultiTitleItem> aryContent = null;
	BillInfo bill;
	private TextView tvTitle;
	private ListView listview;
	private TextView tvInfo;
	private TextView tvPage;
	private Button btnConfirm;
	private Button btnModify;

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
		View view = inflater.inflate(R.layout.fragment_multi_content, container, false);
		
		final MainActivity mact = (MainActivity) getActivity();

		listview = (ListView) view.findViewById(R.id.multi_content_listview);
		listview.setFastScrollAlwaysVisible(false);
		mAdapter = new MyAdapter(getActivity());
		listview.setAdapter(mAdapter);

		tvTitle = (TextView) view.findViewById(R.id.multi_content_title);


		tvInfo = (TextView) view.findViewById(R.id.multi_content_info);

		bottomLayout = (RelativeLayout) view.findViewById(R.id.multi_content_pannal_bottom);
		confirmLayout = (RelativeLayout) view.findViewById(R.id.multi_content_confirm_panel);
		tvConfirmText = (TextView) view.findViewById(R.id.multi_content_confirm_text);

		//tvPage = (TextView) view.findViewById(R.id.multi_content_page);
		Button btnPageUp = (Button) view.findViewById(R.id.multi_content_pageup);
		Button btnPageDown = (Button) view.findViewById(R.id.multi_content_pagedown);

		btnPageUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page >= 1) {
					page--;
					listview.setSelection(page  * 2);
				//	tvPage.setText(page + "/" + totalPage);
				}
			}
		});

		btnPageDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page < totalPage) {
					page++;
					listview.setSelection((page - 1) * 2);
					//tvPage.setText(page + "/" + totalPage);
				}

			}
		});

		btnConfirm = (Button) view.findViewById(R.id.multi_content_btn_submit);
		btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int remain = aryContent.size() - votedCount;
				if (less == 1) {
					if (remain > 0) {

						return;
					}

					mMainActivity.presenter.submitVoteAllOK();
				} else {
					if (remain > 0) {

						confirmLayout.setVisibility(View.VISIBLE);
					}else{

						mMainActivity.presenter.submitVoteAllOK();
					}
				}
			}
		});
		
		btnModify = (Button)view.findViewById(R.id.multi_content_btn_modify);
		btnModify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//mMainActivity.presenter.cancelSubmitVoteAllOK();
				showVote(true);
			}
		});
		
		Button btnConfirmOk = (Button) view.findViewById(R.id.multi_content_confirm_btn_ok);
		btnConfirmOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mMainActivity.presenter.submitVoteAllOK();
				confirmLayout.setVisibility(View.GONE);
			}
		});

		Button btnConfirmCancel = (Button) view.findViewById(R.id.multi_content_confirm_btn_cancel);
		btnConfirmCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmLayout.setVisibility(View.GONE);
			}
		});

		listview.setOnScrollChangeListener(new OnScrollChangeListener() {

			@Override
			public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
				// TODO Auto-generated method stub
//				int index = listview.getFirstVisiblePosition();
//				page = index / 2 + 1;
//				if ((index + 2) >= aryContent.size()) {
//					page = totalPage;
//				}
				//tvPage.setText(page + "/" + totalPage);

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

		//tvPage.setText(page + "/" + totalPage);
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
		tvInfo.setText("");
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

	private void showVote(boolean isModify) {
		btnConfirm.setEnabled(true);
		btnConfirm.setVisibility(View.VISIBLE);
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
		if (less == 1) {// 迫选
	//		tvTitle.setText(bill.title + "(总共" + aryContent.size() + "项), 不可缺选");
		} else {
	//		tvTitle.setText(bill.title + "(总共" + aryContent.size() + "项)");
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
		
	}

	@Override
	public void onVoteSubmitAllOkSuccess() {

		
//		for (int i = 0; i < aryContent.size(); i++) {
//			MultiTitleItem it = aryContent.get(i);
//			it.submited = true;
//			
//		}
		hideVote();
		if(modifyable == 1){
			btnModify.setVisibility(View.VISIBLE);
		}else{
			disableVote();
		}
	}

	/*
	 * MyAdapter
	 * 
	 */
	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater; //得到一个LayoutInfalter对象用来导入布局

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
				convertView = mInflater.inflate(R.layout.list_multi_content_item, null);
				holder = new ViewHolder();

				holder.tvNum = (TextView) convertView.findViewById(R.id.list_multicontent_item_num);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.list_multicontent_item_title);
				holder.tvMemo = (TextView) convertView.findViewById(R.id.list_multicontent_item_memo);
				holder.btnVote = (TextView) convertView.findViewById(R.id.list_multicontent_item_btnvote);
				holder.btnModify = (TextView) convertView.findViewById(R.id.list_multicontent_item_btnmodify);// to
																												// ItemButton
				holder.ivResult = (TextView) convertView.findViewById(R.id.list_multicontent_item_tv_result);
				convertView.setTag(holder); // 绑定ViewHolder对象
			} else {
				holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */

			MultiTitleItem it = aryContent.get(position);
			holder.tvNum.setText(String.valueOf(it.No));
			holder.tvTitle.setText(it.title);
			holder.tvMemo.setText(it.content);

			if (it.result > 0) {
				holder.ivResult.setVisibility(View.VISIBLE);
				if (secret == 0) {// 不保密
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
				//if (it.result == 0) {
					holder.btnVote.setVisibility(View.VISIBLE);
					holder.btnModify.setVisibility(View.INVISIBLE);
				//} else {
					//holder.btnVote.setVisibility(View.INVISIBLE);
					//holder.btnModify.setVisibility(View.VISIBLE);
				//}
			} else {
				holder.btnVote.setVisibility(View.INVISIBLE);
				holder.btnModify.setVisibility(View.INVISIBLE);
			}


			holder.btnVote.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					currIndex = position;
					showDetail(aryContent.get(position));
				}
			});

			holder.btnModify.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					currIndex = position;
					showDetail(aryContent.get(position));
				}
			});

			return convertView;
		}

		public void showDetail(MultiTitleItem item) {
//			FragmentManager fm = getFragmentManager();
//			FragmentTransaction tx = fm.beginTransaction();
//			fDetail = new MultiContentDetailFragment(item, MultiContentFragment.this, isStartVote);
//			fDetail.options = options;
//			fDetail.bill = bill;
//			fDetail.secret = secret;
//			fDetail.modifyable = modifyable;
//			tx.add(R.id.frame_content, fDetail, "fDetail");
//			tx.addToBackStack(null);
//			tx.commit();
		}


		public final class ViewHolder {
			public TextView tvNum;
			public TextView tvTitle;
			public TextView tvMemo;
			public TextView btnVote;
			public TextView btnModify;
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
