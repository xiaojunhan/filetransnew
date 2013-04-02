package com.david4.filetrans.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
	public List<String> getPathList(From from) throws Exception {
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
			return getFileList(path, client);
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
		ChannelSftp client = null;
		try {
			client = getChannelSftp(to.getServerid());
			client.put(localPath, toPath);
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
			client.rename(fromPath, toPath);
		} finally {
			close(client);
		}
	}

	public List<String> getFileList(String path, ChannelSftp client)
			throws IOException, SftpException {
		List<PathModel> list = FileUtil.getPathSegment(path);
		return getFileList(list, 0, client);
	}

	public static List<String> getFileList(List<PathModel> list, int index,
			ChannelSftp client) throws IOException, SftpException {
		// 退出
		if (list == null || index < 0 || index >= list.size()) {
			return null;
		}
		// client.changeWorkingDirectory(rootPath);
		PathModel pm = list.get(index);
		// getFile
		List<String> pathList = new ArrayList<String>();
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
						pathList.add(tempPath);
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
						List<String> tempList = getFileList(list, index + 1,
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

	public ChannelSftp getChannelSftp(ServerConfig ftpconfig) {
		if (ftpconfig == null) {
			logger.warn("ftpconfig is null");
			return null;
		}
		return getChannelSftp(ftpconfig.getHost(), ftpconfig.getPort(),
				ftpconfig.getUserName(), ftpconfig.getPassword());
	}

	public ChannelSftp getChannelSftp(String host, int port, String username,
			String password) {
		ChannelSftp sftp = null;
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	@Override
	public List<FileInfo> getFileInfoList(From from) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
