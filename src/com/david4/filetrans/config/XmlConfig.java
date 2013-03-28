package com.david4.filetrans.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;

import com.david4.common.GlobalBase;
import com.david4.common.model.TaskModel;
import com.david4.filetrans.model.FileTransTaskModel;
import com.david4.filetrans.model.FileTransTaskModel.Delete;
import com.david4.filetrans.model.FileTransTaskModel.From;
import com.david4.filetrans.model.FileTransTaskModel.Move;
import com.david4.filetrans.model.FileTransTaskModel.To;
import com.david4.filetrans.model.ServerConfig;

public class XmlConfig extends  GlobalBase implements TaskConfig {
	/**
	 * true 已经初始化
	 * false 尚未初始化
	 */
	private static AtomicBoolean initFlag = new AtomicBoolean(false);
	private static final Lock lock = new ReentrantLock();
	/**
	 * server config map
	 * key为server id
	 */
	private  static Map<String,ServerConfig> serverConfigMap = new ConcurrentHashMap<String,ServerConfig>();
	/**
	 * Task配置map
	 * key为task id
	 */
	private  static Map<String,TaskModel> taskConfigMap = new ConcurrentHashMap<String,TaskModel>();
	/**
	 * 参数配置
	 */
	private  static Map<String,String> parameterMap = new ConcurrentHashMap<String,String>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new XmlConfig().init();
	}
	@Override
	public void init() {
		if(initFlag.get()){
			return;
		}
		lock.lock();
		if(initFlag.get()){
			return;
		}
		try{
			doInitTask();
			doInitUser();
		}catch(Exception e){
			logger.error("config init error,"+e.getMessage());
			e.printStackTrace();
		}finally{
			initFlag.set(true);
			lock.unlock();
		}
	}
	public void doInitTask() throws IOException, DocumentException{
		InputStream is = null;
		try{
			logger.info("task config init");
			ClassPathResource fsr =new ClassPathResource("tasks.xml");
			is = fsr.getInputStream();
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(is);
			Element root = document.getRootElement();
			Element common = root.element("common");
			//参数MAP
			Element params = common.element("params");
			if(params!=null){
				for (Iterator<?> i = params.elementIterator(); i.hasNext();) {
					Element element = (Element) i.next();
					logger.info(element.getName()+"=="+element.getTextTrim());
					parameterMap.put(element.getName(), element.getTextTrim());
				}
			}
			//server map
			Element servers = common.element("servers");
			if(servers!=null){
				for (Iterator<?> i = servers.elementIterator("servergroup"); i.hasNext();) {
					Element element = (Element) i.next();
					if(element!=null){
						for (Iterator<?> j = element.elementIterator("server"); j.hasNext();) {
							Element server = (Element) j.next();
							if(server!=null){
								String id = server.attribute("id").getValue();
								String host = server.element("host").getTextTrim();
								String port = server.element("port").getTextTrim();
								String name = server.element("name").getTextTrim();
								String password = server.element("password").getTextTrim();
								int p = 0;
								try{
									p = Integer.parseInt(port);
								}catch(Exception e){
									logger.warn("port config exception,port="+port);
									continue;
								}
								ServerConfig config = new ServerConfig();
								config.setHost(host);
								config.setPort(p);
								config.setUserName(name);
								config.setPassword(password);
								logger.info(id+"=="+config);
								serverConfigMap.put(id, config);
							}
						}
					}
				}
			}
			//task map
			Element tasks = root.element("tasks");
			if(tasks==null){
				return;
			}
			for (Iterator<?> i = tasks.elementIterator("task"); i.hasNext();) {
				Element task = (Element) i.next();
				String id = task.attributeValue("id");
				if(id==null){
					logger.error("task id null");
					continue;
				}
				String group = task.attributeValue("group");
				String desc = task.attributeValue("desc");
				String cron = task.attributeValue("cron");
				String nextId = task.attributeValue("nextId");
				FileTransTaskModel model = new FileTransTaskModel();
				model.setId(id);
				model.setDesc(desc);
				model.setGroup(group);
				model.setCron(cron);
				model.setNextId(nextId);
				//from 若存在多个from只取第一个
				Element from = task.element("from");
				if(from!=null){
					String type = from.attributeValue("type");
					String serverid = from.attributeValue("serverid");
					String path = from.elementTextTrim("path");
					From f = new FileTransTaskModel().new From();
					f.setPath(path);
					f.setType(type);
					f.setServerid(serverid);
					model.setFrom(f);
					//to
					List<To> tolist = new ArrayList<To>();
					for (Iterator<?> j = task.elementIterator("to"); j.hasNext();) {
						Element to = (Element) j.next();
						String type1 = to.attributeValue("type");
						String serverid1 = to.attributeValue("serverid");
						String path1 = to.elementTextTrim("path");
						To t = new FileTransTaskModel().new To();
						t.setPath(path1);
						t.setType(type1);
						t.setServerid(serverid1);
						tolist.add(t);
					}
					model.setTo(tolist);
					//move
					Element move = task.element("move");
					if(move!=null){
						String movePath = move.elementTextTrim("path");
						Move m = new FileTransTaskModel().new Move();
						m.setPath(movePath);
						model.setMove(m);
					}
				}
				//delete
				//delete 可以不依赖from单独存在
				List<Delete> deletelist = new ArrayList<Delete>();
				for (Iterator<?> j = task.elementIterator("delete"); j.hasNext();) {
					Element delete = (Element) j.next();
					String type1 = delete.attributeValue("type");
					String serverid1 = delete.attributeValue("serverid");
					String path1 = delete.elementTextTrim("path");
					Delete d = new FileTransTaskModel().new Delete();
					d.setPath(path1);
					d.setType(type1);
					d.setServerid(serverid1);
					deletelist.add(d);
				}
				model.setDelete(deletelist);
				taskConfigMap.put(id, model);
			}
		}finally{
			is.close();
		}
	}
	
	public void doInitUser() throws IOException, DocumentException{
		logger.info("user config init");
		InputStream is = null;
		try{
			ClassPathResource fsr =new ClassPathResource("user.xml");
			is = fsr.getInputStream();
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(is);
			Element root = document.getRootElement();
		}finally{
			if(is!=null){
				is.close();
			}
		}
	}
	@Override
	public TaskModel getTaskModel(String taskId) {
		init();
		return taskConfigMap.get(taskId);
	}

	@Override
	public Map<String, TaskModel> getAllTask() {
		init();
		return taskConfigMap;
	}

	@Override
	public String getParam(String key) {
		init();
		return parameterMap.get(key);
	}
	/**
	 * 将标识设为false，重新加载config文件
	 */
	@Override
	public void refresh() {
		initFlag.set(false);
	}

	@Override
	public ServerConfig getServerConfig(String serverId) {
		init();
		return serverConfigMap.get(serverId);
	}

}
