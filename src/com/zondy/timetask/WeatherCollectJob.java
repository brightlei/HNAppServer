/**   
 * 特别声明：本技术材料受《中华人民共和国着作权法》、《计算机软件保护条例》
 * 等法律、法规、行政规章以及有关国际条约的保护，武汉中地数码科技有限公
 * 司享有知识产权、保留一切权利并视其为技术秘密。未经本公司书面许可，任何人
 * 不得擅自（包括但不限于：以非法的方式复制、传播、展示、镜像、上载、下载）使
 * 用，不得向第三方泄露、透露、披露。否则，本公司将依法追究侵权者的法律责任。
 * 特此声明！
 * 
   Copyright (c) 2013,武汉中地数码科技有限公司
 */
package com.zondy.timetask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.collect.WeatherUtil;
import com.zondy.listener.ApplicationListener;
/**
 * 15天天气预报采集定时任务
 * @author 雷志强
 * @version 1.0
 */
public class WeatherCollectJob extends BaseJob {
	
	private static Logger log = LoggerFactory.getLogger(WeatherCollectJob.class);
	
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
			collectWeather();
		} catch (Exception e) {
			log("Exception="+e.getMessage());
			log.error("天气预报定时任务执行异常",e);
			//e.printStackTrace();
		}
		log("[任务调度]--"+instName+"--结束");
		logLine();
	}
	
	public void collectWeather(){
		int stationCount = ApplicationListener.stationList.size();
		JSONObject station = null;
		String stationNo = null;
		String stationName = null;
		String stationPingyin = null;
		JSONArray data = null;
		String rootPath = ApplicationListener.rootPath;
		String filepath = "";
		File file = null;
		for(int i=0;i<stationCount;i++){
			station = ApplicationListener.stationList.get(i);
			stationNo = station.getString("station_no");
			stationName = station.getString("name").toString();
			stationPingyin = station.getString("ename");
			filepath = rootPath+"/appdata/weather/"+stationNo+".json";
			file = new File(filepath);
			log.info("file="+file);
			try {
				FileUtils.forceMkdir(file.getParentFile());
				data = WeatherUtil.getDay15Weather(stationPingyin);
				if(data!=null && data.size()>0){
					FileUtils.writeStringToFile(file, data.toJSONString(), "UTF-8");
					log("采集成功-站点["+stationName+"]天气预报！");
					log.info("站点[{}]天气预报采集成功！",stationName);
				}else{
					log("采集失败-站点["+stationName+"]天气预报！");
					log.warn("站点[{}]天气预报采集失败！",stationName);
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					log("Exception="+e.getMessage());
				}
			} catch (Exception e) {
				log("Exception="+e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) {
		WeatherCollectJob job = new WeatherCollectJob();
		job.collectWeather();
		//log.info("这是一个测试{}","你好");
	}
}
