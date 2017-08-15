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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ResultElectionFragment extends Fragment {

	private String TAG = "ResultElectionFragment";
	public String[] aryContent;
	public BillInfo bill;
	public ArrayList<MultiTitleItem> subInfo;
	public ArrayList<ResultItem> aryList;
	public String resultStr;

	public String[] options;

	private TextView tvTitle;
	private ListView listview;
	private MyAdapter mAdapter;

	private int paiminType;

	public class ResultItem {
		public String name;
		public int piaoshu;// 选举结果票数
		public int paimin;// 排名
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_result_election, container, false);
		final MainActivity mact = (MainActivity) getActivity();

		ImageButton btnBack = (ImageButton) view.findViewById(R.id.fragment_result_election_btn_back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction tx = fm.beginTransaction();
				tx.remove(ResultElectionFragment.this);
				tx.commit();
				//mact.resultFragment = null;
			}
		});

		listview = (ListView) view.findViewById(R.id.fragment_result_election_list);

		aryContent = resultStr.split(";");
		if (aryContent.length > 0) {
			paiminType = aryContent[0].equals("0") ? 0 : 1;
		}
		aryList = new ArrayList<ResultItem>();
		for (int i = 1; i < aryContent.length; i++) {
			ResultItem tempItem = new ResultItem();
			if (i < subInfo.size()) {
				MultiTitleItem it = subInfo.get(i-1);
				tempItem.name = it.title;
				tempItem.piaoshu = Integer.parseInt(aryContent[i]);
			} else {
				String[] aryOther = aryContent[i].split(",");
				if (aryOther.length > 1) {
					tempItem.name = aryOther[0];
					tempItem.piaoshu = Integer.parseInt(aryOther[1]);
				}
			}
			aryList.add(tempItem);
		}

		calcPaimin();
		
		mAdapter = new MyAdapter(getActivity());
		listview.setAdapter(mAdapter);

		tvTitle = (TextView) view.findViewById(R.id.fragment_result_election_bill_title);
		tvTitle.setText(bill.title);

		return view;
	}
	
	public void calcPaimin(){
		for(int i=0;i<aryList.size();i++){
			for(int j=i;j<aryList.size();j++){
				ResultItem it = aryList.get(i);
				ResultItem it1 = aryList.get(j);
				if(it1.piaoshu>it.piaoshu){
					aryList.remove(j);
					aryList.add(i, it1);
				}
			}
		}
		
		int paimin = 1;
		int binlie = 0;
		for(int i=0;i<aryList.size();i++){
			ResultItem it = aryList.get(i);
			if( i>0 ){
				ResultItem pit = aryList.get(i-1);
				if(it.piaoshu == pit.piaoshu){
					binlie++;
				}else if(it.piaoshu < pit.piaoshu){
					
					paimin++;
					if(paiminType==1){
						paimin+= binlie;
					}
					binlie=0;
				}
			}
		
			it.paimin = paimin;
			
		}
		
		
	}

	/*
	  *
	 * 新的选举结果格式如 0;2;2;3;4;5;张三,3 0代表排名方式按1123,如传1代表排名方式1134，就是排名递增
	 * 从第二个数字开始为候选人名单表格里面的按ID排序的候选人的赞成票数 另选他人的就接在候选人后面
	 * 
	 * 
	 * 
	 * 
	 */

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
			return aryList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return aryList.get(position);
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
				convertView = mInflater.inflate(R.layout.list_result_election_item, null);
				holder = new ViewHolder();

				holder.tvNum = (TextView) convertView.findViewById(R.id.item_result_election_num);
				holder.tvName = (TextView) convertView.findViewById(R.id.item_result_election_name);
				holder.tvVote = (TextView) convertView.findViewById(R.id.item_result_election_agree);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}



			ResultItem it = aryList.get(position);

			holder.tvNum.setText(String.valueOf(it.paimin));
			holder.tvName.setText(it.name);
			holder.tvVote.setText(String.valueOf(it.piaoshu));

			return convertView;
		}


		public final class ViewHolder {
			public TextView tvNum;
			public TextView tvName;
			public TextView tvVote;

		}
	}

}
