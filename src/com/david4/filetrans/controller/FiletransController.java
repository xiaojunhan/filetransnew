package com.david4.filetrans.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.david4.common.BaseController;
import com.david4.common.model.TaskModel;
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
		//User user = (User)(request.getSession().getAttribute("USER_INFO"));
		return "login";
	}
	/**
	 * 登录
	 * @param name
	 * @param password
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/login.jhtml",params={"name","password"})
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
		
		List<TaskModel> list = new ArrayList<TaskModel>();
		if(tasks!=null && tasks.trim().length()>0){
			request.getSession().setAttribute("USER_INFO", user);
			String[] taskArr = tasks.split(",");
			if(taskArr!=null && taskArr.length>0){
				for(String taskId:taskArr){
					TaskModel taskModel = taskConfig.getTaskModel(taskId);
					list.add(taskModel);
				}
			}
		}
		model.addAttribute("taskModelList", list);
		return "filetrans/index";
	}
	
	@RequestMapping(value = "/logout.jhtml")
	public String logout(HttpServletRequest request){
		request.getSession().invalidate();
		return "login";
	}
//	/**
//	 * 刷新配置文件
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping(value = "/refresh.jhtml")
//	public String refresh(Model model){
//		if(!TaskControl.hasRunningTask()){
//			taskConfig.refresh();
//			TaskInfo.log("刷新配置文件成功");
//			model.addAttribute(RESULT,"刷新配置文件成功");
//		}else{
//			TaskInfo.log("有任务在运行，禁止刷新配置文件");
//			model.addAttribute(RESULT,"有任务在运行，禁止刷新配置文件");
//		}
//		return PARAMETER;
//	}
}
