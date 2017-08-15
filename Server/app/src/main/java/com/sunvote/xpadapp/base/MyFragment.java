package com.sunvote.xpadapp.base;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;

import java.lang.reflect.Field;

public class MyFragment extends Fragment {
	public MainActivity mMainActivity;
	private static final Field sChildFragmentManagerField;
	private  static String TAG = "MyFragment";
//	public  String[] voteOptions = { getString(R.string.zc_fd), getString(R.string.zc_fd_qq), getString(R.string.ty_fd_qq), getString(R.string.my_jbmy_bmy), getString(R.string.my_jbmy_bmy_blj),
//			getString(R.string.my_jbmy_yb_bmy), getString(R.string.fcmy_my_blj_bmy_fcbmy), getString(R.string.fcty_ty_bqd_bty_fcbty), getString(R.string.yx_cz_bcz), getString(R.string.yx_cz_jbcz_bcz),
//			getString(R.string.y_l_z_c), getString(R.string.my_bmy_fcbmy) };
////	


	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "Error getting mChildFragmentManager field", e);
		}
		sChildFragmentManagerField = f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mMainActivity = (MainActivity)activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} catch (Exception e) {
				Log.e(TAG, "Error setting mChildFragmentManager field", e);
			}
		}
	}
	
	
	public int getResultResourceByTitle(String title) {
		if (title.equals(getString(R.string.agree))) {
			return R.drawable.agree;
		} else if (title.equals(getString(R.string.tongyi))) {
			return R.drawable.ok;
		} else if (title.equals(getString(R.string.oppose))) {
			return R.drawable.oppos;
		} else if (title.equals(getString(R.string.abstant))) {
			return R.drawable.abstain;
		}

		return R.drawable.voted;
	}
}
