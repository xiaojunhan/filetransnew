package com.david4.filetrans.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.david4.common.BaseService;
import com.david4.common.exception.FromFileEmptyException;
import com.david4.common.util.FileUtil;
import com.david4.common.util.NumberUtil;
import com.david4.common.util.ScriptUtil;
import com.david4.console.TaskInfo;
import com.david4.filetrans.Config;
import com.david4.filetrans.Constants;
import com.david4.filetrans.config.TaskConfig;
import com.david4.filetrans.model.FileTransTaskModel;
import com.david4.filetrans.model.FileTransTaskModel.Delete;
import com.david4.filetrans.model.FileTransTaskModel.From;
import com.david4.filetrans.model.FileTransTaskModel.To;
import com.david4.filetrans.model.ServerConfig;
import com.david4.filetrans.util.FileTransUtil;
import com.david4.filetrans.util.FileTransUtilFactory;
@Service
public class FileTransService extends BaseService{
	@Autowired
	@Qualifier("taskConfig")
	private TaskConfig taskConfig;
	public void doTask(FileTransTaskModel taskModel) throws Exception{
		//TODO没有from也能继续
		From from = taskModel.getFrom();
		String fromPathTemp = from.getPath();
		if(fromPathTemp==null || fromPathTemp.trim().length()==0){
			throw new Exception("fromPath null,fromPath="+fromPathTemp);
		}
		fromPathTemp = ScriptUtil.getString(fromPathTemp, Constants.SCRIPT_PATH);
		if(fromPathTemp==null || fromPathTemp.trim().length()==0){
			throw new Exception("fromPath null,fromPath="+fromPathTemp);
		}
		from.setPath(fromPathTemp);
		
		List<String> list= getPathList(from);
		if(list==null || list.size()==0){
			throw new FromFileEmptyException("from file empty");
		}
		List<To> toList = taskModel.getTo();
		if(toList!=null && toList.size()>0){
			//判断是否需要下载到本地
			//若tolist中serverid、type和from中一致，不需要下载
			boolean needDown = false;
			for(To to:toList){
				if(!to.getServerid().equals(from.getServerid()) || !to.getType().equals(from.getType())){
					needDown  = true;
					break;
				}
			}
			
			for(String fromPath:list){
				System.out.println("fromPath="+fromPath);
				String tempFileName = getTempFileName();
				String localPath = getLOCAL_PATH_TEMP()+tempFileName;
				if(needDown){
					download(from,fromPath,localPath);
				}
				for(To to:toList){
					String toPath = getToPath(fromPath, to.getPath(), from.getPath());
					try{
						if(to.getServerid().equals(from.getServerid()) && to.getType().equals(from.getType())){
							//在同一台服务器上
							move(from,fromPath,toPath);
						}else{
							fileTrans(localPath,to,toPath);
						}
					}catch(Exception e){
						String toServerId = to.getServerid();
						ServerConfig serverConfig = taskConfig.getServerConfig(toServerId);
						String host = null;
						int port = 0;
						if(serverConfig!=null){
							host = serverConfig.getHost();
							port = serverConfig.getPort();
						}
						TaskInfo.log("传文件失败,fromPath="+fromPath+"to host="+host+" port="+port);
						logger.error(e.getMessage());
					}
				}
				//都处理完后删除临时文件
				if(needDown){
					File file = new File(localPath);
					file.delete();
				}
			}
		}

		List<Delete> deleteList = taskModel.getDelete();
		if(deleteList!=null && deleteList.size()>0){
			for(Delete delete:deleteList){
				for(String fromPath:list){
					delete(from,delete,fromPath);				
				}
			}
		}
	}
	/**
	 * 获取来源文件列表 
	 * @param from
	 * @return
	 * @throws Exception 
	 */
	public List<String> getPathList(From from) throws Exception{
		String type = from.getType();
		FileTransUtil fileTransUtil = FileTransUtilFactory.getInstance(type);
		return fileTransUtil.getPathList(from);
	}
	/**
	 * 文件传输
	 * @param from
	 * @param to
	 * @param fromPath
	 * @throws Exception
	 */
	public void fileTrans(String localPath,To to,String toPath) throws Exception{
		String type = to.getType();
		FileTransUtil fromFileTransUtil = FileTransUtilFactory.getInstance(type);
		try {
			fromFileTransUtil.put(to, toPath, localPath);
		} catch (Exception e) {
			logger.error("put file error,toPath="+toPath);
			throw e;
		}
	}
	/**
	 * 文件删除
	 * @param from
	 * @param delete
	 * @param fromPath
	 * @throws Exception
	 */
	public void delete(From from,Delete delete,String fromPath) throws Exception{
		String type = delete.getType();
		FileTransUtil fromFileTransUtil = FileTransUtilFactory.getInstance(type);
		String deletePath = getToPath(fromPath, delete.getPath(), from.getPath());
		try {
			fromFileTransUtil.delete(delete, deletePath);
		} catch (Exception e) {
			logger.error("delete file error,deletePath="+deletePath);
			throw e;
		}
	}
	
	
	public void download(From from,String fromPath,String localPath) throws Exception{
		String type = from.getType();
		FileTransUtil fromFileTransUtil = FileTransUtilFactory.getInstance(type);
		try {
			fromFileTransUtil.get(from, fromPath, localPath);
		} catch (Exception e) {
			logger.error("get file error,fromPath="+fromPath);
			throw e;
		}
	}
	
	public void move(From from,String fromPath,String toPath)throws Exception{
		String type = from.getType();
		FileTransUtil fileTransUtil = FileTransUtilFactory.getInstance(type);
		try {
			String serverId = from.getServerid();
			ServerConfig serverConfig = taskConfig.getServerConfig(serverId);
			fileTransUtil.move(serverConfig,fromPath , toPath);
		} catch (Exception e) {
			logger.error("move file error,fromPath="+fromPath+",toPath="+toPath);
			throw e;
		}
	}
	/**
	 * 本地临时文件存放路径
	 * @return
	 */
	public static String getLOCAL_PATH_TEMP(){
		return Config.getLocalPathTemp();
	}
	
	//临时文件名
	public static String getTempFileName(){
		int random = NumberUtil.getRandom(100000, 999999);
		return Long.toString(System.nanoTime())+random+".tmp";
	}
	/**
	 * 
	 * @param fromPath解析后的来源路径
	 * @param toPathReg解析前配置文件中的目标路径
	 * @param fromReg解析前配置文件中的来源路径
	 * @return
	 * @throws Exception
	 */
	public String getToPath(String fromPath,String toPathReg,String fromReg) throws Exception{
		String toPathTemp = FileUtil.getPath(fromPath, toPathReg, fromReg);
		toPathTemp = ScriptUtil.getString(toPathTemp, Constants.SCRIPT_PATH);
		return toPathTemp;
	}
}
