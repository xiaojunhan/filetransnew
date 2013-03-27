package com.david4.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.david4.common.BaseController;
import com.david4.filetrans.config.TaskConfig;

@Controller
@RequestMapping("/test")
public class TestController extends BaseController{
	@Autowired
	private TaskConfig taskConfig;
	@RequestMapping(value = "/test.jhtml")
	public String test(Model model){
		
		System.out.println("===test===");
		model.addAttribute("test", "tttt");
		//System.out.println(Config.get("LOCAL_PATH"));
		taskConfig.init();
		System.out.println("===test11===");
		return "test/test";
	}
	
	public static void main(String[] args){
		new TestController().t();
	}
	
	public void t(){
		logger.info("tt a");
	}
}
