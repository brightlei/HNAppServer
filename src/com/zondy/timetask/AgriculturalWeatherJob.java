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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.agricultural.GrdBean;
import com.zondy.agricultural.GrdUtils;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.DateUtils;
import com.zondy.util.StationUtil;
/**
 * 农用天气预报采集定时任务
 * @author 雷志强
 * @version 1.0
 */
public class AgriculturalWeatherJob extends BaseJob {
	
	private static Logger log = LoggerFactory.getLogger(AgriculturalWeatherJob.class);
	
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
			createCacheData();
		} catch (Exception e) {
			log("Exception="+e.getMessage());
			log.error("农用天气预报定时任务执行异常",e);
		}
		log("[任务调度]--"+instName+"--结束");
		logLine();
	}
	/**
	 * 生成缓存文件.<br>
	 * @return void
	 */
	public void createCacheData(){
		String xmlfilepath = ApplicationListener.rootPath+"/WEB-INF/resources/xml/webconfig.xml";
		XmlConfig xml = new XmlConfig(xmlfilepath);
		String configInfo = xml.getConfigValue("nytqybConfig");
		log.info("读取农用天气配置信息：{}",configInfo);
		int dateCount = Integer.parseInt(xml.getConfigValue("nytqybDateCount"));
		String nytqybFolder = xml.getConfigValue("nytqybFolder");
		log.info("读取农用天气文件目录：{}",nytqybFolder);
		String[] infoArr = configInfo.split("[|]");
		List<JSONObject> list = new ArrayList<JSONObject>();
		//Date todayDate = DateUtils.dateString2Date("20180921", "yyyyMMdd");//new Date();
		Date todayDate = new Date();
		String today = DateUtils.date2String("yyyyMMdd",todayDate);
		String date = today;
		GrdBean grdData = null;
		String type = null;
		String text = null;
		JSONObject data = null;
		JSONArray dateArray = null;
		JSONArray dataArray = null;
		//按各个预报产品读取3天的天气预报数据
		for(int i=0;i<infoArr.length;i++){
			data = new JSONObject();
			date = today;
			//农用天气预报产品简写
			type = infoArr[i].split("[,]")[0];
			//农用天气预报产品类别
			text = infoArr[i].split("[,]")[1];
			//日期数组
			dateArray = new JSONArray();
			//日期对应的格点数据
			dataArray = new JSONArray();
			for(int k=0;k<dateCount;k++){
				dateArray.add(date);
				grdData = readGrdData(type, text, date, nytqybFolder);
				if(grdData!=null){
					dataArray.add(grdData);
				}
				date = DateUtils.date2String("yyyyMMdd", DateUtils.calculateByDate(todayDate, k+1));
			}
			data.put("type", type);
			data.put("text", text);
			data.put("datelist", dateArray);
			data.put("datalist", dataArray);
			list.add(data);
		}
		List<HashMap<String, String>> stationList = StationUtil.readStations();
		int stationCount = stationList.size();
		log.info("站点个数:{}",stationCount);
		String stationNo = null;
		String stationName = null;
		double x = 0.0;
		double y = 0.0;
		HashMap<String, String> station = null;
		JSONArray stationData = new JSONArray();
		JSONObject stationInfo = null;
		JSONArray stationDateData = new JSONArray();
		String dataFilePathTemplate = ApplicationListener.rootPath+"/appdata/nytqyb/#stationNo#.json";
		String dataFilePath = "";
		File dateFile = null;
		double value = 0.0; 
 		for(int i=0;i<stationCount;i++){
 			stationData = new JSONArray();
 			station = stationList.get(i);
			stationNo = station.get("sno").toString();
			stationName = station.get("sname").toString();
			x = Double.parseDouble(station.get("longitude"));
			y = Double.parseDouble(station.get("latitude"));
			for (JSONObject ybdata : list) {
				stationInfo = new JSONObject();
				dateArray = ybdata.getJSONArray("datelist");
				dataArray = ybdata.getJSONArray("datalist");
				stationInfo.put("type", ybdata.getString("type"));
				stationInfo.put("text", ybdata.getString("text"));
				if(dataArray.size()>0){
					stationDateData = new JSONArray();
					for(int k=0;k<dataArray.size();k++){
						grdData = (GrdBean)dataArray.get(k);
						value = GrdUtils.getGrdValue(grdData, x, y);
						stationDateData.add(dateArray.getString(k)+","+value);
					}
					stationInfo.put("data", stationDateData);
					stationData.add(stationInfo);
				}
			}
			dataFilePath = dataFilePathTemplate.replaceAll("#stationNo#", stationNo);
			dateFile = new File(dataFilePath);
			try {
				FileUtils.forceMkdir(dateFile.getParentFile());
				FileUtils.write(dateFile, stationData.toJSONString(), "UTF-8");
			} catch (IOException e) {
				//e.printStackTrace();
			}
			log.info("[{}]站点[{}-{}]农用天气采集成功！",new Object[]{i+1,stationNo,stationName});
			//createStationData(stationNo,stationName,infoArr);
		}
	}
	
	public GrdBean readGrdData(String type,String text,String date,String nytqybFolder){
		String filepath = nytqybFolder+"/"+type+"_000000_"+date+"_0000.grd";
		File file = new File(filepath);
		GrdBean bean = null;
		if(file.exists()){
			try {
				bean = GrdUtils.loadGrdFile(filepath);
			} catch (IOException e) {
				log("读取["+text+"]预报文件异常！"+e.getMessage());
				//e.printStackTrace();
			}
		}else{
			//log(text+"预报文件不存在！"+filepath);
		}
		return bean;
	}
	
	public static void main(String[] args) {
		AgriculturalWeatherJob job = new AgriculturalWeatherJob();
		job.createCacheData();
		System.err.println("OK");
	}
}
