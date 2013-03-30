package com.david4.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.david4.common.model.PathModel;

public class T {
	public class PathModel{
		private String path;
		private String next;
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public String getNext() {
			return next;
		}
		public void setNext(String next) {
			this.next = next;
		}
		@Override
		public String toString() {
			return "PathModel [path=" + path + ", next=" + next + "]";
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		File file = new File("");
//		String[] str = file.list();
//		System.out.println(str);
		String s = "/D://home/ftp/qcbank/CMB/acc//";
		s="D:\\home\\ftp\\qcbank\\CMB\\acc";
		s="D:/home/ftp/qcbank/CMB/acc";
		s="D:/home/ftp/qcbank/(.*?)/acc/(.*)\\.(.*?)";
	//	s="/s";
		T t = new T();
		List<PathModel> list = t.print(s);
		for(PathModel p:list){
			System.out.println(p);
		}
//		List<File> fileList = new ArrayList<File>();
//		fileList = t.getFile(list.get(0), list.get(1), list, 0);
//
//		String toPath = "D:/home/weblogic/test/${1}/${2}aa.${3}";
//		toPath = "/home/weblogic/test/${1}.substring(1,2)/${2}.replace('WX','22')aa${3}";
//		for(File f:fileList){
//			System.out.println("f.getPath()=="+f.getPath());
//			String temp = getToPath(f.getPath(), toPath, s);
//			System.out.println("temp=="+temp);
//		}
		
		
	}
	
	public static String getToPath(String from,String to,String reg){
		from = from.replaceAll("\\\\+", "/");
	//	System.out.println(from+"====="+reg);
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
			to = to.replaceAll("\\$\\{"+m1.group(1)+"\\}", "\""+strArr[Integer.parseInt(m1.group(1))-1]+"\"");
		}
		return to;
	}
	
	//D:/home/ftp/q(.*?)/(.*?)/acc/(.*)\\.(.*?)
	public List<File> getFile(PathModel pm,PathModel next,List<PathModel> list,int index){
	//	System.out.println("==="+pm.getPath()+"=="+pm.getNext());
		List<File> tempList = new ArrayList<File>();
		File file = new File(pm.getPath());
		if(file.isFile()){
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
	
	public  List<PathModel> print(String path){
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
			PathModel pm = new PathModel();
			pm.setPath(value);
			pm.setNext(next);
			list.add(pm);
		}
		return list;
	}
}
