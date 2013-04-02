package com.david4.filetrans.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.david4.common.filter.MyFTPFileFilter;
import com.david4.common.model.PathModel;
import com.david4.common.util.DateUtil;
import com.david4.common.util.FileUtil;
import com.david4.common.util.NumberUtil;
import com.david4.common.util.ScriptUtil;
import com.david4.filetrans.Constants;
import com.david4.filetrans.config.TaskConfig;
import com.david4.filetrans.model.FileInfo;
import com.david4.filetrans.model.FileTransTaskModel.Delete;
import com.david4.filetrans.model.FileTransTaskModel.From;
import com.david4.filetrans.model.FileTransTaskModel.To;
import com.david4.filetrans.model.ServerConfig;

@Component
public class FTPUtil implements FileTransUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(FTPUtil.class);

	@Autowired
	@Qualifier("taskConfig")
	private TaskConfig taskConfig;

//	@Override
//	public List<String> getPathList(From from) throws Exception {
//		String path = from.getPath();
//		if(path==null || path.trim().length()==0){
//			throw new Exception("fromPath null,fromPath="+path);
//		}
//		path = ScriptUtil.getString(path, Constants.SCRIPT_PATH);
//		if(path==null || path.trim().length()==0){
//			throw new Exception("fromPath null,fromPath="+path);
//		}
//		FTPClient client = null;
//		try {
//			client = getFtpClient(from.getServerid());
//			return getFileList(path, client);
//		} finally {
//			close(client);
//		}
//	}

	@Override
	public void delete(Delete delete, String deletePath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void get(From from, String fromPath, String localPath)
			throws Exception {
		FTPClient client = getFtpClient(from.getServerid());
		File file = new File(localPath);
		FileOutputStream fos = new FileOutputStream(file);
		try {
			boolean b = client.retrieveFile(fromPath, fos);
			if (!b) {
				throw new Exception("get file error");
			}
		} finally {
			try {
				fos.close();
			} catch (Exception e) {

			}
			close(client);
		}
	}

	@Override
	public void put(To to, String toPath, String localPath) throws Exception {
		String toPathTemp = null;
		if(toPath.indexOf("/")>-1){
			toPathTemp = toPath.substring(0,toPath.indexOf("/"));
			toPathTemp = toPathTemp + "/"+getTempName();
		}else{
			toPathTemp = getTempName();
		}
		FTPClient client = null;
		FileInputStream fis = null;
		try {
			client = getFtpClient(to.getServerid());
			mkdirs(getFtpDir(toPath), client);
			File file = new File(localPath);
			fis = new FileInputStream(file);
			boolean b = client.storeFile(toPathTemp, fis);
			if (!b) {
				throw new Exception("storeFile error");
			}
			b= client.rename(toPathTemp, toPath);
			/**
			 * 执行rename
			 * 失败的话，一般是由于文件已存在，所以删除源文件
			 * 若删除源文件成功再次rename,若rename仍然失败,将临时文件删除，抛出异常退出
			 * 若删除源文件失败 将临时文件删除，抛出异常退出
			 */
			if (!b) {
				if(client.deleteFile(toPath)){
					if(!client.rename(toPathTemp, toPath)){
						client.deleteFile(toPathTemp);
						throw new Exception("rename error");
					}
				}else{
					client.deleteFile(toPathTemp);
					throw new Exception("delete error");
				}
			}
		} finally {
			try {
				if(fis!=null){
					fis.close();
				}
			} catch (Exception e) {

			}
			close(client);
		}
	}

//	/**
//	 * 获取FTP文件列表 path支持正则表达式
//	 * 如test/offlinefiles/send/(\\d{11})/((FH|SS|SN|SR|ER|BL
//	 * )\\w{34}|(FH|SS|SN|SR|ER|BL)\\w{23})
//	 * 
//	 * @param path
//	 * @param client
//	 * @return
//	 * @throws IOException
//	 */
//	public static List<String> getFileList(String path, FTPClient client)
//			throws IOException {
//		List<PathModel> list = FileUtil.getPathSegment(path);
//		return getFileList(list, 0, client);
//	}
//
//	public static List<String> getFileList(List<PathModel> list, int index,
//			FTPClient client) throws IOException {
//		// 退出
//		if (list == null || index < 0 || index >= list.size()) {
//			return null;
//		}
//		// client.changeWorkingDirectory(rootPath);
//		PathModel pm = list.get(index);
//		// getFile
//		List<String> pathList = new ArrayList<String>();
//		if (index == list.size() - 1) {
//			logger.debug("file==pm.getPath()==" + pm.getPath()
//					+ "==pm.getNext()==" + pm.getNext());
//			 
//			FTPFile[] ftpFileArr = client.listFiles(pm.getPath(),
//					new MyFTPFileFilter(pm.getNext()));
//			if (ftpFileArr != null && ftpFileArr.length > 0) {
//				for (FTPFile f : ftpFileArr) {
//					if(f.getName().equals(".")||f.getName().equals("..")){
//						continue;
//					}
//					String name = f.getName();
//					String tempPath = pm.getPath() + "/" + name;
//					//中文文件名的
//					tempPath = new String(tempPath.getBytes("UTF-8"),
//							"ISO-8859-1");
//					pathList.add(tempPath);
//				}
//			}
//			return pathList;
//		}
//		if (index < list.size() - 1) {
//			// get dir
//			logger.debug("dir==pm.getPath()==" + pm.getPath()
//					+ "==pm.getNext()==" + pm.getNext());
//			FTPFile[] ftpFileArr = client.listDirectories(pm.getPath());
//			if (ftpFileArr != null && ftpFileArr.length > 0) {
//				for (FTPFile f : ftpFileArr) {
//					if(f.getName().equals(".")||f.getName().equals("..")){
//						continue;
//					}
//					Pattern p = Pattern.compile("^" + pm.getNext() + "$");
//					Matcher m = p.matcher(f.getName());
//					if (m.find()) {
//						String dirName = f.getName();
//						list.get(index + 1).setPath(
//								pm.getPath() + "/" + dirName);
//						List<String> tempList = getFileList(list, index + 1,
//								client);
//						if (tempList != null && tempList.size() > 0) {
//							pathList.addAll(tempList);
//						}
//					}
//				}
//			}
//			return pathList;
//		}
//		return null;
//	}

	public FTPClient getFtpClient(String serverId) throws Exception {
		// String serverId = from.getServerid();
		ServerConfig config = taskConfig.getServerConfig(serverId);
		FTPClient client = getFtpClient(config);
		if (client == null) {
			throw new Exception("connect and login ftp error," + config);
		}
		return client;
	}

	public FTPClient getFtpClient(ServerConfig ftpconfig) {
		if (ftpconfig == null) {
			logger.warn("ftpconfig is null");
			return null;
		}
		return getFtpClient(ftpconfig.getHost(), ftpconfig.getPort(),
				ftpconfig.getUserName(), ftpconfig.getPassword());
	}

	/**
	 * 获取获得FTP客户端
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @return
	 */
	public static FTPClient getFtpClient(String host, int port,
			String userName, String password) {
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect(host, port);
		} catch (Exception e) {
			logger.warn("连接FTP失败host=" + host + ",port=" + port + ","
					+ e.getMessage());
			return null;
		}
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			try {
				ftp.disconnect();
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}
			logger.warn("FTP server1 refused connection.host=" + host
					+ ",port=" + port);
			return null;
		}
		try {
			boolean b = ftp.login(userName, password);
			if (!b) {
				logger.warn("loginFTP失败host=" + host + ",port=" + port);
				return null;
			}
		} catch (IOException e) {
			logger.warn("loginFTP失败host=" + host + ",port=" + port + ","
					+ e.getMessage());
			return null;
		}
		ftp.setControlEncoding("UTF-8");
		ftp.setConnectTimeout(10000);
		return ftp;
	}

	public static void mkdirs(String dir, FTPClient ftp) throws IOException {
		if (dir == null) {
			return;
		}
		dir = dir.replaceAll("/+", "/");
		String sdir = ftp.printWorkingDirectory().replaceAll("\"", "");

		// 目录已存在，直接返回
		boolean result = ftp.changeWorkingDirectory(dir);
		if (result) {
			ftp.changeWorkingDirectory(sdir);
			return;
		}
		ftp.changeWorkingDirectory(sdir);
		if (!dir.endsWith("/")) {
			dir = dir + "/";
		}
		int start = 0;
		int end = 0;
		if (dir.startsWith("/")) {
			start = 1;
		} else {
			start = 0;
		}
		end = dir.indexOf("/", start);
		while (true) {
			if (end <= start) {
				ftp.changeWorkingDirectory(sdir);
				break;
			}
			String temp = dir.substring(start, end);
			if (!ftp.changeWorkingDirectory(temp)) {
				if (ftp.makeDirectory(temp)) {
					ftp.changeWorkingDirectory(temp);
				} else {
					ftp.changeWorkingDirectory(sdir);
					throw new IOException("创建目录失败,确定用户是否有创建目录的权限,dir=" + dir);
				}
			}
			start = end + 1;
			end = dir.indexOf("/", start);
			if (end <= start) {
				ftp.changeWorkingDirectory(sdir);
				break;
			}
		}
		ftp.changeWorkingDirectory(sdir);
	}

	public static String getFtpDir(String path) {
		if(path.lastIndexOf("/")==-1){
			return null;
		}
		path = path.substring(0, path.lastIndexOf("/"));
		return path;
	}

	/**
	 * 关闭FTP关闭
	 * 
	 * @param ftp
	 */
	public static void close(FTPClient ftp) {
		if (ftp != null && ftp.isConnected()) {
//			try {
//				ftp.completePendingCommand();
//			} catch (IOException e) {
//				logger.warn(e.getMessage());
//			}
			try {
				ftp.logout();
			} catch (IOException e) {
				logger.warn(e.getMessage());
			}
			try {
				ftp.disconnect();
			} catch (IOException e) {
				logger.warn(e.getMessage());
			}
			
		}
	}

	@Override
	public void move(ServerConfig serverConfig, String fromPath, String toPath)
			throws Exception {
		FTPClient client = null;
		try {
			client = getFtpClient(serverConfig);
			mkdirs(getFtpDir(toPath), client);
			boolean b = client.rename(fromPath, toPath);
			if (!b) {
				if(client.deleteFile(toPath)){
					if(!client.rename(fromPath, toPath)){
						throw new Exception("move rename error");
					}
				}else{
					throw new Exception("move delete error");
				}
			}
		} finally {
			close(client);
		}
	}
	
	public static String getTempName(){
		int random = NumberUtil.getRandom(100000, 999999);
		return Long.toString(System.nanoTime())+random+"-do-not-delete";
	}

	@Override
	public List<FileInfo> getFileInfoList(From from) throws Exception {
		String path = from.getPath();
		if(path==null || path.trim().length()==0){
			throw new Exception("fromPath null,fromPath="+path);
		}
		path = ScriptUtil.getString(path, Constants.SCRIPT_PATH);
		if(path==null || path.trim().length()==0){
			throw new Exception("fromPath null,fromPath="+path);
		}
		FTPClient client = null;
		try {
			client = getFtpClient(from.getServerid());
			return getFileInfoList(path, client);
		} finally {
			close(client);
		}
	}
	
	public static List<FileInfo> getFileInfoList(String path, FTPClient client)
			throws IOException {
		List<PathModel> list = FileUtil.getPathSegment(path);
		return getFileInfoList(list, 0, client);
	}

	public static List<FileInfo> getFileInfoList(List<PathModel> list, int index,
			FTPClient client) throws IOException {
		// 退出
		if (list == null || index < 0 || index >= list.size()) {
			return null;
		}
		// client.changeWorkingDirectory(rootPath);
		PathModel pm = list.get(index);
		// getFile
		List<FileInfo> pathList = new ArrayList<FileInfo>();
		if (index == list.size() - 1) {
			logger.debug("file==pm.getPath()==" + pm.getPath()
					+ "==pm.getNext()==" + pm.getNext());
			 
			FTPFile[] ftpFileArr = client.listFiles(pm.getPath(),
					new MyFTPFileFilter(pm.getNext()));
			if (ftpFileArr != null && ftpFileArr.length > 0) {
				for (FTPFile f : ftpFileArr) {
					if(f.getName().equals(".")||f.getName().equals("..")){
						continue;
					}
					String name = f.getName();
					String tempPath = pm.getPath() + "/" + name;
					//中文文件名的
					tempPath = new String(tempPath.getBytes("UTF-8"),
							"ISO-8859-1");
					FileInfo fileInfo = new FileInfo();
					fileInfo.setName(tempPath);
					fileInfo.setSize(f.getSize());
					fileInfo.setDate(DateUtil.format(f.getTimestamp().getTime(),"yyyy-MM-dd HH:mm:ss"));
					pathList.add(fileInfo);
				}
			}
			return pathList;
		}
		if (index < list.size() - 1) {
			// get dir
			logger.debug("dir==pm.getPath()==" + pm.getPath()
					+ "==pm.getNext()==" + pm.getNext());
			FTPFile[] ftpFileArr = client.listDirectories(pm.getPath());
			if (ftpFileArr != null && ftpFileArr.length > 0) {
				for (FTPFile f : ftpFileArr) {
					if(f.getName().equals(".")||f.getName().equals("..")){
						continue;
					}
					Pattern p = Pattern.compile("^" + pm.getNext() + "$");
					Matcher m = p.matcher(f.getName());
					if (m.find()) {
						String dirName = f.getName();
						list.get(index + 1).setPath(
								pm.getPath() + "/" + dirName);
						List<FileInfo> tempList = getFileInfoList(list, index + 1,
								client);
						if (tempList != null && tempList.size() > 0) {
							pathList.addAll(tempList);
						}
					}
				}
			}
			return pathList;
		}
		return null;
	}
}
