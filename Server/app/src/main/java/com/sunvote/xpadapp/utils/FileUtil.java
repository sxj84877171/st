package com.sunvote.xpadapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

	public static byte[] getByteArrayFromFile(String fileName) {
		File file = null;
		try {
			file = new File(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if (!file.exists() || !file.isFile() || !file.canRead()) {
			return null;
		}

		int len = (int) file.length();
		byte buffer[] = new byte[len];

		try {
			FileInputStream fis = new FileInputStream(file);
			while (fis.read(buffer) > 0) {

			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}

	public static byte[] getByteArrayFromFile(File file) {

		if (!file.exists() || !file.isFile() || !file.canRead()) {
			return null;
		}

		int len = (int) file.length();
		byte buffer[] = new byte[len];

		try {
			FileInputStream fis = new FileInputStream(file);
			while (fis.read(buffer) > 0) {

			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}

	public static boolean saveByteArrayToFile(String fileName, byte[] data) {
		boolean success = false;
		if (null != fileName) {
			String name = fileName;
			int count = 0;
			File file = new File(name);
			while (file.exists()) {
				count++;
				name = fileName + count;
				file = new File(name);
			}
			file.getParentFile().mkdirs();
			try {
				FileOutputStream fs = new FileOutputStream(fileName);
				fs.write(data);
				fs.close();
				success = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return success;
		} else {
			return success;
		}
	}

	public static void deleteFile(File file) {
		if(file == null){
			return;
		}
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteFile(childFiles[i]);
			}
			file.delete();
		}
	}
}
