package com.david4.console;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 控制台信息
 * 1、存1000条，超过后删前面500条
 * 2、第一次取最后20条
 * 3、其他次从上一次取的最后一条到目前的最后一条，没有的话返回""
 * 正常情况下lastSize<=size 若总条数过一千了，被删了500条后lastSize-500<=size
 * 可能遇到的情况，这次和上一次之间发生了多次超过1000被截取的情况，页面会遗漏信息。
 * @author hanxj
 *
 */
public class TaskInfo {
	//TODO 信息 日志 分组存放，目前都放一起了，配置删除过期的日志。logback支持这个配置即可。
	private  static final Logger logger = LoggerFactory.getLogger(TaskInfo.class);
	public static final String FEN_GE = ";_,";
//	public static List<String> infoList = Collections.synchronizedList(new LinkedList<String>());
	public static Map<String,List<String>> infoMap = new ConcurrentHashMap<String,List<String>>();
	/**
	 * 第一次默认取20条
	 */
	public static final int DEFAULT_SIZE = 20;
	public static final int TOTAL_SIZE = 1000;
	public static void log(String msg){
		log("0", msg);
	}
	public static void log(String group,String msg){
		List<String> infoList = infoMap.get(group);
		if(infoList==null){
			infoList = Collections.synchronizedList(new LinkedList<String>());
		}
		if(infoList.size()>=TOTAL_SIZE){
			infoList = infoList.subList(infoList.size()/2, infoList.size());
		}
		infoList.add(getDate() + " " + msg);
		infoMap.put(group, infoList);
		logger.info(msg);
	}
	
	public static String get(String group){
		List<String> infoList = infoMap.get(group);
		if(infoList==null){
			return "0"+FEN_GE;
		}
		int size = infoList.size();
		List<String> list = new LinkedList<String>();
		if(size>DEFAULT_SIZE){
			list = infoList.subList(size-DEFAULT_SIZE, size);
		}else{
			list = infoList;
		}
		StringBuilder sb = new StringBuilder();
		if(list!=null && list.size()>0){
			sb.append(size).append(FEN_GE);
			for(int i=0;i<list.size();i++){
				sb.append(list.get(i)).append(FEN_GE);
			}
		}
		return sb.toString();
	}
	
	public static String get(int lastSize,String group){
		List<String> infoList = infoMap.get(group);
		if(infoList==null){
			return "0"+FEN_GE;
		}
		int size = infoList.size();
		if(size==0){
			return "0"+FEN_GE;
		}
		if(lastSize==0){
			return get(group);
		}
		if(lastSize>size){
			lastSize = lastSize - TOTAL_SIZE/2;
		}
		if(lastSize<0){
			lastSize=0;
		}
		List<String> list = infoList.subList(lastSize, size);
		StringBuilder sb = new StringBuilder();
		if(list!=null && list.size()>0){
			sb.append(size).append(FEN_GE);
			for(int i=0;i<list.size();i++){
				sb.append(list.get(i)).append(FEN_GE);
			}
		}
		return sb.toString();
	}
    public static String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
