package com.david4.filetrans.util;

import java.util.List;

import com.david4.filetrans.model.FileInfo;
import com.david4.filetrans.model.FileTransTaskModel.Delete;
import com.david4.filetrans.model.FileTransTaskModel.From;
import com.david4.filetrans.model.FileTransTaskModel.To;
import com.david4.filetrans.model.ServerConfig;

public interface FileTransUtil {
	/**
	 * 获取文件列表
	 * 
	 * @param from
	 * @return
	 * @throws Exception
	 */
	public List<FileInfo> getFileInfoList(From from) throws Exception;

	/**
	 * 文件删除
	 * 
	 * @param delete
	 * @param deletePath
	 */
	public void delete(Delete delete, String deletePath) throws Exception;

	/**
	 * 下载
	 * 
	 * @param from
	 * @param fromPath
	 * @param localPath
	 * @throws Exception
	 */
	public void get(From from, String fromPath, String localPath)
			throws Exception;

	/**
	 * 上传 上传时先传到一个临时目录，传完后再移动到需要的目录
	 * 
	 * @param to
	 * @param toPath
	 * @param localPath
	 * @throws Exception
	 */
	public void put(To to, String toPath, String localPath) throws Exception;

	public void move(ServerConfig serverConfig, String fromPath, String toPath)
			throws Exception;
}
