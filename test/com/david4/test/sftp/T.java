package com.david4.test.sftp;

import java.io.IOException;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class T {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SftpException 
	 */
	public static void main(String[] args) throws IOException, SftpException {
		String host = "127.0.0.1";
		int port = 22;
		String username = "test1";
		String password = "test1";
		ChannelSftp  channelSftp = null;
		String dir = "e/kj";
		try{
			channelSftp = getChannelSftp(host, port, username, password);
			mkdirs(dir, channelSftp);
		}finally{
			System.out.println("=====end1====");
			close(channelSftp);
		}
		System.out.println("=====end2====");
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
	public static ChannelSftp getChannelSftp(String host, int port, String username,
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

	public static void close(ChannelSftp client) {
		try {
			if (client != null) {
				client.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
