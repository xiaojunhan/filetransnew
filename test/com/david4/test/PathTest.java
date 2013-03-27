package com.david4.test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String path = "D:/home/ftp/qcbank/(.*?)/acc/(.*)\\.(.*?)";
//		String[] pathArr = path.split("/");
		File file = new File("D:/home/ftp/qcbank/CMB/acc");
		File[] fileArr = file.listFiles(new MyNameFilter("(.*)\\.(.*?)"));
		for(File f:fileArr){
			//System.out.println("=="+f.getName());
			//取最后一个点
			Pattern p = Pattern.compile("^(.*)\\.(.*?)$");
			Matcher m = p.matcher(f.getName());
			System.out.println("m.groupCount()=="+m.groupCount());
			if(m.find()){
				System.out.println("result="+m.group(1)+"==="+m.group(2));
			}
		}
//		for(String s:pathArr){
//			System.out.println("=="+s);
//		}
//		System.out.println(getFirstPath(pathArr));
	}

	public static String getFirstPath(String[] pathArr){
		if(pathArr==null || pathArr.length==0){
			return null;
		}
		for(int i=0;i<pathArr.length;i++){
			if(pathArr[i]!=null && !pathArr[i].trim().equals("")){
				return pathArr[i];
			}
		}
		return null;
	}
	
	public static List<String> getFilePathList(String path){
		String[] pathArr = path.split("/");
		
		List<String> list = new ArrayList<String>();
		return list;
	}
}
