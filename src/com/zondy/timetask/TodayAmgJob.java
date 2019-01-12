package com.zondy.timetask;

import java.util.HashMap;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zondy.collect.TodayAmgData;

public class TodayAmgJob extends BaseJob {
	private static Logger log = LoggerFactory.getLogger(TodayAmgJob.class);
	
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String instName = context.getJobDetail().getName();
		String groupName = context.getJobDetail().getGroup();
		log("[任务调度]--"+instName+"--开始");
		log("[任务组名]--"+groupName);
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		HashMap<String, String> configMap = (HashMap<String, String>)dataMap.get("configMap");
		log("定时任务参数："+configMap);
		try {			
			TodayAmgData.collectTodayAmgData();
		} catch (Exception e) {
			log("Exception="+e.getMessage());
			log.error("当前格点数据预报定时任务执行异常",e);
			//e.printStackTrace();
		}
		log("[任务调度]--"+instName+"--结束");
		logLine();
	}

}
