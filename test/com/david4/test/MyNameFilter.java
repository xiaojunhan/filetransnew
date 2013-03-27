package com.david4.test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyNameFilter implements FilenameFilter {
	private String reg;
	MyNameFilter(String reg){
		this.reg = reg;
	}
	@Override
	public boolean accept(File dir, String name) {
		Pattern p = Pattern.compile("^"+reg+"$");
		Matcher m = p.matcher(name);
		return m.find();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
