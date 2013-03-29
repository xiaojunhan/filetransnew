package com.david4.filetrans.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.david4.common.BaseController;
import com.david4.console.TaskControl;
import com.david4.console.TaskInfo;
import com.david4.filetrans.config.TaskConfig;
import com.david4.filetrans.dao.UserDao;
import com.david4.filetrans.model.User;

@Controller
public class FiletransController  extends BaseController{

	@Autowired
	@Qualifier("taskConfig")
	private TaskConfig taskConfig;
	
	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;
	
	@RequestMapping(value = "/index.jhtml")
	public String index(Model model){
		return "filetrans/index";
	}
	@RequestMapping(value = "/index1.jhtml")
	public String index1(Model model){
		return "filetrans/indexforjingliang";
	}
	@RequestMapping(value = "/login.jhtml")
	public String login(String name,String password,Model model,HttpServletRequest request){
		User user = userDao.getUser(name);
		if(user == null){
			model.addAttribute("message", "用户不存在");
			return "login";
		}
		if(password==null || !password.equals(user.getPassword())){
			model.addAttribute("message", "密码错误");
			return "login";
		}
		String tasks = user.getTasks();
		if(tasks!=null && tasks.trim().length()>0){
			request.getSession().setAttribute("TASKS_"+name, tasks);
			String[] taskArr = tasks.split(",");
		}
		return "filetrans/index";
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
