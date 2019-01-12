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
package com.zondy.collect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.agricultural.GrdBean;
import com.zondy.agricultural.GrdUtils;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.DateUtils;

/**
 * 当日各种要素数据
 * @author 雷志强
 * @version 1.0
 */
public class TodayAmgData {
	
	private static Logger log = Logger.getLogger(TodayAmgData.class);
	
	private static String today = DateUtils.date2String("yyyyMMdd");
	
	private static JSONArray stationArray = new JSONArray();
	
	private static JSONObject stationData = new JSONObject();
	
	public static void collectTodayAmgData(){
		stationArray = readStations();
		int count = stationArray.size();
		log.info("共["+count+"]个站点");
		JSONObject station = null;
		for(int i=0;i<count;i++){
			station = stationArray.getJSONObject(i);
			stationData.put(station.getString("sno"), new JSONObject());
		}
		readGrdGroups();
	}
	/**
	 * 读取气象站点数据.<br>
	 * @return JSONArray
	 */
	public static JSONArray readStations(){
		JSONArray stationArray = new JSONArray();
		String stationFilePath = ApplicationListener.rootPath+"appdata/stations.json";
		File file = new File(stationFilePath);
		if(file.exists()){
			try {
				String jsonstr = FileUtils.readFileToString(file, "UTF-8");
				stationArray = JSON.parseArray(jsonstr); 
			} catch (IOException e) {
				log.error("IOException", new Throwable(e));
				//e.printStackTrace();
			}
		}else{
			log.warn("气象站点文件不存在！"+stationFilePath);
		}
		return stationArray;
	}
	
	public static void readGrdGroups(){
		String webconfigFilepath = ApplicationListener.rootPath+"WEB-INF/resources/xml/webconfig.xml";
		XmlConfig xml = new XmlConfig(webconfigFilepath);
		String grdGroups = xml.getConfigValue("grdGroups");
		String[] grdGroupArr = grdGroups.split("[,]");
		for(int i=0;i<grdGroupArr.length;i++){
			readGrdGroupData(grdGroupArr[i],xml);
		}
		String todayJsonFilePath = ApplicationListener.rootPath+"/appdata/today.json";
		try {
			String dataFieldConfig = xml.getConfigValue("dataFieldConfig");
			stationData.put("fieldinfo", dataFieldConfig);
			FileUtils.writeStringToFile(new File(todayJsonFilePath), stationData.toJSONString(), "UTF-8");
		} catch (IOException e) {
			log.error("IOException",new Throwable(e));
			//e.printStackTrace();
		}
	}
	
	public static void readGrdGroupData(String groupInfo,XmlConfig xml){
		String grdElements = xml.getConfigValue("group-"+groupInfo);
		String[] elementArr = grdElements.split("[,]");
		String grdRootPath = xml.getConfigValue("grdRootPath");
		String cropCodes = xml.getConfigValue("cropCode");
		String filepath = "";
		List<String> cropList = new ArrayList<String>();
		if(groupInfo.equals("AgmForecast") || groupInfo.equals("AgmDisease")){
			String[] cropArr = cropCodes.split("[,]");
			cropList = Arrays.asList(cropArr);
		}else if(groupInfo.equals("AgmWeather")){
			cropList.add("000000");
		}else{
			cropList.add("00000");
		}
		File file =  null;
		String elementName = null;
		for(int i=0;i<elementArr.length;i++){
			for(int k=0;k<cropList.size();k++){
				elementName = elementArr[i]+"_"+cropList.get(k);
				filepath = grdRootPath+"/"+groupInfo+"/"+elementName+"_"+today+"_0000.grd";
				file = new File(filepath);
				if(file.exists()){
					readGrdData(groupInfo,elementName,filepath);
				}else{
					System.out.println("格点文件不存在！"+file);
				}
			}
		}
	}
	
	public static void readGrdData(String groupInfo,String elementName,String filepath){
		log.info(groupInfo+"|"+elementName+"|"+filepath);
		try {
			GrdBean grdBean = GrdUtils.loadGrdFile(filepath);
			JSONObject station = null;
			String sno = null;
			double x = 0.0;
			double y = 0.0;
			double v = 0.0;
			JSONObject stationGrdData = null;
			for(int i=0;i<stationArray.size();i++){
				station = stationArray.getJSONObject(i);
				sno = station.getString("sno");
				x = station.getDoubleValue("longitude");
				y = station.getDoubleValue("latitude");
				v = GrdUtils.getGrdValue(grdBean, x, y);
				stationGrdData = stationData.getJSONObject(sno);
				if(!stationGrdData.containsKey(groupInfo)){
					stationGrdData.put(groupInfo, new JSONObject());
				}
				stationGrdData.getJSONObject(groupInfo).put(elementName, v);
			}
			System.out.println(stationGrdData);
		} catch (IOException e) {
			log.error("IOException",new Throwable(e));
			//e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		collectTodayAmgData();
		System.out.println("OK");
	}

}
