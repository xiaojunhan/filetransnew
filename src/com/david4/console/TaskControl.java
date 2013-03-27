package com.david4.console;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author hanxj
 *
 */
public class TaskControl {
	private static final Integer STATE_RUNNING = 1;
	private static final Integer STATE_COMPLETE = 2;
	/**
	 * task MAP
	 * key taskId
	 * value task state 1 running 2 stop
	 */
	private static ConcurrentHashMap<String,Integer> taskMap = new ConcurrentHashMap<String,Integer>();
	/**
	 * confirm task can run
	 * @param taskId
	 * @return true:can run false:can't run
	 */
	public static boolean canRun(String taskId){
		return taskMap.put(taskId,STATE_RUNNING)!=STATE_RUNNING;
	}
	/**
	 * task complete
	 * @param taskId
	 */
	public static void complete(String taskId){
		taskMap.put(taskId, STATE_COMPLETE);
	}
//	/**
//	 * task start
//	 * @param taskId
//	 */
//	public static void start(int taskId){
//		taskMap.put(taskId, STATE_RUNNING);
//	}
	
	public static String getRunningTask(){
		StringBuilder sb = new StringBuilder();
		if(taskMap.size()>0){
			for(Entry<String,Integer> entry:taskMap.entrySet()){
				if(entry.getValue()==STATE_RUNNING){
					sb.append(entry.getKey()).append(",");
				}
			}
		}
		return sb.toString();
	}
	/**
	 * 返回true 有在运行的task
	 * 返回false 没有在运行的task
	 * @return
	 */
	public static boolean hasRunningTask(){
		return !getRunningTask().equals("");
	}
}
