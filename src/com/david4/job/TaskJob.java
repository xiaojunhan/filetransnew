package com.david4.job;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.david4.common.SpringContainer;
import com.david4.common.exception.TaskNotExsitException;
import com.david4.common.model.TaskModel;
import com.david4.console.TaskControl;
import com.david4.console.TaskInfo;
import com.david4.console.service.ConsoleService;
 

public class TaskJob extends QuartzJobBean implements Serializable{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1868394966836042609L;
	private String taskId;

	public void dojob1(){
		Date d = new Date();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(s.format(d)+"==taskId=="+taskId);
	}
	public void dojob(){
//		String group = "0";
//		try{
//			TaskModel task = XmlConfig.getTaskModel(taskId);
//			task.getGroup();
//			if(!TaskControl.canRun(taskId)){
//				TaskInfo.log(group,"#"+taskId+"#该任务在执行中");
//				return;
//			}
//			ConsoleService consoleService = SpringContainer.getBean(ConsoleService.class);
//			consoleService.doTask(task);
//			TaskInfo.log(group,"#"+taskId+"#执行成功");
//			
//		}catch(TaskNotExsitException e){
//			TaskInfo.log(group,"#"+taskId+"#"+e.getMessage());
//		}catch (Exception e) {
//			TaskInfo.log(group,"#"+taskId+"#执行失败"+e.getMessage());
//		
//		}finally{
//			TaskControl.complete(taskId);
//		}
//		return;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		dojob();
	}

}
