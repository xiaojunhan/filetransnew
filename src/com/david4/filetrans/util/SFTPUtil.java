package com.david4.filetrans.util;

import java.util.List;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.david4.common.model.PathModel;
import com.david4.common.util.FileUtil;
import com.david4.filetrans.config.TaskConfig;
import com.david4.filetrans.model.FileInfo;
import com.david4.filetrans.model.FileTransTaskModel.Delete;
import com.david4.filetrans.model.FileTransTaskModel.From;
import com.david4.filetrans.model.FileTransTaskModel.To;
import com.david4.filetrans.model.ServerConfig;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
@Component
public class SFTPUtil implements FileTransUtil{
	private static final Logger logger = LoggerFactory
			.getLogger(SFTPUtil.class);
	
	@Autowired
	@Qualifier("taskConfig")
	private TaskConfig taskConfig;
	
	@Override
	public List<String> getPathList(From from) throws Exception {
		String path = from.getPath();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void put(To to, String toPath, String localPath) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(ServerConfig serverConfig, String fromPath, String toPath)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	public List<String> getFileList(String path,ChannelSftp client){
		List<PathModel> list = FileUtil.getPathSegment(path);
		for(PathModel p : list){
			System.out.println(p);
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
			System.out.println("Session created.");
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			System.out.println("Session connected.");
			System.out.println("Opening Channel.");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			System.out.println("Connected to " + host + ".");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sftp;
	}
	
	public void close(ChannelSftp client){
		try{
			if(client!=null){
				client.disconnect();
			}
		}catch(Exception e){
			logger.error("disconnect error "+e.getMessage());
		}
	}

	@Override
	public List<FileInfo> getFileInfoList(From from) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
