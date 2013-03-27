package com.david4.test;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String name = "FH1103097000705000002001A00705000002";
//		System.out.println(isNameValid(name));
//		String dir = "/serverBusiness//00000000005/uploadbak//FH1103097000705000002001A00705000004";
//		dir = dir.replaceAll("/+", "/");
//		System.out.println(dir);
//		ConcurrentHashMap<Integer,Integer> taskMap = new ConcurrentHashMap<Integer,Integer>();
//		System.out.println(taskMap.put(1, 0));
//		System.out.println(taskMap.put(1, 1));
//		System.out.println(taskMap.put(1, 2));
		String path = "F:\\1\\284\\c";
		File file = new File(path);
		File[] fileArr = file.listFiles();
		for(int i=0;i<fileArr.length;i++){
			String s="<VALUE><BUSINESSNO>"+fileArr[i].getName()+"</BUSINESSNO><PASSWORD>"+fileArr[i].getName()+"</PASSWORD></VALUE>";
			System.out.println(s);
		}
	}

	public static boolean isNameValid(String name){
		Pattern p2 = Pattern
				.compile("^((FH|SS|SN|SR|ER|BL)\\w{34}|(FH|SS|SN|SR|ER|BL)\\w{23})$");
		Matcher m = p2.matcher(name);
		return m.find();
	}
}
