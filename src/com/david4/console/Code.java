package com.david4.console;

public class Code {
	public static final String CODE = "CODE"; 
	public static final String MESSAGE = "MESSAGE"; 
	
	public static final String CODE_SUCCESS = "0000"; 
	public static final String CODE_ERROR = "9999"; 
	public static final String CODE_NONAME = "0001"; 
	public static final String CODE_PASERR = "0002"; 
	public static final String CODE_NOROLE = "0003"; 
	public static final String CODE_RUNNING = "0004"; 
	public static final String CODE_NOTASK = "0005";
	public static final String CODE_FILEMPTY = "0006"; 
	public static final String CODE_START = "0007"; //用于异步时
	
	public static final String MESSAGE_SUCCESS = "执行成功"; 
	public static final String MESSAGE_NONAME = "用户名不存在"; 
	public static final String MESSAGE_PASERR = "密码错误"; 
	public static final String MESSAGE_NOROLE = "没有执行该任务的权限"; 
	public static final String MESSAGE_RUNNING = "该任务在执行中,稍候再试"; 
	public static final String MESSAGE_NOTASK = "任务不存在"; 
	public static final String MESSAGE_FILEMPTY = "没有找到文件"; 
	public static final String MESSAGE_ERROR = "执行失败"; 
	public static final String MESSAGE_START = "任务开始执行"; 
}
