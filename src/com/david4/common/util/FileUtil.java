package com.david4.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.david4.common.filter.MyNameFilter;
import com.david4.common.model.PathModel;



public class FileUtil {
	private  static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	public static void main(String[] args){
		//D:\ftp\batch\qbbank\CMB\acc
		String path = "D:/ftp/batch/qcbank/(.*?)/acc/(.*?)/(.*?)";
//		path = "D:/home/ftp/qcbank/(.*?)/acc/(.*)\\.(.*?)";
		List<File> list = getFileList(path);
		for(File f:list){
			System.out.println("=="+f.getPath());
		}
	}
	
	public static List<File> getFileList(String path){
		List<PathModel> segmentList = FileUtil.getPathSegment(path);
		if(segmentList==null || segmentList.size()==0){
			return null;
		}
		List<File> fileList = new ArrayList<File>();
		if(segmentList.size()>1){
			fileList = getFile(segmentList.get(0), segmentList.get(1), segmentList, 0);
		}else{
			fileList = getFile(segmentList.get(0), null, segmentList, 0);
		}
		return fileList;
	}
	
	public static  List<PathModel> getPathSegment(String path){
		if(path==null){
			return null;
		}
		List<PathModel> list = new ArrayList<PathModel>();
//		path=path.replaceAll("\\\\+", "/");
		path=path.replaceAll("/+", "/");
		String[] str = path.split("/");
		int length = str.length;
		for(int i=0,j=0;i<length;i++){
			int index = path.indexOf("/",j);
			String value = null;
			if(index==0){
				j=j+1;
				continue;
			}else 
			if(index==-1){
				value = path;
			}else{
				value = path.substring(0,index);
			}
			j=value.length()+1;
			String next = null;
			int nextIndex = path.indexOf("/",j);
			if(nextIndex==-1 && index!=-1){
				next = path.substring(index+1,path.length());
			}else if(nextIndex>=0){
				next = path.substring(index+1,nextIndex);
			}
			if(next!=null){
				PathModel pm = new PathModel();
				//pm.setPath(value.replaceFirst("/+", ""));
				//用于去掉第一位的/
				if(value.indexOf("/")==0){
					value = value.substring(1);
				}
				pm.setPath(value);
				pm.setNext(next);
				list.add(pm);
			}
			
		}
		return list;
	}
	
	
	public static List<File> getFile(PathModel pm,PathModel next,List<PathModel> list,int index){
		//	System.out.println("==="+pm.getPath()+"=="+pm.getNext());
			List<File> tempList = new ArrayList<File>();
			File file = new File(pm.getPath());
			if(file.isFile() && index==list.size()-1){
				tempList.add(file);
			}else if(file.isDirectory()){
				if(pm.getNext()==null || next == null){
					return tempList;
				}
				//System.out.println(pm.getPath()+"=="+pm.getNext());
				File[] fileArr = file.listFiles(new MyNameFilter(pm.getNext()));
				if(fileArr!=null && fileArr.length>0){
					for(File f:fileArr){
						String nextPath = pm.getPath()+"/"+f.getName();
						//System.out.println("nextPath22==="+nextPath+"=index"+index+"==list.size=="+list.size());
						next.setPath(nextPath);
						if(index+2>=list.size()){
							tempList.addAll(getFile(next,null,list,index+1));
						}else{
							tempList.addAll(getFile(next,list.get(index+2),list,index+1));
						}
					}
				}
			}
			return tempList;
	}
		
	public static String getPath(String from,String to,String reg) throws Exception{
		from = from.replaceAll("\\\\+", "/");
		Pattern p = Pattern.compile("^"+reg+"$");
		Matcher m = p.matcher(from);
		String[] strArr = null;
		if(m.find()){
			int count = m.groupCount();
			strArr = new String[count];
			for(int i=0;i<count;i++){
				strArr[i] = m.group(i+1);
			}
		}
		Pattern p1 = Pattern.compile("\\$\\{(\\d+)\\}");
		Matcher m1 = p1.matcher(to);
		while(m1.find()){
			String value = m1.group(1);
			int index = Integer.parseInt(value);
			if(strArr==null || strArr.length<index){
				logger.error("from="+from);
				logger.error("to="+to);
				logger.error("reg="+reg);
				throw new Exception("目标路径中需要的参数超过源路径中提供的数量");
			}
			//to = to.replaceAll("\\$\\{"+value+"\\}", "\""+strArr[index-1]+"\"");
			to = to.replaceAll("\\$\\{"+value+"\\}", strArr[index-1]);
		}
		return to;
	}
}
