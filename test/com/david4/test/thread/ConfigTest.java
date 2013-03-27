package com.david4.test.thread;

import java.io.IOException;

import org.dom4j.DocumentException;

import com.david4.filetrans.Config;
import com.david4.filetrans.config.XmlConfig;

public class ConfigTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int i=0;i<10;i++){
			new ConfigThread().start();
		}
//		System.out.println(XmlConfig.initFlag1);
//		System.out.println(XmlConfig.initFlag2);
//		System.out.println(XmlConfig.initFlag3);
	}

	public static class ConfigThread extends Thread{
		XmlConfig xmlConfig = new XmlConfig();
		@Override
		public void run() {
			//Config.getLocalPathTemp();
			 
				xmlConfig.init();
			 
		}
		
	}
}
