package com.david4.common;

import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.JobDetailBean;

import com.david4.common.model.TaskModel;
import com.david4.filetrans.config.TaskConfig;
import com.david4.job.TaskJob;


public class SystemInit{
	private  final Logger logger = LoggerFactory.getLogger(SystemInit.class);
	public void init(){
		logger.info("==system init==");
		initJobTask();
	}

	/**
	 * 初始化需要定时执行的任务
	 */
	public void initJobTask(){
		TaskConfig taskConfig =  SpringContainer.getBean("taskConfig",TaskConfig.class);
		Map<String,TaskModel> map = taskConfig.getAllTask();//TaskConfig.COMPLETE
		StdScheduler singleScheduler =(StdScheduler) SpringContainer.getBean("singleSchedulerFactory");
		if(singleScheduler==null){
			return;
		}
		//singleScheduler.shutdown();
		if(map!=null && map.size()>0){
			for(Entry<String,TaskModel> e:map.entrySet()){
				TaskModel t = e.getValue();
				String taskId = e.getKey();
				if(t.getCron()!=null && t.getCron().trim()!=null && !t.getCron().equalsIgnoreCase("false")){
					JobDetailBean jobDetailBean = new JobDetailBean();
					jobDetailBean.setName("Job"+taskId);
					jobDetailBean.setJobClass(TaskJob.class);
					JobDataMap jobDataMap = new JobDataMap();
					jobDataMap.put("taskId", e.getKey());
					jobDetailBean.setJobDataMap(jobDataMap);
					
					CronTriggerBean cronTriggerBean = new CronTriggerBean();
					cronTriggerBean.setName("Trigger"+taskId);
					cronTriggerBean.setJobName("Job"+taskId);
					cronTriggerBean.setJobDetail(jobDetailBean);
					try{
						cronTriggerBean.setCronExpression(t.getCron());
					} catch (ParseException e1) {
						logger.error(e1.getMessage());
						continue;
					}
					try {
						singleScheduler.scheduleJob(jobDetailBean,cronTriggerBean);
					} catch (SchedulerException e1) {
						logger.error(e1.getMessage());
					}
				}
			}
		}
	}
}
