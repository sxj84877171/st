package com.sunvote.xpadapp.fragments;

import java.util.ArrayList;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.MyFragment;
import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadcomm.XPadApiInterface.VoteInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import static com.sunvote.xpadapp.utils.MyStringUtil.replaceBlank;

public class ResultMultiVoteFragment extends MyFragment {

	public byte[] data;
	public BillInfo bill;
	public VoteInfo voteInfo;
	String TAG = "ResultMultiVoteFragment";

	public ArrayList<BillInfo> subInfo;
	String[] options;

	private ArrayList<ListItem> aryContent = null;

	private TextView tvTitle;
	private TextView titleAbstain;
	private TextView titleUnvote;
	private TextView titleResult;

	private ListView listview;
	private MyAdapter mAdapter;
	private int resultBits;

	int fenmu;
	int yindao;
	int shidao;

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_result_multi_vote, container, false);
		final MainActivity mact = (MainActivity) getActivity();

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				;
			}
		});

		listview = (ListView) view.findViewById(R.id.multi_vote_result_listview);
		options = bill.billOptions.split("/");

		initData();

		mAdapter = new MyAdapter(getActivity());
		listview.setAdapter(mAdapter);

		tvTitle = (TextView) view.findViewById(R.id.multi_vote_result_tv_title);
		if (bill != null) {
			tvTitle.setText(  replaceBlank(bill.title));
		}

		titleAbstain = (TextView) view.findViewById(R.id.multi_vote_result_title_abstan);
		titleUnvote = (TextView) view.findViewById(R.id.multi_vote_result_title_unvote);
		titleResult = (TextView) view.findViewById(R.id.multi_vote_result_title_result);

		if (resultBits == 0xffff) {
			titleResult.setVisibility(View.GONE);
		} else {
			titleResult.setVisibility(View.VISIBLE);
		}
		if (options.length == 2) {
			titleAbstain.setVisibility(View.GONE);
		} else {
			titleAbstain.setVisibility(View.VISIBLE);
		}
		TextView tvYindao = (TextView) view.findViewById(R.id.multi_vote_result_yindao);
		TextView tvShidao = (TextView) view.findViewById(R.id.multi_vote_result_shidao);
		if (yindao == 0xff || yindao == 0xffff) {
			tvYindao.setVisibility(View.GONE);
		} else {
			tvYindao.setText(getString(R.string.yindao) + getString(R.string.maohao) + yindao);
			tvYindao.setVisibility(View.VISIBLE);
		}

		if (shidao == 0xff || shidao == 0xffff) {
			tvShidao.setVisibility(View.GONE);
		} else {
			tvShidao.setText(getString(R.string.shidao) + getString(R.string.maohao) + shidao);
			tvShidao.setVisibility(View.VISIBLE);
		}

		return view;
	}

	private void initData() {

		int bitNum = data[2] & 0xf;
		int digiters = data[2] >> 4 & 0xf;
		int voteNo = data[3] & 0xff;
		int startNo = ((data[4] << 8) | data[5]) & 0xffff;
		int endNo = ((data[6] << 8) | data[7]) & 0xff;
		int rH = data[8] & 0xff;
		int rL = data[9] & 0xff;
		resultBits = rH << 8 | rL;

		// resultBits = ((data[8] << 8) | data[9]) & 0xff;

		// Log.d(TAG, "data len:" + data.length);
		// Toast.makeText(mMainActivity, "data len:" + data.length,
		// Toast.LENGTH_LONG).show();
		subInfo = mMainActivity.dbm.getSubBillItems(mMainActivity.meetingId, bill.billId);
		aryContent = new ArrayList<ListItem>();

		if (bitNum == 0) {
			fenmu = data[10] & 0xff;
			yindao = data[11] & 0xff;
			shidao = data[12] & 0xff;
			int pos = 13;
			for (int i = startNo; i <= endNo; i++) {
				ListItem it = new ListItem();
				it.No = i;
				it.title = getTitle(i);
				it.agree = data[pos++] & 0xff;
				it.oppose = data[pos++] & 0xff;
				if (pos == data.length - 1) {
					break;
				}
				if (options.length == 3) {
					it.abstain = data[pos++] & 0xff;
				}
				if (pos == data.length - 1) {
					break;
				}
				it.unvote = data[pos++] & 0xff;
				if (resultBits == 0xffff) {
					it.pass = 0xffff;
				} else {
					int moveBits = 15 - (i - startNo);
					it.pass = (resultBits >> moveBits) & 1;
				}
				aryContent.add(it);
			}
		} else {
			fenmu = (data[10] << 8 | data[11]) & 0xffff;
			yindao = (data[12] << 8 | data[13]) & 0xffff;
			shidao = (data[14] << 8 | data[15]) & 0xffff;
			int pos = 16;
			for (int i = startNo; i <= endNo; i++) {
				ListItem it = new ListItem();
				it.No = i;
				it.title = getTitle(i);
				it.agree = (data[pos++] << 8 | data[pos++]) & 0xffff;
				it.oppose = (data[pos++] << 8 | data[pos++]) & 0xffff;
				if (options.length == 3) {
					it.abstain = (data[pos++] << 8 | data[pos++]) & 0xffff;
				}
				it.unvote = (data[pos++] << 8 | data[pos++]) & 0xffff;
				if (resultBits == 0xffff) {
					it.pass = 0xffff;
				} else {
					int moveBits = 15 - (i - startNo);
					it.pass = (resultBits >> moveBits) & 1;
				}
				aryContent.add(it);
			}
		}

	}

	private String getTitle(int billNo) {
		for (int i = 0; i < subInfo.size(); i++) {
			BillInfo subItem = subInfo.get(i);
			if (subItem.billNo == billNo) {
				return subItem.title;
			}
		}
		return "";
	}

	public class ListItem {
		int No;
		String title;
		int agree;
		int oppose;
		int abstain;
		int unvote;
		int pass;
	}

	/*
	 * MyAdapter
	 * 
	 */
	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

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
				convertView = mInflater.inflate(R.layout.list_multi_vote_result_item, null);
				holder = new ViewHolder();

				holder.tvNum = (TextView) convertView.findViewById(R.id.list_multi_result_item_num);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.list_multi_result_item_name);
				holder.tvAgree = (TextView) convertView.findViewById(R.id.list_multi_result_item_agree);
				holder.tvOppose = (TextView) convertView.findViewById(R.id.list_multi_result_item_oppose);
				holder.tvAbstain = (TextView) convertView.findViewById(R.id.list_multi_result_item_abstan);
				holder.tvUnvote = (TextView) convertView.findViewById(R.id.list_multi_result_item_unvote);
				holder.tvResult = (TextView) convertView.findViewById(R.id.list_multi_result_item_result);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}



			ListItem it = aryContent.get(position);
			holder.tvNum.setText(String.valueOf(it.No));
			holder.tvTitle.setText(it.title);


			holder.tvOppose.setText(String.valueOf(it.oppose));
			holder.tvAbstain.setText(String.valueOf(it.abstain));
			if(it.agree != 0xff && it.agree != 0xffff){
				holder.tvAgree.setVisibility(View.VISIBLE);
				holder.tvAgree.setText(String.valueOf(it.agree));
			}else{
				holder.tvAgree.setVisibility(View.INVISIBLE);
				holder.tvAgree.setText("");
			}
			if(it.oppose != 0xff && it.oppose != 0xffff){
				holder.tvOppose.setVisibility(View.VISIBLE);
				holder.tvOppose.setText(String.valueOf(it.oppose));
			}else{
				holder.tvOppose.setVisibility(View.INVISIBLE);
				holder.tvOppose.setText("");
			}
			if(it.abstain != 0xff && it.abstain != 0xffff){
				holder.tvAbstain.setVisibility(View.VISIBLE);
				holder.tvAbstain.setText(String.valueOf(it.abstain));
			}else{
				holder.tvAbstain.setVisibility(View.INVISIBLE);
				holder.tvAbstain.setText("");
			}

			if (it.unvote != 0xff && it.unvote != 0xffff) {
				titleUnvote.setVisibility(View.VISIBLE);
				holder.tvUnvote.setVisibility(View.VISIBLE);
				holder.tvUnvote.setText(String.valueOf(it.unvote));
			} else {
				titleUnvote.setVisibility(View.INVISIBLE);
				holder.tvUnvote.setVisibility(View.INVISIBLE);
				holder.tvUnvote.setText("");

			}

			String strPass = "";

			if (it.pass == 0) {
				strPass = getString(R.string.no_pass);
			} else if (it.pass == 1) {
				strPass = getString(R.string.pass);
			}

			holder.tvResult.setText(strPass);
			if (it.pass == 0xffff) {
				holder.tvResult.setVisibility(View.GONE);
			} else {
				holder.tvResult.setVisibility(View.VISIBLE);
			}

			if (options.length == 2) {
				holder.tvAbstain.setVisibility(View.GONE);

			} else {
				holder.tvAbstain.setVisibility(View.VISIBLE);
			}

			return convertView;
		}


		public final class ViewHolder {
			public TextView tvNum;
			public TextView tvTitle;
			public TextView tvAgree;
			public TextView tvOppose;
			public TextView tvAbstain;
			public TextView tvUnvote;
			public TextView tvResult;
		}
	}

}
