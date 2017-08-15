package com.sunvote.xpadapp.fragments;

import java.util.ArrayList;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadapp.db.modal.MultiTitleItem;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ResultPingshengFragment extends Fragment {
	public BillInfo bill;
	public ArrayList<MultiTitleItem> subInfo;
	public String resultStr;
	String[] options;

	private ArrayList<ListItem> aryContent = null;

	private TextView tvTitle;
	private ListView listview;
	private MyAdapter mAdapter;

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("ResultPingshengFragment", "onCreateView");
		View view = inflater.inflate(R.layout.fragment_result_pingsheng, container, false);
		final MainActivity mact = (MainActivity) getActivity();

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				;
			}
		});
		
		ImageButton btnBack = (ImageButton) view.findViewById(R.id.pingsheng_result_btnback);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction tx = fm.beginTransaction();
				tx.remove(ResultPingshengFragment.this);
				tx.commit();
				//mact.resultFragment = null;
			}
		});
		listview = (ListView) view.findViewById(R.id.pingsheng_result_listview);

		initData();

		mAdapter = new MyAdapter(getActivity());
		listview.setAdapter(mAdapter);

		tvTitle = (TextView) view.findViewById(R.id.pingsheng_result_tv_title);
		tvTitle.setText(bill.title);

		return view;
	}

	 


	private void initData() {
		aryContent = new ArrayList<ListItem>();

		String[] results = resultStr.split(";");

		for (int i = 0; i < subInfo.size(); i++) {
			MultiTitleItem subItem = subInfo.get(i);
			String[] ary = results[i].split(",");

			ListItem it = new ListItem();
			it.num = "" + subItem.No;
			it.name = subItem.title;

			it.agree = ary[0];
			it.oppose = ary[1];
			it.abstan = ary[2];


			aryContent.add(it);
		}
	}

	public class ListItem {
		public String num;
		public String name;
		public String xianren;
		public String niren;
		public String nimian;
		public String agree;
		public String oppose;
		public String abstan;
		public String result;
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
				convertView = mInflater.inflate(R.layout.list_pingsheng_result_item, null);
				holder = new ViewHolder();

				holder.tvNum = (TextView) convertView.findViewById(R.id.list_result_item_num);
				holder.tvName = (TextView) convertView.findViewById(R.id.list_result_item_name);
				holder.tvXianren = (TextView) convertView.findViewById(R.id.list_result_item_xianren);
				holder.tvNiren = (TextView) convertView.findViewById(R.id.list_result_item_niren);
				holder.tvNimain = (TextView) convertView.findViewById(R.id.list_result_item_nimian);
				// ItemButton
				holder.tvAgree = (TextView) convertView.findViewById(R.id.list_result_item_agree);
				holder.tvOppose = (TextView) convertView.findViewById(R.id.list_result_item_oppose);
				holder.tvAbstain = (TextView) convertView.findViewById(R.id.list_result_item_abstan);
				holder.tvResult = (TextView) convertView.findViewById(R.id.list_result_item_result);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}



			ListItem it = aryContent.get(position);
			holder.tvNum.setText(it.num);
			holder.tvName.setText(it.name);
			holder.tvXianren.setText(it.xianren);
			holder.tvNiren.setText(it.niren);
			holder.tvNimain.setText(it.nimian);
			holder.tvAgree.setText(it.agree);
			holder.tvOppose.setText(it.oppose);
			holder.tvAbstain.setText(it.abstan);
			holder.tvResult.setText(it.result);

			return convertView;
		}


		public final class ViewHolder {
			public TextView tvNum;
			public TextView tvName;
			public TextView tvXianren;
			public TextView tvNiren;
			public TextView tvNimain;
			public TextView tvAgree;
			public TextView tvOppose;
			public TextView tvAbstain;
			public TextView tvResult;
		}
	}

}
