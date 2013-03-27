package com.david4.common.util;

import java.io.File;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * js脚本简单解析
 * @author hanxj
 *
 */
public class ScriptUtil {
	private  static final Logger logger = LoggerFactory.getLogger(ScriptUtil.class);
	
	private static ScriptEngineManager sem = new ScriptEngineManager();
	private static ScriptEngine engine = sem.getEngineByExtension("js");

	static{
		String path = ScriptUtil.class.getResource("/").toString();
		path = path.substring(5);
		path = path + "common.js";
		File jsFile = new File(path);
		try{
			FileReader fr = new FileReader(jsFile);
			engine.eval(fr);
		}catch(Exception e){
			logger.error("js init error "+e.getMessage());
		}
	}
	public static Object get(String script, String key) {
		logger.trace("script="+script);
		try {
			engine.eval(script);
		} catch (ScriptException ex) {
			ex.printStackTrace();
			return null;
		}
		return engine.get(key);
	}

	public static String getString(String script, String key) {
		return (String) get(script, key);
	}

	public static Boolean getBoolean(String script, String key) {
		return (Boolean) get(script, key);
	}
	
	public static void main(String[] args){
//		init();
		String script = "var frompath = \"D:/ftp/batch/qcbank/(.*?)/acc/(.*?)\";";
		script = "var frompathbak = \"D:/ftp/batch/qcbankbak/${1}/acc/${2}\";";
		script = "var frompath = \"bank/(.*?)/acc/(WXSMK-QC-.*?-\";frompath = frompath+getTodayDate();frompath = frompath+\"(-TZ)?\\..*?)\";";
		System.out.println(getString(script, "frompath"));
	}
}
