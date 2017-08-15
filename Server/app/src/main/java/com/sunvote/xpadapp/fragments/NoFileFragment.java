package com.sunvote.xpadapp.fragments;

import com.sunvote.xpadapp.R;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoFileFragment extends Fragment {
	private TextView tv;
	public int meetingId;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("MeetingWelcome", "onCreateView");
		View view = inflater.inflate(R.layout.fragment_no_file, container, false);

		tv = (TextView) view.findViewById(R.id.fragment_no_file_title);

		// String sname = mMainActivity.meetingInfo.meetingTitle;
		tv.setText(getString(R.string.no_file_tips));

		TextView tv2 =  (TextView) view.findViewById(R.id.fragment_no_file_title_2);
		tv2.setText(getString(R.string.no_file_tips_meeting_id)+meetingId);
		return view;
	}
}
