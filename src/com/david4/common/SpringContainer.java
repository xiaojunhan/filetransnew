package com.david4.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContainer implements ApplicationContextAware {

	public SpringContainer() {
		super();
	}

	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext = arg0;
	}

	/**
	 * 根据spring配置文件中的id获取对象
	 * 
	 * @param id
	 *            bean的id
	 * @return 对象
	 */
	public static Object getBean(String id) {
		return applicationContext.getBean(id);
	}

	public static <T> T getBean(Class<T> clz) {
		return applicationContext.getBean(clz);
	}

	public static <T> T getBean(String id, Class<T> clz) {
		return applicationContext.getBean(id, clz);
	}

}
