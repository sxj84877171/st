package com.sunvote.xpadapp.fragments;

import java.util.ArrayList;

import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.base.ZanyEditText;
import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadapp.db.modal.MultiTitleItem;
import com.sunvote.xpadcomm.XPadApi;
import com.sunvote.xpadcomm.XPadApiInterface.BatchSelectInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ElectionFragment extends BaseFragment {
	private String TAG = "ElectionFragment";
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

	private RelativeLayout addOtherLayout;
	private ZanyEditText edtOther;

	private int currIndex;
	private MyAdapter mAdapter;
	private int page = 1;
	private int totalPage;
	
	private int modifyable;
	private int secret;
	private int less;//

	private int otherCount;
	private int votedCount;

	private BatchSelectInfo voteInfo;

	private String[] options;

	public void setInfo(BillInfo info, ArrayList<MultiTitleItem> subInfo) {

		bill = info;
		options = bill.billOptions.split("/");
		aryContent = subInfo;
		
//		for (int i = 0; i < aryContent.size(); i++) {
//			MultiTitleItem it = aryContent.get(i);
//				it.result = 1;
//				 
//		}

		totalPage = aryContent.size() / 6;
		if (aryContent.size() % 6 > 0) {
			totalPage++;
		}

		otherCount = 0;

	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_election, container, false);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				;
			}
		});
		listview = (ListView) view.findViewById(R.id.election_listview);
		listview.setFastScrollAlwaysVisible(false);
		mAdapter = new MyAdapter(getActivity());
		listview.setAdapter(mAdapter);

		tvTitle = (TextView) view.findViewById(R.id.election_title);
		int houxuan = aryContent.size()-1;
		tvTitle.setText(bill.title + "(��ѡ" + houxuan + "��)");

		tvInfo = (TextView) view.findViewById(R.id.election_info);

		bottomLayout = (RelativeLayout) view.findViewById(R.id.election_pannal_bottom);
		addOtherLayout = (RelativeLayout) view.findViewById(R.id.election_add_other_panel);
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(edtOther.isFocused()){
					edtOther.clearFocus();
					InputMethodManager im=(InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
					im.hideSoftInputFromWindow( edtOther.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
		
		

		tvPage = (TextView) view.findViewById(R.id.election_page);
		Button btnPageUp = (Button) view.findViewById(R.id.election_pageup);
		Button btnPageDown = (Button) view.findViewById(R.id.election_pagedown);

		btnPageUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page >= 1) {
					page--;
					listview.setSelection(page * 3);
					tvPage.setText(page + "/" + totalPage);
				}
			}
		});

		btnPageDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page < totalPage) {
					page++;
					listview.setSelection((page - 1) * 3);
					tvPage.setText(page + "/" + totalPage);
				}

			}
		});

		btnConfirm = (Button) view.findViewById(R.id.election_btn_confirm);
		btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (voteInfo.less == 1) { // ��������ѡ
					int selCnt = getSelectCount();
					if (selCnt < voteInfo.select) {
						Toast.makeText(mMainActivity, getString(R.string.need_select) + voteInfo.select + getString(R.string.person_can_submit), Toast.LENGTH_SHORT).show();
						return;
					}
				}
				tvInfo.setText(getString(R.string.submiting));
				new Thread(new Runnable() {
					public void run() {
						for (int i = 0; i < aryContent.size(); i++) {
							MultiTitleItem it = aryContent.get(i);
							if (it.No < 0) {
								int otherNo = it.No * -1;
								mMainActivity.presenter.submitVote(XPadApi.AnsType_SelectOther,
										otherNo + ":00" + it.title);

							}
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mMainActivity.presenter.submitVoteAllOK();
					}
				}).start();

			}
		});
		
		btnModify = (Button)view.findViewById(R.id.election_btn_modify);
		btnModify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showVote();
				
			}
		});

		edtOther = (ZanyEditText) view.findViewById(R.id.election_add_edit);
		edtOther.setOnKeyListener(new OnKeyListener() {                 
		    @Override
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
		        //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
		        if(keyCode == KeyEvent.KEYCODE_DEL) {  
		            //this is for backspace
		        }
		        return false;       
		    }
		});
		
		Button btnAddOk = (Button) view.findViewById(R.id.election_add_edit_btn_ok);
		btnAddOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String strName = edtOther.getText().toString();
				if (strName == null || strName.length() == 0) {
					Toast.makeText(getActivity(), getString(R.string.must_input_name), Toast.LENGTH_SHORT).show();
					return;
				}
				Log.d(TAG, "add other" + edtOther.getText().toString());
				MultiTitleItem it = new MultiTitleItem();
				it.startVote = true;
				it.No = --otherCount;
				it.result = 1;
				it.title = edtOther.getText().toString();
				aryContent.add(aryContent.size() - 1, it);
				mAdapter.notifyDataSetChanged();
				addOtherLayout.setVisibility(View.GONE);
				checkVoted();
				edtOther.setText("");
			}
		});

		Button btnAddCancel = (Button) view.findViewById(R.id.election_add_edit_btn_cancel);
		btnAddCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				edtOther.setText("");
				addOtherLayout.setVisibility(View.GONE);
			}
		});

		// listview.setOnScrollChangeListener(new OnScrollChangeListener() {
		//
		// @Override
		// public void onScrollChange(View v, int scrollX, int scrollY, int
		// oldScrollX, int oldScrollY) {
		// // TODO Auto-generated method stub
		// int index = listview.getFirstVisiblePosition();
		// page = index / 3 + 1;
		// if ((index + 3) >= aryContent.size()) {
		// page = totalPage;
		// }
		// tvPage.setText(page + "/" + totalPage);
		//
		// }
		// });

		tvPage.setText(page + "/" + totalPage);
		tvInfo.setText("");
		try {
			hideVote();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}

	private int getSelectCount() {
		int selCnt = 0;
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			if (it.result == 1) {
				selCnt++;
			}
		}
		return selCnt;
	}

	private void hideVote() throws Exception {
		btnConfirm.setVisibility(View.INVISIBLE);
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			it.startVote = false;
		}
		mAdapter.notifyDataSetChanged();
	}

	private void showVote() {
		btnConfirm.setEnabled(true);
		btnConfirm.setVisibility(View.VISIBLE);
		btnModify.setVisibility(View.INVISIBLE);
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			it.startVote = true;
			it.result = 0;
			if(it.No<0){
				aryContent.remove(it);
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onVoteEvent(int baseId, int iMode, Object info) {
		if (iMode == XPadApi.VoteType_BatchSelect) {
			voteInfo = (BatchSelectInfo) info;
			BatchSelectInfo tmp = voteInfo;
			int houxuan = aryContent.size()-1;
			tvTitle.setText(bill.title + "("+getString(R.string.houxuan) + houxuan +getString(R.string.houxuan2) + voteInfo.select + getString(R.string.houxuan3)+")");
			 
			showVote();
			checkVoted();
			
			
			
		} else if (iMode == XPadApi.VoteType_Stop) {
			try {
				hideVote();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onVoteSubmitSuccess() {

		// if (fDetail != null) {
		// fDetail.onVoteSubmitSuccess();
		// }
		// super.onVoteSubmitSuccess();
	}

	@Override
	public void onVoteSubmitAllOkSuccess() {
		tvInfo.setText(getString(R.string.submited));
		if(modifyable==0){
			btnConfirm.setEnabled(false);
			for (int i = 0; i < aryContent.size(); i++) {
				MultiTitleItem it = aryContent.get(i);
				it.startVote = true;
			}
			mAdapter.notifyDataSetChanged();
		}else{
			btnConfirm.setVisibility(View.INVISIBLE);
		}
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

		private ViewHolder holder;

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			Log.v("BaseAdapterTest", "getView " + position + " " + convertView);
			MultiTitleItem it = aryContent.get(position);
			if (position > 0 && position == aryContent.size() - 1) {
				convertView = mInflater.inflate(R.layout.list_election_item_add, null);
				Button btnAdd = (Button) convertView.findViewById(R.id.list_election_item_add_btn);
				if (it.startVote && voteInfo.other > 0) {
					btnAdd.setEnabled(true);
				} else {
					btnAdd.setEnabled(false);
				}
				btnAdd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d(TAG, "add other");
						if(votedCount>= voteInfo.select ){
							Toast.makeText(getActivity(), getString(R.string.max_select)+voteInfo.select+getString(R.string.person), Toast.LENGTH_SHORT).show();
							return;
						}
						 
						addOtherLayout.setVisibility(View.VISIBLE);
					}
				});
				return convertView;
			}

			if (convertView == null || convertView.findViewById(R.id.list_election_item_add_btn) != null) {
				convertView = mInflater.inflate(R.layout.list_election_item, null);
				holder = new ViewHolder();
				/* �õ������ؼ��Ķ��� */
				holder.tvName = (TextView) convertView.findViewById(R.id.list_election_item_name);
				holder.btnO = (RadioButton) convertView.findViewById(R.id.list_election_item_oo);
				holder.btnX = (RadioButton) convertView.findViewById(R.id.list_election_item_xx);// to
				holder.RG = (RadioGroup) convertView.findViewById(R.id.list_election_item_radio_group);
				convertView.setTag(holder); // ��ViewHolder����
			} else {
				holder = (ViewHolder) convertView.getTag(); // ȡ��ViewHolder����
			}

			/* ����TextView��ʾ�����ݣ������Ǵ���ڶ�̬�����е����� */

			holder.tvName.setText(String.valueOf(it.title));
			if (it.result == 0) {
				holder.RG.clearCheck();
			} else if (it.result == 1) {
				holder.RG.check(R.id.list_election_item_oo);
			} else if (it.result == 2) {
				holder.RG.check(R.id.list_election_item_xx);
			}

			/* ΪButton��ӵ���¼� */
			holder.btnO.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MultiTitleItem it = aryContent.get(position);
					if (it.startVote == false) {
						Toast.makeText(getActivity(),getString(R.string.vote_not_start), Toast.LENGTH_SHORT).show();
						checkVoted();
						return;
					}
					
					 
					 
					if(votedCount>= voteInfo.select ){
						Toast.makeText(getActivity(), getString(R.string.max_select)+voteInfo.select+getString(R.string.person), Toast.LENGTH_SHORT).show();
						checkVoted();
						return;
					}
					 

				//	Log.v("BaseAdapterTest", "������ OO" + position);
					
					currIndex = position;
					it.result = 1;
					checkVoted();
					if (it.No > 0) {
						mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, it.No + ":" + 1);
					}
				}
			});

			holder.btnX.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MultiTitleItem it = aryContent.get(position);
					if (it.startVote == false) {
						Toast.makeText(getActivity(), getString(R.string.vote_not_start), Toast.LENGTH_SHORT).show();
						checkVoted();
						return;
					}

					 
					it.result = 2;
					//Log.v("BaseAdapterTest", "������ XX" + position);
					currIndex = position;
					checkVoted();
					if (it.No > 0) {
						mMainActivity.presenter.submitVote(XPadApi.AnsType_BatchSingle, it.No + ":" + 2);
					}
				}
			});

			return convertView;
		}

		/* ��ſؼ� ��ViewHolder */
		public final class ViewHolder {
			public TextView tvName;
			public RadioButton btnO;
			public RadioButton btnX;
			public RadioGroup RG;
		}
	}
	
	 

	private void checkVoted() {
		votedCount = 0;
		for (int i = 0; i < aryContent.size(); i++) {
			MultiTitleItem it = aryContent.get(i);
			if (it.result == 1) {
				votedCount++;
			}
		}
		if(voteInfo!=null){
			tvInfo.setText(getString(R.string.should_select) + voteInfo.select + getString(R.string.should_select1) + votedCount + getString(R.string.person));
		}
		mAdapter.notifyDataSetChanged();
		listview.invalidate();
		
	}

	// @Override
	public void onBack() {
		mAdapter.notifyDataSetChanged();
	}
}
