package com.sunvote.xpadapp.base;

import com.sunvote.xpadapp.R;
import com.sunvote.xpadcomm.XPadApiInterface.VoteInfo;

import android.widget.Toast;

public class BaseFragment extends MyFragment {

	public BaseFragment() {

	}

	@Override
	public void onStart() {

		super.onStart();

		// new Handler().postDelayed(new Runnable(){
		// public void run() {
		// Log.d("BaseFragment", "getVoteStatus");
		// mMainActivity.presenter.getVoteStatus();
		// }
		// }, 500);

	}

	public void stopDownload() {

	}

	public void onVoteEvent(int baseId, int iMode, Object info) {
		// TODO Auto-generated method stub

	}

	public void onVoteEvent(VoteInfo info) {

	}

	public void onVoteSubmitSuccess() {

	}

	public void onVoteSubmitError() {
		Toast.makeText(getActivity(), getString(R.string.submit_error), Toast.LENGTH_LONG).show();
	}

	public void onVoteSubmitAllOkSuccess() {

	}
}
