package com.david4.filetrans.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.david4.common.model.PathModel;
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
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Component
public class SFTPUtil implements FileTransUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(SFTPUtil.class);

	@Autowired
	@Qualifier("taskConfig")
	private TaskConfig taskConfig;

	@Override
	public List<FileInfo> getFileInfoList(From from) throws Exception {
		String path = from.getPath();
		if (path == null || path.trim().length() == 0) {
			throw new Exception("fromPath null,fromPath=" + path);
		}
		path = ScriptUtil.getString(path, Constants.SCRIPT_PATH);
		if (path == null || path.trim().length() == 0) {
			throw new Exception("fromPath null,fromPath=" + path);
		}
		ChannelSftp client = null;
		try {
			client = getChannelSftp(from.getServerid());
			return getFileInfoList(path, client);
		} finally {
			close(client);
		}
	}

	@Override
	public void delete(Delete delete, String deletePath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void get(From from, String fromPath, String localPath)
			throws Exception {
		ChannelSftp client = null;
		try {
			client = getChannelSftp(from.getServerid());
			client.get(fromPath, localPath);
		} finally {
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
		ChannelSftp client = null;
		try {
			client = getChannelSftp(to.getServerid());
			mkdirs(getFtpDir(toPath), client);
			client.put(localPath, toPathTemp);
			try{
				client.rename(toPathTemp, toPath);
			}catch(SftpException e){
				try{
					client.rm(toPath);
					try{
						client.rename(toPathTemp, toPath);
					}catch(SftpException e1){
						client.rm(toPathTemp);
						throw new Exception("rename error");
					}
				}catch(SftpException e1){
					client.rm(toPathTemp);
					throw new Exception("delete error");
				}
			}
		} finally {
			close(client);
		}
	}

	@Override
	public void move(ServerConfig serverConfig, String fromPath, String toPath)
			throws Exception {
		ChannelSftp client = null;
		try {
			client = getChannelSftp(serverConfig);
			mkdirs(getFtpDir(toPath), client);
			try{
				client.rename(fromPath, toPath);
			}catch(SftpException e){
				try{
					client.rm(toPath);
					try{
						client.rename(fromPath, toPath);
					}catch(SftpException e1){
						throw new Exception("move rename error");
					}
				}catch(SftpException e1){
					throw new Exception("move delete error");
				}
			}
		} finally {
			close(client);
		}
	}

	public List<FileInfo> getFileInfoList(String path, ChannelSftp client)
			throws IOException, SftpException {
		List<PathModel> list = FileUtil.getPathSegment(path);
		return getFileInfoList(list, 0, client);
	}

	public static List<FileInfo> getFileInfoList(List<PathModel> list, int index,
			ChannelSftp client) throws IOException, SftpException {
		// 退出
		if (list == null || index < 0 || index >= list.size()) {
			return null;
		}
		PathModel pm = list.get(index);
		// getFile
		List<FileInfo> pathList = new ArrayList<FileInfo>();
		if (index == list.size() - 1) {
			logger.info("file==pm.getPath()==" + pm.getPath()
					+ "==pm.getNext()==" + pm.getNext());
			Vector<?> vector = client.ls(pm.getPath());
			if (vector != null && vector.size() > 0) {
				for (int i = 0; i < vector.size(); i++) {
					LsEntry entry = (LsEntry) vector.get(i);
					if (entry.getFilename().equals(".")
							|| entry.getFilename().equals("..")) {
						continue;
					}
					Pattern p = Pattern.compile("^" + pm.getNext() + "$");
					Matcher m = p.matcher(entry.getFilename());
					if (m.find()) {
						String name = entry.getFilename();
						String tempPath = pm.getPath() + "/" + name;
						// 中文文件名的
						tempPath = new String(tempPath.getBytes("UTF-8"),
								"ISO-8859-1");
						FileInfo fileInfo = new FileInfo();
						fileInfo.setName(tempPath);
						fileInfo.setSize(entry.getAttrs().getSize());
//						System.out.println(entry.getAttrs().getATime() +"==="+entry.getAttrs().getMTime());
//						System.out.println(entry.getAttrs().getAtimeString() +"==="+entry.getAttrs().getMtimeString());
//						Date d = new Date(entry.getAttrs().getMTime());
					//	fileInfo.setDate(DateUtil.format(d,"yyyy-MM-dd HH:mm:ss"));
						fileInfo.setDate(formatDate(entry.getAttrs().getMtimeString()));
						pathList.add(fileInfo);
					}
				}
			}
			return pathList;
		}
		if (index < list.size() - 1) {
			// get dir
			logger.info("dir==pm.getPath()==" + pm.getPath()
					+ "==pm.getNext()==" + pm.getNext());
			Vector<?> vector = client.ls(pm.getPath());
			if (vector != null && vector.size() > 0) {
				for (int i = 0; i < vector.size(); i++) {
					LsEntry entry = (LsEntry) vector.get(i);
					if (entry.getFilename().equals(".")
							|| entry.getFilename().equals("..")) {
						continue;
					}
					Pattern p = Pattern.compile("^" + pm.getNext() + "$");
					Matcher m = p.matcher(entry.getFilename());
					if (m.find()) {
						String dirName = entry.getFilename();
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

	public ChannelSftp getChannelSftp(String serverId) throws Exception {
		ServerConfig config = taskConfig.getServerConfig(serverId);
		ChannelSftp client = getChannelSftp(config);
		if (client == null) {
			throw new Exception("connect and login ftp error," + config);
		}
		return client;
	}

	public ChannelSftp getChannelSftp(ServerConfig ftpconfig) throws Exception{
		if (ftpconfig == null) {
			logger.warn("ftpconfig is null");
			return null;
		}
		return getChannelSftp(ftpconfig.getHost(), ftpconfig.getPort(),
				ftpconfig.getUserName(), ftpconfig.getPassword());
	}

	public ChannelSftp getChannelSftp(String host, int port, String username,
			String password)throws Exception {
			ChannelSftp sftp = null;
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			Session sshSession = jsch.getSession(username, host, port);
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		return sftp;
	}

	public void close(ChannelSftp client) {
		try {
			if (client != null) {
				client.disconnect();
			}
		} catch (Exception e) {
			logger.error("disconnect error " + e.getMessage());
		}
	}
	public static String getFtpDir(String path) {
		if(path.lastIndexOf("/")==-1){
			return null;
		}
		path = path.substring(0, path.lastIndexOf("/"));
		return path;
	}
	public static String getTempName(){
		int random = NumberUtil.getRandom(100000, 999999);
		return Long.toString(System.nanoTime())+random+"-do-not-delete";
	}
	
	public static void mkdirs(String dir, ChannelSftp ftp) throws IOException, SftpException {
		if (dir == null) {
			return;
		}
		dir = dir.replaceAll("/+", "/");
		String sdir = ftp.pwd().replaceAll("\"", "");

		// 目录已存在，直接返回
		try{
			ftp.cd(dir);
			ftp.cd(sdir);
			return;
		}catch(SftpException e){
			
		}

		ftp.cd(sdir);
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
				ftp.cd(sdir);
				break;
			}
			String temp = dir.substring(start, end);
			try{
				ftp.cd(temp);
			}catch(SftpException e){
				try{
					ftp.mkdir(temp);
					ftp.cd(temp);
				}catch(SftpException e1){
					ftp.cd(sdir);
					throw new IOException("创建目录失败,确定用户是否有创建目录的权限,dir=" + dir);
				}
			}
			start = end + 1;
			end = dir.indexOf("/", start);
			if (end <= start) {
				ftp.cd(sdir);
				break;
			}
		}
		ftp.cd(sdir);
	}
	
	public static String formatDate(String s){
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.US);
        try {
            TimeZone tz=TimeZone.getTimeZone("US/Central");
            sdf.setTimeZone(tz);
            java.util.Date date= sdf.parse(s);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("GMT-6:00"));
            return format1.format(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
	}
}
