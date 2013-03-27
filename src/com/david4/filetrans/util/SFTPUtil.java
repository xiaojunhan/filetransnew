package com.david4.filetrans.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.david4.filetrans.model.FileTransTaskModel.Delete;
import com.david4.filetrans.model.FileTransTaskModel.From;
import com.david4.filetrans.model.FileTransTaskModel.To;
import com.david4.filetrans.model.ServerConfig;
@Component
public class SFTPUtil implements FileTransUtil{

	@Override
	public List<String> getPathList(From from) {
		// TODO Auto-generated method stub
		return null;
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

	
}
