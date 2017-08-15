package com.sunvote.xpadapp.db;


import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper {
	// 得到SD卡路径
	private final String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
			
	private final Activity activity;

	private final String DATABASE_FILENAME;

	private int mId;
	public DBHelper(Context context,int meetingId) {

		mId = meetingId;
		DATABASE_FILENAME = "Meeting.db";
		activity = (Activity) context;
	}

	// 得到操作数据库的对象
	public SQLiteDatabase openDatabase(int confId) {
		try {
			boolean b = false;
			mId = confId;
			if(confId == 0){
				Log.e("DBHelper", "openDatabase with Error confid 0");
				return null;
			}
			// 得到数据库的完整路径名
			String databaseFilename = DATABASE_PATH + "/sunvote/"+ mId + "/" + DATABASE_FILENAME;
			// 将数据库文件从资源文件放到合适地方（资源文件也就是数据库文件放在项目的res下的raw目录中）
			// 将数据库文件复制到SD卡中 File dir = new File(DATABASE_PATH);
//			if (!dir.exists())
//				b = dir.mkdir();
//			// 判断是否存在该文件
			if (!(new File(databaseFilename)).exists()) {
				Log.e("DBHelper", "db not exists");
				//Toast.makeText(activity, "没有找到会议资料文件，请联系管理员下载", Toast.LENGTH_LONG);
//				// 不存在得到数据库输入流对象
//				InputStream is = activity.getResources().openRawResource(R.raw.jokebook);
//				// 创建输出流
//				FileOutputStream fos = new FileOutputStream(databaseFilename);
//				// 将数据输出
//				byte[] buffer = new byte[8192];
//				int count = 0;
//				while ((count = is.read(buffer)) > 0) {
//					fos.write(buffer, 0, count);
//				}
//				// 关闭资源
//				fos.close();
//				is.close();
			}
			// 得到SQLDatabase对象
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
			return database;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
