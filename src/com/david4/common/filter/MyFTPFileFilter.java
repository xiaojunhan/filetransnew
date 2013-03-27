package com.david4.common.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

public class MyFTPFileFilter implements FTPFileFilter {
	private String reg;

	public MyFTPFileFilter(String reg) {
		this.reg = reg;
	}

	@Override
	public boolean accept(FTPFile file) {
		String name = file.getName();
		Pattern p = Pattern.compile("^" + reg + "$");
		Matcher m = p.matcher(name);
		return m.find();
	}

}
