package com.david4.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * 1、存1000条，超过后删前面500条
 * 2、第一次取最后20条
 * 3、其他次从上一次取的最后一条到目前的最后一条，没有的话返回""
 * 正常情况下lastSize<=size 若总条数过一千了，被删了500条后lastSize-500<=size
 * @author hanxj
 *
 */
public class ListTest {

	public static List<String> infoList = Collections.synchronizedList(new LinkedList<String>());
	/**
	 * 第一次默认取20条
	 */
	public static final int DEFAULT_SIZE = 20;
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		for(int i=0;i<30;i++){
			list.add(Integer.toString(i));
		}
		infoList = list;
		
		print(list);
		list = list.subList(30, list.size());
		print(list);
//		System.out.println(get());
//		
//		
//		list = list.subList(list.size()/2, list.size());
//		for(int i=30;i<60;i++){
//			list.add(Integer.toString(i));
//		}
//		
//		System.out.println(get(30));
	}

	public static void print(List<String> list){
		System.out.println("list.size()="+list.size());
		for(String s:list){
			System.out.print(s+",");
		}
		System.out.println();
	} 
	
	public static String get(){
		int size = infoList.size();
		List<String> list = new LinkedList<String>();
		if(size>DEFAULT_SIZE){
			list = infoList.subList(size-DEFAULT_SIZE, size);
		}else{
			list = infoList;
		}
		StringBuilder sb = new StringBuilder();
		if(list!=null && list.size()>0){
			sb.append(size).append(",");
			for(int i=0;i<list.size();i++){
				sb.append(list.get(i)).append(",");
			}
		}
		return sb.toString();
	}
	
	public static String get(int lastSize){
		int size = infoList.size();
		if(lastSize>=size){
			return get();
		}
		List<String> list = new LinkedList<String>();
		list = infoList.subList(lastSize, size);
		StringBuilder sb = new StringBuilder();
		if(list!=null && list.size()>0){
			sb.append(size).append(",");
			for(int i=0;i<list.size();i++){
				sb.append(list.get(i)).append(",");
			}
		}
		return sb.toString();
	}
}
