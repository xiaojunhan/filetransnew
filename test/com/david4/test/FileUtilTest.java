package com.david4.test;

import java.util.List;

import com.david4.common.model.PathModel;
import com.david4.common.util.FileUtil;

public class FileUtilTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "qcbank/(.*?)/acc/(.*?)";
		path="///qcbank/AAAA/acc/2.txt";
		List<PathModel> list = FileUtil.getPathSegment(path);
		for(PathModel p:list){
			System.out.println(p);
		}
	}
	
	
	public static void getPathTest(){
//		String from = "qcbank/AAAA/acc/2";
//		String from = "qcbank/AAAA/acc/2";
//		String from = "qcbank/AAAA/acc/2";
//		FileUtil.getPath(from, to, reg);
	}

}
