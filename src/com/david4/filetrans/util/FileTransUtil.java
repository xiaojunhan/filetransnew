package com.david4.filetrans.util;

import java.util.List;

import com.david4.filetrans.model.ServerConfig;
import com.david4.filetrans.model.FileTransTaskModel.Delete;
import com.david4.filetrans.model.FileTransTaskModel.From;
import com.david4.filetrans.model.FileTransTaskModel.To;

public interface FileTransUtil {
    /**
     * 文件列表
     * @param from
     * @return
     */
	public List<String> getPathList(From from)throws Exception;
	/**
	 * 文件删除
	 * @param delete
	 * @param deletePath
	 */
	public void delete(Delete delete,String deletePath)throws Exception;
	
	/**
	 * 下载
	 * @param from
	 * @param fromPath
	 * @param localPath
	 * @throws Exception
	 */
	public void get(From from,String fromPath,String localPath) throws Exception;
	/**
	 * 上传
	 * @param to
	 * @param toPath
	 * @param localPath
	 * @throws Exception
	 */
	public void put(To to,String toPath,String localPath) throws Exception;
	
	public void move(ServerConfig serverConfig,String fromPath,String toPath) throws Exception;
}
