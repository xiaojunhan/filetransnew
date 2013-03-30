package com.david4.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.david4.common.util.NumberUtil;

public class T3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		List<String> list = new ArrayList<String>();
//		list.addAll(null);
//		int random = NumberUtil.getRandom(100000, 999999);
//		System.out.println(random);
		File file = new File("D:\\ftp\\test\\qcbank\\CCCC\\acc\\kj.txt");
		System.out.println(file.delete());
	}

}
