package com.david4.console.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.david4.common.BaseService;
import com.david4.common.exception.TaskNotExsitException;
import com.david4.common.model.TaskModel;
import com.david4.console.TaskInfo;
import com.david4.filetrans.model.FileTransTaskModel;
import com.david4.filetrans.service.FileTransService;
@Service
public class ConsoleService extends BaseService{
	@Autowired
	private FileTransService fileTransService;
	public void doTask(TaskModel taskModel) throws Exception{
		if(taskModel == null){
			throw new TaskNotExsitException("任务不存在");
		}
		TaskInfo.log(taskModel.getGroup(),"#"+taskModel.getId()+"#开始执行");
		//目前就这一种，以后有其他种类的话，可将model和实现配置在xml中，避免硬编码
		if(taskModel instanceof FileTransTaskModel){
			FileTransTaskModel model = (FileTransTaskModel)taskModel;
			fileTransService.doTask(model);
		}
	} 
}
