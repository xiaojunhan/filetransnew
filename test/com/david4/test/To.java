package com.david4.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class To {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String reg = "serverBussiness/(\\d{11})/upload/((FH|SS|SN|SR|ER|BL)(\\w{34}|\\w{23})$)";

				Pattern p = Pattern.compile("^"+reg+"$");
		//Pattern p = Pattern.compile(reg);
						Matcher m = p.matcher("serverBussiness/00631494692/upload/FH2203097000000000004002A");

						if(m.find()){
							int count = m.groupCount();
							 
							for(int i=0;i<count;i++){
								System.out.println(m.group(i+1));
							}
						}else{
							System.out.println("false");
						}
						

	}

}
