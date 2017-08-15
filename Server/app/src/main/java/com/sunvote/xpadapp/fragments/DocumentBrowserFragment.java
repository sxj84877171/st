package com.sunvote.xpadapp.fragments;

import java.util.ArrayList;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.db.modal.BillInfo;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnScrollChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class DocumentBrowserFragment extends BaseFragment {

	
	private String TAG = "DocumentBrowserFragment";
	private ArrayList<BillInfo> aryContent = null;
	BillInfo bill;
	private TextView tvTitle;
	private ListView listview;
	private TextView tvInfo;
	private TextView tvPage;
	private Button btnModify;

	

	 
	private int currIndex;
	private MyAdapter mAdapter;
	private int page = 0;
	private int totalPage;
	private int votedCount;

	private int modifyable;
	private int secret;
	private int less;//
	private String[] options;
	private boolean isShowBack;
	private static final int pageSize = 1;
	public String title;

	
	public void setInfo( ArrayList<BillInfo> docList,boolean showBack) {
		isShowBack = showBack;
		aryContent = docList;
		totalPage = aryContent.size() / pageSize;
		if (aryContent.size() % pageSize > 0) {
			totalPage++;
		}

	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_document_browser, container, false);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				;//���ε�����ϲ�fragment
			}
		});
		final MainActivity mact = (MainActivity) getActivity();

		ImageButton btnBack = (ImageButton)view.findViewById(R.id.document_browser_btnback);
		if(isShowBack){
			btnBack.setVisibility(View.VISIBLE);
		}
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goBack();
			}
		});
		
		listview = (ListView) view.findViewById(R.id.document_browser_listview);
		listview.setFastScrollAlwaysVisible(false);
		mAdapter = new MyAdapter(getActivity());
		listview.setAdapter(mAdapter);
		listview.setFastScrollAlwaysVisible(false);
		tvTitle = (TextView) view.findViewById(R.id.document_browser_title);
		
		if(title!=null){
			tvTitle.setText(title);
		}else{
			tvTitle.setText(getString(R.string.doc_title));
		}

		Button btnPageUp = (Button) view.findViewById(R.id.document_browser_pageup);
		Button btnPageDown = (Button) view.findViewById(R.id.document_browser_pagedown);

		btnPageUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page >= 1) {
					page--;
					Log.d(TAG, "page:"+page);
					listview.setSelection(page  * pageSize);
					//tvPage.setText(page + "/" + totalPage);
				}
			}
		});

		btnPageDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page < totalPage) {
					page++;
					Log.d(TAG, "page:"+page);
					listview.setSelection((page) * pageSize);
					if(page == totalPage){
						page--;
					}
				}

			}
		});
		
		

		 

		listview.setOnScrollChangeListener(new OnScrollChangeListener() {

			@Override
			public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
 

			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "onItemClick:" + position);
				BillInfo it = aryContent.get(position);
				showDetail(it);
			}

		});
		
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
	
	private void goBack() {
		 
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out); 
		tx.remove(DocumentBrowserFragment.this);
		tx.commit();

	}
 
	private void showDetail(BillInfo item) {
		
		if(item.billType == 20){
			ArrayList<BillInfo> list = mMainActivity.dbm.getSubBillItems(mMainActivity.meetingId,item.billId);
			FragmentManager fm = getFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			DocumentBrowserFragment fDetail = new DocumentBrowserFragment();
			fDetail.setInfo(list,true);
			//tx.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out); 
			fDetail.title = item.title;
			tx.add(R.id.frame_content, fDetail, "fDetail");
			tx.addToBackStack(null);
			tx.commit();
			return;
		}
		
		if(item.billFile == null || item.billFile.length()==0){
			return;
		}
		
		
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		MultiContentDetailFragment fDetail = new MultiContentDetailFragment();
		fDetail.setInfo(item, null,true);
		tx.add(R.id.frame_content, fDetail, "fDetail");
		tx.addToBackStack(null);
		tx.commit();
	}
	

	/*
	 * MyAdapter
	 * 
	 */
	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater; // �õ�һ��LayoutInfalter�����������벼��

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
				convertView = mInflater.inflate(R.layout.list_document_list_item, null);
				holder = new ViewHolder();
				/* �õ������ؼ��Ķ��� */
				holder.tvNum = (TextView) convertView.findViewById(R.id.list_document_item_num);
				holder.tvContent = (TextView) convertView.findViewById(R.id.list_document_item_content);
				holder.tvDetail = (TextView) convertView.findViewById(R.id.list_document_item_detail);
				convertView.setTag(holder); // ��ViewHolder����
			} else {
				holder = (ViewHolder) convertView.getTag(); // ȡ��ViewHolder����
			}

			/* ����TextView��ʾ�����ݣ������Ǵ���ڶ�̬�����е����� */

			BillInfo it = aryContent.get(position);
			holder.tvNum.setText(String.valueOf(it.billNo));
			holder.tvContent.setText(it.title);
			if (it.billFile!=null && it.billFile.length() > 0 || it.billType == 20) {
				holder.tvDetail.setVisibility(View.VISIBLE);
			} else {
				holder.tvDetail.setVisibility(View.INVISIBLE);
			}

			 

			return convertView;
		}

		

		/* ��ſؼ� ��ViewHolder */
		public final class ViewHolder {
			public TextView tvNum;
			public TextView tvContent;
			public TextView tvDetail;
		}
	}


	 

	
}
