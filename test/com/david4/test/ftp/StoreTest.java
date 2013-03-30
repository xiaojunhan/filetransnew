package com.david4.test.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

import com.david4.filetrans.util.FTPUtil;

public class StoreTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String toPath = "a.txt";
		String localPath = "F:\\eclipse-jee-helios-SR2-win32-x86_64.zip";
		FTPClient client = null;
		FileInputStream fis = null;
		try {
			client = FTPUtil.getFtpClient("127.0.0.1", 21,"test", "test");
			 
			File file = new File(localPath);
			fis = new FileInputStream(file);
			System.out.println("=======start========");
			boolean b = client.storeFile(toPath, fis);
			System.out.println("=======end========");
			System.out.println(b);
		} finally {
			try {
				if(fis!=null){
					fis.close();
				}
			} catch (Exception e) {

			}
			close(client);
		}
	}
	public static void close(FTPClient ftp) {
		if (ftp != null && ftp.isConnected()) {
//			try {
//				ftp.completePendingCommand();
//			} catch (IOException e) {
//				logger.warn(e.getMessage());
//			}
			try {
				ftp.logout();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
