package com.david4.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class T2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String from = "/home/weblogic/test/offlinefiles/send/00705000002/SR1301097000705000002001A00705000001";
		String reg =  "/home/weblogic/test/offlinefiles/send/(\\d{11})/((FH|SS|SN|SR|ER|BL)\\w{34}|(FH|SS|SN|SR|ER|BL)\\w{23})";
		Pattern p = Pattern.compile("^"+reg+"$");
		Matcher m = p.matcher(from);
		String[] strArr = null;
		if(m.find()){
			int count = m.groupCount();
			strArr = new String[count];
			for(int i=0;i<count;i++){
				String s = m.group(i+1);
				System.out.println(s);
			}
		}

	}

}
