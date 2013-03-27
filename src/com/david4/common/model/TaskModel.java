package com.david4.common.model;

public class TaskModel {
	/**
	 * 任务ID
	 */
	private String id;
	/**
	 * 任务描述
	 */
	private String desc;
	/**
	 * 所在组
	 */
	private String group;
	/**
	 * 定时时间配置
	 */
	private String cron;
	/**
	 * 下一个task的ID
	 * 用于执行完task后，继续执行下一个task
	 */
	private String nextId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public String getNextId() {
		return nextId;
	}
	public void setNextId(String nextId) {
		this.nextId = nextId;
	}
}
