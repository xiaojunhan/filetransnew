package com.david4.filetrans.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileTrans {
	/**
	 * 
	 * @param url=http://localhost:8080/filetrans/console/run.jhtml
	 * @param taskId=9
	 * @param name=qjs
	 * @param password=qjs
	 * @param async true 同步 false 异步
	 * @return
	 */
	public static String doTask(String url,String taskId,String name,String password,int timeout,boolean async){
		//http://localhost:8080/filetrans/console/run.jhtml?taskId=9&name=qjs&password=qjs
		url = url+"?taskId="+taskId+"&name="+name+"&password="+password+"&async="+async;
		return getContent(url, "UTF-8",timeout);
	}
	
	public static String getContent(String url, String code,int timeout) {
		HttpURLConnection connect = null;
		try {
			URL myurl = new URL(url);
			connect = (HttpURLConnection) myurl.openConnection();
			connect.setConnectTimeout(timeout);//10分钟
			connect.setReadTimeout(timeout);
//			connect.setRequestProperty(
//					"User-Agent",
//					"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13");
			return convertStreamToString(connect.getInputStream(),
					code);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connect != null) {
				connect.disconnect();
			}
		}
		return null;
	}
	
	public static String convertStreamToString(InputStream is, String code) {
		if (isEmpty(code)) {
			code = "gbk";
		}
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(is, code));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
}
