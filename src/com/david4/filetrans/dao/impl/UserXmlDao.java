package com.david4.filetrans.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
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
import com.david4.filetrans.dao.UserDao;
import com.david4.filetrans.model.User;

public class UserXmlDao extends GlobalBase implements UserDao {
	/**
	 * true 已经初始化 false 尚未初始化
	 */
	private static AtomicBoolean initFlag = new AtomicBoolean(false);
	private static final Lock lock = new ReentrantLock();

	private static Map<String, User> userMap = new ConcurrentHashMap<String, User>();

	@Override
	public User getUser(String name) {
		init();
		return userMap.get(name);
	}

	public void doInitUser() throws IOException, DocumentException {
		InputStream is = null;
		try {
			logger.info("user config init");
			ClassPathResource fsr = new ClassPathResource("user.xml");
			is = fsr.getInputStream();
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(is);
			Element root = document.getRootElement();
			for (Iterator<?> i = root.elementIterator("user"); i.hasNext();) {
				Element userElement = (Element) i.next();
				String name = userElement.elementTextTrim("name");
				String password = userElement.elementTextTrim("password");
				String task = userElement.elementTextTrim("task");
				User user = new User();
				user.setName(name);
				user.setPassword(password);
				user.setTasks(task);
				userMap.put(name, user);
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public void init() {
		if (initFlag.get()) {
			return;
		}
		lock.lock();
		if (initFlag.get()) {
			return;
		}
		try {
			doInitUser();
			// doInitUser();
		} catch (Exception e) {
			logger.error("config init error," + e.getMessage());
			e.printStackTrace();
		} finally {
			initFlag.set(true);
			lock.unlock();
		}
	}
}
