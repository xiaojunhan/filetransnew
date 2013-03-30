package com.david4.test;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.david4.filetrans.model.FileTransTaskModel;
import com.david4.filetrans.model.FileTransTaskModel.From;
import com.david4.filetrans.util.SFTPUtil;

public class SFTPUtilTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new FileSystemXmlApplicationContext("webroot/WEB-INF/conf/webControllerContext.xml");
		
		getPathListTest(context);
	}

	public static void getPathListTest(ApplicationContext context) throws Exception{
		SFTPUtil sFTPUtil = new SFTPUtil();
		From from = new FileTransTaskModel().new From();
		String path = "var path = \"offline/offlinefiles/send/(\\d{11})/((FH|SS|SN|SR|ER|BL)(\\w{34}|\\w{23})$)\";";
		from.setPath(path);
		from.setServerid("-2");
		from.setType("sftp");
		List<String> list = sFTPUtil.getPathList(from);
		if(list!=null && list.size()>0){
			for(String s:list){
				System.out.println(s);
			}
		}
	}
}
