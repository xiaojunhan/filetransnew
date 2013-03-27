package com.david4.console.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.david4.common.BaseController;
import com.david4.common.exception.FromFileEmptyException;
import com.david4.common.exception.TaskNotExsitException;
import com.david4.common.model.TaskModel;
import com.david4.console.TaskControl;
import com.david4.console.TaskInfo;
import com.david4.console.service.ConsoleService;
import com.david4.filetrans.config.TaskConfig;
@Controller
@RequestMapping(value = "/console")
public class ConsoleController  extends BaseController{
	@Autowired
	private ConsoleService consoleService;
	@Autowired
	@Qualifier("taskConfig")
	private TaskConfig taskConfig;
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
	public String runTask(String taskId,Model model){
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
