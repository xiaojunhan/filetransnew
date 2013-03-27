//package com.david4.filetrans.filter;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.apache.commons.net.ftp.FTPFile;
//import org.apache.commons.net.ftp.FTPFileFilter;
///**
// * 交易文件过滤器
// * @author hanxj
// *
// */
//public class TransFileFilter implements FTPFileFilter{
//
//	@Override
//	public boolean accept(FTPFile file) {
//		String name = file.getName();
//		Pattern p2 = Pattern
//				.compile("((FH|SS|SN|SR|ER|BL)\\w{34}|(FH|SS|SN|SR|ER|BL)\\w{23})");
//		Matcher m = p2.matcher(name);
//		return m.find();
//	}
//
//	 
//
//}
