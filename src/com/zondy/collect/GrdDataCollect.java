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
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zondy.agricultural.GrdBean;
import com.zondy.agricultural.GrdUtils;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.DateUtils;
import com.zondy.util.StationUtil;

/**
 * 格点数据采集
 * @author 雷志强
 * @version 1.0
 */
public class GrdDataCollect {
	
	private static Logger log = Logger.getLogger(GrdDataCollect.class);
	
	private static String today = DateUtils.date2String("yyyyMMdd");
	//站点数据
	private static List<JSONObject> stations = StationUtil.getAllStation();
	
	private static XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
	
	public static void readGrdGroups(){
		String webconfigFilepath = ApplicationListener.rootPath+"WEB-INF/resources/xml/webconfig.xml";
		XmlConfig xml = new XmlConfig(webconfigFilepath);
		String grdGroups = xml.getConfigValue("grdGroups");
		String[] grdGroupArr = grdGroups.split("[,]");
		for(int i=0;i<grdGroupArr.length;i++){
//			if(grdGroupArr[i].equals("AgmWeather")){
//				readGrdGroupData(grdGroupArr[i],xml);
//			}
			readGrdGroupData(grdGroupArr[i],xml);
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
		String elementName = null;
		for(int i=0;i<elementArr.length;i++){
			for(int k=0;k<cropList.size();k++){
				elementName = elementArr[i]+"_"+cropList.get(k);
				readGrdData(grdRootPath,groupInfo,elementName);
			}
		}
	}
	
	public static void readGrdData(String grdRootPath,String groupInfo,String elementName){
		Date todayDate = DateUtils.dateString2Date(today, "yyyyMMdd");
		String datestr = DateUtils.date2String("yyyyMMdd",todayDate);
		String grd_name = elementName+"_"+datestr+"_0000.grd";
		String filepath = grdRootPath+"/"+groupInfo+"/"+grd_name;
		File file = new File(filepath);
		System.out.println(filepath);
		while(file.exists()){
			int ret = saveOrUpdateStationGrdData(datestr,grd_name,groupInfo,filepath);
			log.info("GRD文件["+grd_name+"]["+datestr+"]数据采集成功，共["+ret+"]条数据！");
			todayDate = DateUtils.calculateByDate(todayDate, 1);
			datestr = DateUtils.date2String("yyyyMMdd",todayDate);
			grd_name = elementName+"_"+datestr+"_0000.grd";
			filepath = grdRootPath+"/"+groupInfo+"/"+grd_name;
			file = new File(filepath);
		}
	}
	
	public static int saveOrUpdateStationGrdData(String datetime,String grd_name,String groupInfo,String filepath){
		int ret = 0;
		int count = stations.size();
		JSONObject station = null;
		GrdBean grdBean = null;
		try {
			grdBean = GrdUtils.loadGrdFile(filepath);
		} catch (IOException e) {
			log.error("IOException");
		}
		String[] tArr = grd_name.split("[_]");
		JSONObject sqlParam = new JSONObject();
		sqlParam.put("datetime", datetime);
		sqlParam.put("grd_name", grd_name);
		sqlParam.put("group_dir", groupInfo);
		sqlParam.put("crop_code", tArr[1]);
		sqlParam.put("element_name", tArr[0]);
		String sql = null;
		double value = 0.0;
		List<JSONObject> list = null;
		List<String> sqlList = new ArrayList<String>();
		for(int i=0;i<count;i++){
			station = stations.get(i);
			value = GrdUtils.getGrdValue(grdBean, station.getDoubleValue("x"), station.getDoubleValue("y"));
			sqlParam.put("stationId", station.getString("stationid"));
			sqlParam.put("updatetime", DateUtils.date2String("HH:mm:ss"));
			sqlParam.put("element_value", value);
			sql = sqlXml.getParamConfig("checkDayGrdData", sqlParam);
			list = (List<JSONObject>) ApplicationListener.dao.listAll(sql,1,1);
			if(list != null && list.size()>0){
				sql = sqlXml.getParamConfig("updateGrdData", sqlParam);
			}else{
				sql = sqlXml.getParamConfig("saveGrdData", sqlParam);
			}
			sqlList.add(sql);
		}
		ret = ApplicationListener.dao.betchSaveData(sqlList);
		return ret;
	}
	
	public static void main(String[] args) {
		readGrdGroups();
		System.out.println("OK");
	}
}
