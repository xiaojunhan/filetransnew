package com.david4.filetrans.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.david4.common.BaseController;
import com.david4.console.TaskControl;
import com.david4.console.TaskInfo;
import com.david4.filetrans.config.TaskConfig;

@Controller
public class FiletransController  extends BaseController{
//	@Autowired
//	private FiletransService filetransService;
	@Autowired
	@Qualifier("taskConfig")
	private TaskConfig taskConfig;
	@RequestMapping(value = "/index.jhtml")
	public String index(Model model){
		return "filetrans/index";
	}
	@RequestMapping(value = "/index1.jhtml")
	public String index1(Model model){
		return "filetrans/indexforjingliang";
	}
	
	/**
	 * 刷新配置文件
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/refresh.jhtml")
	public String refresh(Model model){
		if(!TaskControl.hasRunningTask()){
			taskConfig.refresh();
			TaskInfo.log("刷新配置文件成功");
			model.addAttribute(RESULT,"刷新配置文件成功");
		}else{
			TaskInfo.log("有任务在运行，禁止刷新配置文件");
			model.addAttribute(RESULT,"有任务在运行，禁止刷新配置文件");
		}
		return PARAMETER;
	}
}
