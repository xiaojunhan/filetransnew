//package com.david4.test;
//
//import org.apache.commons.net.ftp.FTPClient;
//
//import com.david4.common.util.FTPUtil;
//
//public class FTPTest {
//
//	/**
//	 * @param args
//	 * @throws Exception 
//	 */
//	public static void main(String[] args) throws Exception {
//		// TODO Auto-generated method stub
//		FTPClient qzftp = FTPUtil.getFtpClient("127.0.0.1",21,"qz","qz");
//		if(qzftp==null){
//			throw new Exception("connect and login ftp error");
//		}
//		
//		
//		boolean b = qzftp.rename("/serverBusiness/00000000004/upload/FH1103097000705000002001A00705000001", 
//				"/serverBusiness/00000000004/uploadbak/FH1103097000705000002001A00705000001");
//		System.out.println("=="+b);
//		FTPUtil.close(qzftp);
////		FTPFile[] dirArr = qzftp.listDirectories("/serverBusiness/");
////		for(FTPFile s:dirArr ){
////			System.out.println(s.getName());
////		}
//	}
//
//}
