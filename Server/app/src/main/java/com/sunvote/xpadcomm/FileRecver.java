package com.sunvote.xpadcomm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Environment;
import android.util.Log;

public class FileRecver {
	private String TAG = "FileRecver";
	private String ip;
	private int port;
	private Socket socket = null;
	DataOutputStream out = null;
	DataInputStream getMessageStream = null;
	private String filePath = Environment.getExternalStorageDirectory().getPath() + "/sunvote";
	private String fileName;
	private String keypadID;
	private FileReciverInterface listener;

	public interface FileReciverInterface {
		void onConnectServerError();

		void onDownloadDataError();

		void onDownload(long percent);

		void onDownloadSuccess();

	}

	public FileRecver(FileReciverInterface recvInterface, String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.listener = recvInterface;
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdir();
			if (file.exists()) {
				Log.d(TAG, "create dir ok!");
			}
		}
	}

	public void tryGetMeetingFiles(int keyId) {
		keypadID = String.valueOf(keyId);
		if (createConnection()) {
			sendMessage(keyId + "\n");
			getMeetingFiles();
		}
	}

	public boolean createConnection() {
		try {
			socket = new Socket(ip, port);
			return true;
		} catch (UnknownHostException e) {
			listener.onConnectServerError();
			e.printStackTrace();
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return false;
		} catch (Exception e) {
			listener.onConnectServerError();
			e.printStackTrace();
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return false;
		}
	}

	public void sendMessage(String sendMessage) {
		try {
			out = new DataOutputStream(socket.getOutputStream());

			byte[] midbytes = sendMessage.getBytes("UTF8");
			out.write(midbytes);
			// out.writeUTF(sendMessage);
			out.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void getMeetingFiles() {
		DataInputStream inputStream = null;
		inputStream = getDataInStream();
		try {
			String savePath = null;
			int bufferSize = 4096;// 8192
			byte[] buf = new byte[bufferSize];
			long passedlen = 0;
			long len = 0;
			long tmpTime = System.currentTimeMillis();
			String strln = null;
			while (strln == null) {
				strln = inputStream.readLine();
				if (strln != null) {
					Log.d(TAG, strln);
					String[] ary = strln.split(",");
					if (ary.length == 2) {
						len = Long.parseLong(ary[0]);
						fileName = ary[1];
						savePath = filePath + "/" + fileName;
						break;
					}
				}
				if (System.currentTimeMillis() - tmpTime > 20 * 1000) {
					Log.e(TAG, "get file name and len time out!");
					break;
				}
				// strln = inputStream.readLine();
			}
			if (savePath == null) {
				Log.e(TAG, "savePath==null");
				listener.onConnectServerError();
				return;
			}
			File saveFile = new File(savePath);
			if (saveFile.exists()) {
				saveFile.delete();
			}
			// savePath += inputStream.readUTF();
			// log.d("AndroidClient","@@@savePath"+savePath);
			DataOutputStream fileOut = new DataOutputStream(
					new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));
			// len = inputStream.readLong();
			// Log.d(TAG,"文件的长度为:"+len);
			Log.d(TAG, "start recv");
			while (true) {
				int read = 0;
				if (inputStream != null) {
					read = inputStream.read(buf);
				}
				passedlen += read;
				if (read == -1) {
					break;
				}
				long per = passedlen * 100 / len;
				Log.d(TAG, "file recv" + per + "%/n");
				if (per < 0) {
					Log.d(TAG, "error");
				}
				listener.onDownload(per);
				fileOut.write(buf, 0, read);
				if (passedlen == len) {
					break;
				}
			}
			// Log.d("AndroidClient", "@@@文件接收完成" + savePath);
			Log.d(TAG, "recv file 100%");
			fileOut.close();
			String outDirName;
			try {
				Log.d(TAG, "unzip");
				if (fileName.endsWith(".zip")) {
					outDirName = fileName.substring(0, fileName.length() - 4);
					String uzipDirPath = filePath + "/" + outDirName;
					File unzipDir = new File(uzipDirPath);
					if (!unzipDir.exists()) {
						unzipDir.mkdir();
					}
					AntZipUtil.unZip(filePath + "/" + fileName, uzipDirPath);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			sendMessage(keypadID + ":success\n");
			Log.d(TAG, "send success");
			int answerTimes = 0;
			while (answerTimes < 30) {
				Log.d(TAG, "send success");
				sendMessage(keypadID + ":success\n");
				strln = inputStream.readLine();
				if (strln != null) {
					Log.d(TAG, "=======recv:" + strln);
					if (strln.equals("success")) {
						Log.d(TAG, "recv server seccuss");
						break;
					}
				}
				answerTimes++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (answerTimes == 30) {
				Log.d(TAG, "wait answer timeout");
			}
			shutDownConnection();// 关掉socket连接

			listener.onDownloadSuccess();

			// UnZip.unzipFile(filePath+"/"+fileName , filePath+"/metting/");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DataInputStream getDataInStream() {
		try {
			getMessageStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			// return getMessageStream;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (getMessageStream != null) {
				try {
					getMessageStream.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return getMessageStream;
	}

	public void shutDownConnection() {
		try {
			if (out != null) {
				out.close();
			}
			if (getMessageStream != null) {
				getMessageStream.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
