package com.sunvote.xpadapp.fragments;

import com.sunvote.xpadapp.MainActivity;
import com.sunvote.xpadapp.R;
import com.sunvote.xpadapp.base.BaseFragment;
import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadcomm.XPadApi;
import com.sunvote.xpadcomm.XPadApiInterface.EvaluateInfo;
import com.sunvote.xpadcomm.XPadApiInterface.VoteInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContentVoteFragment extends BaseFragment {
    private String TAG = "ContentVoteFragment";

    private TextView tvTitle;
    private WebView tvContent;
    private TextView tvModify;
    private TextView tvTips;
    private TextView ivReuslt;
    private Button btnA;
    private Button btnB;
    private Button btnC;
    private Button btnModify;

    private RelativeLayout tipLayout;
    private RelativeLayout voteLayout;
    private RelativeLayout modifyLayout;

    private int scale = 0;
    private int originSize = 200;

    BillInfo bill;

    VoteInfo voteInfo;
    EvaluateInfo evaluateInfo;

    private int modifyable;
    private int secret;
    private String[] options;
    private String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

    public ContentVoteFragment() {
        super();
    }



    public void setInfo(BillInfo info){
        bill = info;
        options = bill.billOptions.split("/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ContentVoteFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_contentvote, container, false);
        final MainActivity mact = (MainActivity) getActivity();

        tvModify = (TextView) view.findViewById(R.id.contentvote_tvmodify);

        tvTitle = (TextView) view.findViewById(R.id.contentvote_title);
        tvTitle.setText(mMainActivity.meetingInfo.meetingTitle);
        tvContent = (WebView) view.findViewById(R.id.contentvote_content);

        // String filename ="file:///"+ DATABASE_PATH +
        // "/sunvote/htmltest/xpad.htm";
        String filename = "file:///" + DATABASE_PATH + "/sunvote/" + mMainActivity.meetingInfo.meetingID + "/"
                + bill.billFile;
        tvContent.loadUrl(filename);
        WebSettings mWebSettings = tvContent.getSettings();
        tvContent.setInitialScale(originSize);

        mWebSettings.setSupportZoom(true);


        tvTips = (TextView) view.findViewById(R.id.contentvote_tv_tips);
        ivReuslt = (TextView) view.findViewById(R.id.contentvote_tv_result);
        ivReuslt.setVisibility(View.INVISIBLE);
        tipLayout = (RelativeLayout) view.findViewById(R.id.contentvote_pannal_tips);
        voteLayout = (RelativeLayout) view.findViewById(R.id.contentvote_pannal_vote);
        modifyLayout = (RelativeLayout) view.findViewById(R.id.contentvote_pannal_modify);
        // tvContent.setTextSize(originSize);
        ImageButton btnBigger = (ImageButton) view.findViewById(R.id.contentvote_bigger);
        btnBigger.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                scale++;
                if (scale > 10) {
                    scale = 10;
                }
                int newsize = originSize + scale * 20;
                Log.d(TAG, "old size = " + originSize + " new size = " + newsize);
                // tvContent.setTextSize(newsize);
                tvContent.setInitialScale(newsize);

            }
        });

        ImageButton btnSmaller = (ImageButton) view.findViewById(R.id.contentvote_smaller);
        btnSmaller.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scale--;
                if (scale < -3) {
                    scale = -3;
                }

                int newsize = (originSize + scale * 20);
                Log.d(TAG, "old size = " + originSize + " new size = " + newsize);
                tvContent.setInitialScale(newsize);

            }
        });

        btnA = (Button) view.findViewById(R.id.contentvote_btnA);
        btnA.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                doVoteWithIndex(1);

            }
        });

        btnB = (Button) view.findViewById(R.id.contentvote_btnB);
        btnB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                doVoteWithIndex(2);

            }
        });

        btnC = (Button) view.findViewById(R.id.contentvote_btnC);
        btnC.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (options.length == 2) {
                    doVoteWithIndex(2);
                } else {
                    doVoteWithIndex(3);
                }

            }
        });

        btnModify = (Button) view.findViewById(R.id.contentvote_btn_modify);
        btnModify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showVote();
            }
        });

        if (options.length == 2) {
            btnA.setText(options[0]);
            btnB.setVisibility(View.INVISIBLE);
            btnC.setText(options[1]);
        } else if (options.length == 3) {
            btnA.setText(options[0]);
            btnB.setText(options[1]);
            btnC.setText(options[2]);
        }

        hideVote();
        return view;
    }


    private void doVoteWithIndex(int index) {

        tvModify.setText(getString(R.string.submiting));
        mMainActivity.presenter.submitVote(XPadApi.AnsType_Single, String.valueOf(index));
        if (secret == 0) {
            if (bill.billType == BillInfo.BillType_Vote) {
                ivReuslt.setText("");
                ivReuslt.setBackgroundResource(getResultResourceByTitle(options[index - 1]));
            } else if (bill.billType == BillInfo.BillType_Evaluate) {
                ivReuslt.setText(options[index - 1]);
                ivReuslt.setBackgroundResource(R.drawable.voted_empty);
            }
        } else {
            ivReuslt.setText("");
            ivReuslt.setBackgroundResource(R.drawable.voted);
        }
        ivReuslt.setVisibility(View.VISIBLE);

        if (modifyable == 1) {
            showModify();
        } else {
            disableVote();
        }
    }

    private void hideVote() {
        voteLayout.setVisibility(View.INVISIBLE);
        ivReuslt.setVisibility(View.INVISIBLE);
        // tvTips.setVisibility(View.INVISIBLE);
    }

    private void showVote() {
        voteLayout.setVisibility(View.VISIBLE);
        //tipLayout.setVisibility(View.VISIBLE);
        modifyLayout.setVisibility(View.INVISIBLE);
        ivReuslt.setVisibility(View.INVISIBLE);
        tvTips.setVisibility(View.VISIBLE);

    }

    private void disableVote() {
        // btnA.setVisibility(View.INVISIBLE);
        // btnB.setVisibility(View.INVISIBLE);

        // btnC.setVisibility(View.INVISIBLE);
        voteLayout.setVisibility(View.INVISIBLE);
        //tipLayout.setVisibility(View.INVISIBLE);
        modifyLayout.setVisibility(View.INVISIBLE);
    }

    private void showTips() {
        voteLayout.setVisibility(View.INVISIBLE);
        //tipLayout.setVisibility(View.VISIBLE);
        modifyLayout.setVisibility(View.INVISIBLE);
    }

    private void showModify() {
        voteLayout.setVisibility(View.INVISIBLE);
        tipLayout.setVisibility(View.INVISIBLE);
        modifyLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVoteEvent(int baseId, int iMode, Object info) {
        if (iMode == XPadApi.VoteType_Vote) {
            voteInfo = (VoteInfo) info;
            modifyable = voteInfo.modifyMode;
            secret = voteInfo.secretMode;
            if (modifyable == 1) {
                tvModify.setText(getString(R.string.please_vote_can_modify));
            } else {
                tvModify.setText(getString(R.string.please_vote_can_not_modify));
            }
            showVote();
        } else if (iMode == XPadApi.VoteType_Evaluate) {
            evaluateInfo = (EvaluateInfo) info;
            modifyable = evaluateInfo.modify;
            secret = evaluateInfo.secrecy;
            showVote();
        } else if (iMode == XPadApi.VoteType_Stop) {
            disableVote();
        }

    }

    @Override
    public void onVoteSubmitSuccess() {
        tvModify.setText(getString(R.string.submited));

        super.onVoteSubmitSuccess();
    }
}
