package com.david4.filetrans;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Config {
	private static volatile String localPathTemp = null;
	private static final Lock lock = new ReentrantLock();
	/**
	 * 获取本地临时文件存放路径
	 * @return
	 */
	public static String getLocalPathTemp() {
		if (localPathTemp != null) {
			return localPathTemp;
		}
		lock.lock();
		try {
			if (localPathTemp != null) {
				return localPathTemp;
			}
			String path = Config.class.getResource("/").toString();
			path = path.substring(5,
					path.length() - "WEB-INF/classes/".length());
			path = path + "file/temp/";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			localPathTemp = path;
			return localPathTemp;
		} finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) {
		String s = getLocalPathTemp();
		System.out.println(s);
	}
}
