//package com.david4.test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPFile;
//
//import com.david4.common.filter.MyFTPFileFilter;
//import com.david4.common.model.PathModel;
//import com.david4.common.util.FTPUtil;
//import com.david4.common.util.FileUtil;
//import com.david4.filetrans.model.FTPConfig;
//
//public class FTPGetListTest {
//
//	/**
//	 * @param args
//	 * @throws IOException 
//	 */
//	public static void main(String[] args) throws IOException {
//		//String path = "/home/weblogic/test/offlinefiles/send/(\\d{11})/((FH|SS|SN|SR|ER|BL)\\w{34}|(FH|SS|SN|SR|ER|BL)\\w{23})";
//		String path = "/test/offlinefiles/send/(\\d{11})/((FH|SS|SN|SR|ER|BL)\\w{34}|(FH|SS|SN|SR|ER|BL)\\w{23})";
//		
//		String rootPath = "/home/weblogic/";
//		FTPConfig ftpconfig = new FTPConfig();
//		ftpconfig.setHost("10.1.12.20");
//		ftpconfig.setPort(21);
//		ftpconfig.setUserName("weblogic");
//		ftpconfig.setPassword("weblogic");
//		//ftpconfig.setRootpath("/home/weblogic/");
//		FTPClient client = FTPUtil.getFtpClient(ftpconfig);
//		List<String> list = getFileList(path, client);
//		if(list!=null && list.size()>0){
//			for(String s : list){
//				System.out.println("=="+s);
//			}
//		}else{
//			System.out.println("file empty");
//		}
//	}
//
//	public static List<String> getFileList(String path,FTPClient client) throws IOException{
//		List<PathModel> list = FileUtil.getPathSegment(path);
//		return getFileList(list,0,client);
//	}
//	public static List<String> getFileList(List<PathModel> list,int index,FTPClient client) throws IOException{
//		//退出
//		if(list==null || index < 0 || index>=list.size()){
//			return null;
//		}
//		//client.changeWorkingDirectory(rootPath);
//		PathModel pm = list.get(index);
//		//getFile
//		List<String> pathList = new ArrayList<String>();
//		if(index == list.size()-1){
//			//System.out.println("file==pm.getPath()=="+pm.getPath()+"=="+pm.getNext());
//			FTPFile[] ftpFileArr = client.listFiles(pm.getPath(), new MyFTPFileFilter(pm.getNext()));
//			if(ftpFileArr!=null && ftpFileArr.length>0){
//				for(FTPFile f:ftpFileArr){
//					String name = f.getName();
//					pathList.add(pm.getPath()+"/"+name);
//				}
//			}
//			return pathList;
//		} 
//		if(index<list.size()-1){
//			//get dir
//			//logger.("dir==pm.getPath()=="+pm.getPath()+"=="+pm.getNext());
//			FTPFile[] ftpFileArr = client.listDirectories(pm.getPath());
//			if(ftpFileArr!=null && ftpFileArr.length>0){
//				for(FTPFile f:ftpFileArr){
//					Pattern p = Pattern.compile("^"+pm.getNext()+"$");
//					Matcher m = p.matcher(f.getName());
//					if(m.find()){
//						String dirName = f.getName();
//						list.get(index+1).setPath(pm.getPath()+"/"+dirName);
//						List<String> tempList = getFileList(list,index+1,client);
//						if(tempList!=null && tempList.size()>0){
//							pathList.addAll(tempList);
//						}
//					}
//				}
//			}
//			return pathList;
//		}
//		return null;
//	}
//	
//}
