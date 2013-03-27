package com.david4.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeanUtils;

import com.david4.test.T.PathModel;

public class FinalTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "E:\\workspace\\filetrans\\config\\tasks.xml";
		File inputXml = new File(path);
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(inputXml);
			Element root = document.getRootElement();
			for (Iterator<?> i = root.elementIterator(); i.hasNext();) {
				//T t = clz.newInstance();
				Element element = (Element) i.next();
				//System.out.println("=="+element.getName()+"==");
				if(element.getName().equals("task")){
					String frompath = element.element("frompath").getText().trim();
					//System.out.println("frompath=="+frompath);
					frompath = t("frompath",frompath);
					//System.out.println("frompath=="+frompath);
					
					String topath = element.element("topath").getText().trim();
				//	System.out.println("topath=="+topath);
		
					
					T t = new T();
					List<PathModel> list = t.print(frompath);
					List<File> fileList = new ArrayList<File>();
					fileList = t.getFile(list.get(0), list.get(1), list, 0);

//					String toPath = "D:/home/weblogic/test/${1}/${2}aa.${3}";
//					toPath = "/home/weblogic/test/${1}.substring(1,2)/${2}.replace('WX','22')aa${3}";
					for(File f:fileList){
						System.out.println("f.getPath()=="+f.getPath());
						String temp = t.getToPath(f.getPath(), topath, frompath);
					//	System.out.println("temp=="+temp);
						temp = t("topath",temp);
						//System.out.println("topath=="+topath);
						System.out.println("temp=="+temp);
					}
				}

			 
			}
		} catch (Exception e) {
			e.printStackTrace();
			//logger.error(e.getMessage(), e);
			//return null;
		}

	}

	public static String t(String key,String biaodashi){
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine engine = sem.getEngineByExtension("js");
		try {
			engine.eval(biaodashi);
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}
		return (String)engine.get(key);
	}
}
