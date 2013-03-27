package com.david4.filetrans.util;

import com.david4.common.SpringContainer;

public class FileTransUtilFactory {

	public static FileTransUtil getInstance(String type) throws Exception{
		if("ftp".equalsIgnoreCase(type)){
			return SpringContainer.getBean(FTPUtil.class);
		}
		if("sftp".equalsIgnoreCase(type)){
			return SpringContainer.getBean(SFTPUtil.class);
		}
		throw new Exception("can't get fileTransUtil by type '"+type+"'");
	}

}
