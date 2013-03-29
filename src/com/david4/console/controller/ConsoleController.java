package com.david4.console.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.david4.common.BaseController;
import com.david4.common.exception.FromFileEmptyException;
import com.david4.common.exception.TaskNotExsitException;
import com.david4.common.model.TaskModel;
import com.david4.console.Code;
import com.david4.console.TaskControl;
import com.david4.console.TaskInfo;
import com.david4.console.service.ConsoleService;
import com.david4.filetrans.config.TaskConfig;
import com.david4.filetrans.dao.UserDao;
import com.david4.filetrans.model.User;
@Controller
@RequestMapping(value = "/console")
public class ConsoleController  extends BaseController{
	@Autowired
	private ConsoleService consoleService;
	@Autowired
	@Qualifier("taskConfig")
	private TaskConfig taskConfig;
	
	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;
	/**
	 * 获取运行状态
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getState.jhtml")
	public String getState(Model model){
		model.addAttribute(RESULT,TaskControl.getRunningTask());
		return PARAMETER;
	}
	
	/**
	 * 获取运行信息
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getRunInfo.jhtml",params={"lastSize","taskId"})
	public String getRunInfo(int lastSize,String taskId,Model model){
		TaskModel taskModel= taskConfig.getTaskModel(taskId);
		if(taskModel!=null){
			String group = taskModel.getGroup();
			model.addAttribute(RESULT,TaskInfo.get(lastSize,group));
		}
		return PARAMETER;
	}
	
	/**
	 * run task
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/runtask.jhtml",params={"taskId"})
	public String runTask(String taskId,Model model,HttpServletRequest request){
		String tasks = (String)(request.getSession().getAttribute("TASKS"));
		if(tasks==null){
			model.addAttribute(RESULT,"没有权限或登录已过期");
			return PARAMETER;
		}
		boolean flag = false;
		String[] taskArr = tasks.split(",");
		if(taskArr!=null && taskArr.length>0){
			for(String s:taskArr){
				if(s.equals(taskId)){
					flag = true;
					break;
				}
			}
		}
		if(!flag){
			model.addAttribute(RESULT,"没有权限");
			return PARAMETER;
		}
		
		if(!TaskControl.canRun(taskId)){
			model.addAttribute(RESULT,"该任务在执行中");
			return PARAMETER;
		}
		String group = "0";
		try{
			TaskModel task = taskConfig.getTaskModel(taskId);
			group = task.getGroup();
			consoleService.doTask(task);
			TaskInfo.log(group,"#"+taskId+"#执行成功");
			model.addAttribute(RESULT,"#"+taskId+"#执行成功");
		}catch(TaskNotExsitException e){
			TaskInfo.log(group,"#"+taskId+"#"+e.getMessage());
			model.addAttribute(RESULT,e.getMessage());
		}catch(FromFileEmptyException e){
			TaskInfo.log(group,"#"+taskId+"#"+e.getMessage());
			model.addAttribute(RESULT,e.getMessage());
		}catch (Exception e) {
			TaskInfo.log(group,"#"+taskId+"#执行失败"+e.getMessage());
			model.addAttribute(RESULT,e.getMessage());
			e.printStackTrace();
		}finally{
			TaskControl.complete(taskId);
		}
		return PARAMETER;
	}
	
	/**
	 * 对外提供的服务
	 * @return
	 */
	@RequestMapping(value = "/run.jhtml",params={"taskId","name","password"})
	public String run(String taskId,String name,String password,Model model){
		User user = userDao.getUser(name);
		//用户名判断
		if(user == null){
			model.addAttribute(RESULT, getRtnMsg(Code.CODE_NONAME, Code.MESSAGE_NONAME));
			return PARAMETER;
		}
		//密码判断
		if(password==null || !password.equals(user.getPassword())){
			model.addAttribute(RESULT, getRtnMsg(Code.CODE_PASERR, Code.MESSAGE_PASERR));
			return PARAMETER;
		}
		//权限判断
		boolean flag = false;
		String tasks = user.getTasks();
		if(tasks!=null && tasks.trim().length()>0){
			String[] taskArr = tasks.split(",");
			if(taskArr!=null && taskArr.length>0){
				for(String s:taskArr){
					if(s.equals(taskId)){
						flag = true;
						break;
					}
				}
			}
		}
		if(!flag){
			model.addAttribute(RESULT, getRtnMsg(Code.CODE_NOROLE, Code.MESSAGE_NOROLE));
			return PARAMETER;
		}
		//是否执行中判断
		if(!TaskControl.canRun(taskId)){
			model.addAttribute(RESULT,getRtnMsg(Code.CODE_RUNNING, Code.MESSAGE_RUNNING));
			return PARAMETER;
		}
		
		String group = "0";
		try{
			TaskModel task = taskConfig.getTaskModel(taskId);
			//TODO 提供两种方式 同步 异步，目前这个是同步的
			consoleService.doTask(task);
			group = task.getGroup();
			TaskInfo.log(group,"#"+taskId+"#执行成功");
			model.addAttribute(RESULT,getRtnMsg(Code.CODE_SUCCESS, Code.MESSAGE_SUCCESS));
		}catch(TaskNotExsitException e){
			TaskInfo.log(group,"#"+taskId+"#"+e.getMessage());
			model.addAttribute(RESULT,getRtnMsg(Code.CODE_NOTASK, Code.MESSAGE_NOTASK));
		}catch(FromFileEmptyException e){
			TaskInfo.log(group,"#"+taskId+"#"+e.getMessage());
			model.addAttribute(RESULT,getRtnMsg(Code.CODE_FILEMPTY, Code.MESSAGE_FILEMPTY));
		}catch (Exception e) {
			TaskInfo.log(group,"#"+taskId+"#执行失败"+e.getMessage());
			model.addAttribute(RESULT,getRtnMsg(Code.CODE_ERROR, Code.MESSAGE_ERROR));
			e.printStackTrace();
		}finally{
			TaskControl.complete(taskId);
		}
		return PARAMETER;
	}
	
//	@RequestMapping(value = "/listfile.jhtml",params={"taskId"})
//	public String listFile(String taskId,Model model){
//		try{
//			TaskModel task = XmlConfig.getTaskModel(taskId);
//			if(task instanceof FiletransTaskModel){
//				FiletransTaskModel ftm = (FiletransTaskModel)task;
//				String fromFtpId = ftm.getFromftp();
//				FTPConfig fromConfig = XmlConfig.getFTPConfig(fromFtpId);
//				if(fromConfig==null){
//					throw new Exception("get ftp config error,config is null,ftpid="+fromFtpId);
//				}
//				FTPClient fromftp = FTPUtil.getFtpClient(fromConfig);
//				if(fromftp==null){
//					throw new Exception("connect and login ftp error,"+fromConfig.toString());
//				}
//				String fromPath = ftm.getFrompath();
//				if(fromPath==null || fromPath.trim().length()==0){
//					throw new Exception("fromPath null,fromPath="+fromPath);
//				}
//				fromPath = ScriptUtil.getString(fromPath, Constants.SCRIPT_PATH);
//				if(fromPath==null || fromPath.trim().length()==0){
//					throw new Exception("fromPath null,fromPath="+fromPath);
//				}
//				String toPath = ftm.getTopath();
//				if(toPath==null || toPath.trim().length()==0){
//					throw new Exception("toPath null,toPath="+toPath);
//				}
//				String fromPathBak =  ftm.getFrompathbak();
//				List<String> pathList = FTPUtil.getFileList(fromPath, fromftp);
//				if(pathList==null || pathList.size()==0){
//					model.addAttribute(RESULT, "没有查询到文件");
//					return PARAMETER;
//				}
//				StringBuilder sb = new StringBuilder();
//				int i = 1;
//				for(String path:pathList){
//					String toPathTemp = FileUtil.getPath(path, toPath, fromPath);
//					toPathTemp = ScriptUtil.getString(toPathTemp, Constants.SCRIPT_PATH);
//					String fromPathBakTemp = null;
//					if(fromPathBak==null || fromPathBak.trim().length()==0){
//						fromPathBakTemp=null;
//					}else{
//						fromPathBakTemp = FileUtil.getPath(path, fromPathBak, fromPath);
//						fromPathBakTemp = ScriptUtil.getString(fromPathBakTemp, Constants.SCRIPT_PATH);
//					}
//					logger.info("frompath="+path);
//					logger.info("frompathbak="+fromPathBakTemp);
//					logger.info("topath="+toPathTemp);
//					sb.append(i).append("原文件  ").append(path).append(TaskInfo.FEN_GE)
//					.append(i).append("备份文件").append(fromPathBakTemp).append(TaskInfo.FEN_GE)
//					.append(i).append("目标文件").append(toPathTemp).append(TaskInfo.FEN_GE)
//					.append("=====================================================")
//					.append(TaskInfo.FEN_GE);
//					i++;
//				}
//				model.addAttribute(RESULT, sb.toString());
//				return PARAMETER;
//			}
//		}catch(Exception e){
//			model.addAttribute(RESULT, e.getMessage());
//		}
//		return PARAMETER;
//	}
}
