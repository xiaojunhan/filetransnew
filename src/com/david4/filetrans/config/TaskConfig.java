package com.david4.filetrans.config;

import java.util.Map;

import com.david4.common.model.TaskModel;
import com.david4.filetrans.model.ServerConfig;

public interface TaskConfig {
	/**
	 * 初始状态
	 */
	public static final int DEFAULT =0;
	/**
	 * 运行中
	 */
	public static final int RUNNING =1;
	/**
	 * 已完成
	 */
	public static final int COMPLETE =2;
	/**
	 * 失败
	 */
	public static final int ERROR =3;
	/**
	 * 初始化
	 */
	public void init() ;
	/**
	 * 获取task
	 * @param taskId
	 * @return
	 */
	public  TaskModel getTaskModel(String taskId);
	/**
	 * 获取所有Task
	 * @return
	 */
	public Map<String,TaskModel> getAllTask();
	/**
	 * 获取参数
	 * @param key
	 * @return
	 */
	public  String getParam(String key);
	/**
	 * 获取服务器信息
	 * @param serverId
	 * @return
	 */
	public ServerConfig getServerConfig(String serverId);
	/**
	 * 刷新配置
	 */
	public  void refresh();

}
