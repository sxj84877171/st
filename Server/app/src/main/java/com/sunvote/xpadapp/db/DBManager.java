package com.sunvote.xpadapp.db;

import java.util.ArrayList;

import com.sunvote.xpadapp.db.modal.BillInfo;
import com.sunvote.xpadapp.db.modal.MeetingInfo;
import com.sunvote.xpadapp.db.modal.MultiTitleItem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;
	public int confId;

	public boolean checkDB() {
		if(db == null){
			db = helper.openDatabase(confId);
		}
		return db != null;
	}

	public DBManager(Context context, int meetingid) {
		helper = new DBHelper(context, meetingid);
		confId = meetingid;
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.openDatabase(confId);
	}
	
	public int getKeypadRole(int keypadId){
		int voterType = 0;
		Cursor c = null;
		try {
			checkDB();
			String sql = "SELECT * FROM Voters where KeypadID="+keypadId;
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				voterType = c.getInt(c.getColumnIndex("VoterType"));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return voterType;
	}
	
	
	public int writeKeypadRole(int keypadId,int role){
		int voterType = 0;
		Cursor c = null;
		try {
			 checkDB();
			String sql = "UPDATE Voters set VoterType="+role+ "  where KeypadID="+keypadId;
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return voterType;
	}
	

	public MeetingInfo getMettingInfo(int meetingId) {
		MeetingInfo info = new MeetingInfo();
		Cursor c = null;
		try {
			 checkDB();
			String sql = "SELECT * FROM Meeting";
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				info.meetingID = confId;
				info.meetingTitle = c.getString(c.getColumnIndex("MeetingTitle"));
				info.meetingAddress = c.getString(c.getColumnIndex("MeetingAddress"));
				info.meetingMember = c.getString(c.getColumnIndex("MeetingMember"));
				info.meetingMemo = c.getString(c.getColumnIndex("MeetingMemo"));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return info;
	}

	public BillInfo getBillInfo(int meetingId, int billId) {
		BillInfo info = new BillInfo();
		Cursor c = null;
		confId = meetingId;
		try {
			 checkDB();
			String sql = "SELECT * FROM Bills WHERE BillNo=" + billId;
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				info.billId = c.getInt(c.getColumnIndex("BillID"));
				info.billNo = c.getInt(c.getColumnIndex("BillNo"));
				info.billType = c.getInt(c.getColumnIndex("BillType"));
				info.subType = c.getInt(c.getColumnIndex("BillSubType"));
				info.title = c.getString(c.getColumnIndex("BillTitle"));
				info.memo = c.getString(c.getColumnIndex("BillMemo"));
				info.billFile = c.getString(c.getColumnIndex("BillFile"));
				info.billOptions = c.getString(c.getColumnIndex("BillPar"));
				break;
			}
		} catch (Exception e) {
			info = null;
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return info;
	}
	
	public ArrayList<BillInfo> getBillItems(int meetingId) {
		ArrayList<BillInfo> titleList = new ArrayList<BillInfo>();
		Cursor c = null;
		confId = meetingId;
		try {

			 checkDB();

			String sql = "SELECT * FROM Bills";
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				BillInfo it = new BillInfo();
				it.billId = c.getInt(c.getColumnIndex("BillID"));
				it.billNo = c.getInt(c.getColumnIndex("BillNo"));
				it.title = c.getString(c.getColumnIndex("BillTitle"));
				it.memo = c.getString(c.getColumnIndex("BillMemo"));
				it.billFile = c.getString(c.getColumnIndex("BillFile"));
				it.billType = c.getInt(c.getColumnIndex("BillType"));
				titleList.add(it);
				// break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return titleList;
	}
	
	public ArrayList<BillInfo> getSubBillItems(int meetingId, int billid) {
		ArrayList<BillInfo> titleList = new ArrayList<BillInfo>();
		Cursor c = null;
		confId = meetingId;
		try {

			 checkDB();

			String sql = "SELECT * FROM SubBills WHERE BillID=" + billid + " ORDER BY SubBillNo";
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				BillInfo it = new BillInfo();
				it.billId = c.getInt(c.getColumnIndex("BillID"));
				it.billNo = c.getInt(c.getColumnIndex("SubBillNo"));
				it.title = c.getString(c.getColumnIndex("BillTitle"));
				it.memo = c.getString(c.getColumnIndex("BillMemo"));
				it.billFile = c.getString(c.getColumnIndex("BillFile"));
				titleList.add(it);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return titleList;
	}

	public ArrayList<MultiTitleItem> getMultiTitleItems(int meetingId, int billid) {
		ArrayList<MultiTitleItem> titleList = new ArrayList<MultiTitleItem>();
		Cursor c = null;
		confId = meetingId;
		try {

			 checkDB();

			String sql = "SELECT * FROM SubBills WHERE BillID=" + billid + " ORDER BY SubBillNo";
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				MultiTitleItem it = new MultiTitleItem();
				it.No = c.getInt(c.getColumnIndex("SubBillNo"));
				it.title = c.getString(c.getColumnIndex("BillTitle"));
				it.content = c.getString(c.getColumnIndex("BillMemo"));
				it.file = c.getString(c.getColumnIndex("BillFile"));
				it.startVote = false;
				titleList.add(it);
				// break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return titleList;
	}

	public ArrayList<MultiTitleItem> getCandidateList(int meetingId, int billid) {

		ArrayList<MultiTitleItem> titleList = new ArrayList<MultiTitleItem>();
		Cursor c = null;
		try {
			confId = meetingId;
			 checkDB();
			 

			String sql = "SELECT * FROM Candidates WHERE BillID=" + billid + " ORDER BY CandidateNo ASC";
			c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				MultiTitleItem it = new MultiTitleItem();
				it.No = c.getInt(c.getColumnIndex("CandidateNo"));
				it.title = c.getString(c.getColumnIndex("CandidateName"));
				it.content = c.getString(c.getColumnIndex("CandidateMemo"));
				it.file = c.getString(c.getColumnIndex("CandidateFile"));
				it.startVote = false;
				titleList.add(it);
				// break;
			}

			MultiTitleItem it = new MultiTitleItem();
			it.result = 0;
			it.No = 0;
			it.startVote = false;
			titleList.add(it); // 另选他人

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return titleList;
	}

	/**
	 * close database
	 */
	public void closeDB() {
		if (db != null) {
			db.close();
			db=null;
		}
	}
}
