package com.david4.test;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.david4.filetrans.model.FileInfo;
import com.david4.filetrans.model.FileTransTaskModel;
import com.david4.filetrans.model.FileTransTaskModel.From;
import com.david4.filetrans.util.FTPUtil;
import com.david4.filetrans.util.SFTPUtil;

public class FTPUtilTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new FileSystemXmlApplicationContext("webroot/WEB-INF/conf/webControllerContext.xml");
		
		getPathListTest(context);
	}

	public static void getPathListTest(ApplicationContext context) throws Exception{
		FTPUtil ftpUtil = context.getBean(FTPUtil.class);
		From from = new FileTransTaskModel().new From();
		String path = "var path = \"qcbank/(.*?)/acc/(.*?)\";";
		from.setPath(path);
		from.setServerid("-1");
		from.setType("ftp");
		List<FileInfo> list = ftpUtil.getFileInfoList(from);
		System.out.println("list.size="+list.size());
		if(list!=null && list.size()>0){
			for(FileInfo s:list){
				System.out.println("test=="+s);
			}
		}
	}
}
