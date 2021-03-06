package com.zondy.timetask;

import java.util.HashMap;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.zondy.collect.WeatherAlarmUtils;
import com.zondy.collect.WeatherForecast;

/**
 * 天气预报数据采集定时任务
 * @author 雷志强
 * @version 1.0
 */
public class WeatherForecastJob extends BaseJob {
	
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
			WeatherForecast.collectWeatherForecast();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log("[任务调度]--"+instName+"--结束");
		logLine();
	}
}
