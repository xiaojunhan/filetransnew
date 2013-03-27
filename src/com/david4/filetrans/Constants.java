package com.david4.filetrans;

public interface Constants {
	/**
	 * 配置脚本中路径变量名
	 */
	public static final String SCRIPT_PATH = "path";
	/**
	 * 操作FTP上文件的task
	 */
	public static final String TYPE_TASK_FTP = "ftptask";
	/**
	 * 操作SFTP上文件的task
	 */
	public static final String TYPE_TASK_SFTP = "sftptask";
//	/**
//	 * 获取文件、下载文件
//	 */
//	public static final String FILE_FLOW_GET = "get";
//	/**
//	 * 上传文件
//	 */
//	public static final String FILE_FLOW_PUT = "put";
//	/**
//	 * 删除文件
//	 */
//	public static final String FILE_FLOW_DEL = "delete";
	
	
	
	/**
	 * 执行失败
	 */
	public static final int ERROR = -1;
}
