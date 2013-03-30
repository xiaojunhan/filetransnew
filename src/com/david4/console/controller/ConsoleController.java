package com.david4.console.controller;

import java.util.List;

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
import com.david4.filetrans.model.FileTransTaskModel;
import com.david4.filetrans.model.FileTransTaskModel.From;
import com.david4.filetrans.model.User;
import com.david4.filetrans.service.FileTransService;
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
	
	@Autowired
	private FileTransService fileTransService;
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
	
	@RequestMapping(value = "/listfile.jhtml",params={"taskId"})
	public String listFile(String taskId,Model model){
		try{
			TaskModel taskModel = taskConfig.getTaskModel(taskId);
			if(taskModel instanceof FileTransTaskModel){
				FileTransTaskModel ftm = (FileTransTaskModel)taskModel;
				From from = ftm.getFrom();
				if(from == null){
					throw new Exception("from model null");
				}				
				List<String> pathList = fileTransService.getPathList(from);
				if(pathList==null || pathList.size()==0){
					model.addAttribute(RESULT, "没有查询到文件");
					return PARAMETER;
				}
				StringBuilder sb = new StringBuilder();
				int i = 1;
				for(String path:pathList){
//					System.out.println(path);
//					System.out.println(new String(path.getBytes("ISO-8859-1"),
//							"UTF-8"));
//					System.out.println(new String(path.getBytes("ISO-8859-1"),
//							"GBK"));
//					System.out.println(new String(path.getBytes("ISO-8859-1")));
//					System.out.println(new String(path.getBytes("GBK"),
//							"UTF-8"));
//					System.out.println(new String(new String(path.getBytes("UTF-8"), "ISO-8859-1").getBytes("UTF-8")));
//					
//					String xml = new String(path);
//					byte[] latin1 = xml.getBytes("UTF-8");
//					byte[] utf8 = new String(latin1, "ISO-8859-1").getBytes("UTF-8");
//					System.out.println(new String(utf8));
				//	byte[] utf8 = new String(latin1, "ISO-8859-1").getBytes("UTF-8");
					sb.append(i).append(":").append(path).append(TaskInfo.FEN_GE);
					i++;
				}
				model.addAttribute(RESULT, sb.toString());
				return PARAMETER;
			}
		}catch(Exception e){
			model.addAttribute(RESULT, e.getMessage());
		}
		return PARAMETER;
	}
}
