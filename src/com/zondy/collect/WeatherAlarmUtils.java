package com.zondy.collect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.StationUtil;

/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class WeatherAlarmUtils {
	
	private static Logger log = Logger.getLogger(WeatherAlarmUtils.class);
	
	public static String collectHeNanAlarm(){
		String msg = "";
		//最新采集的页面地址
		String url = "";
		XmlConfig xml = new XmlConfig(ApplicationListener.webconfigFilePath);
		String pageurl = xml.getConfigValue("alarmnews");
		String http = xml.getConfigValue("alarmiconHttp");
		XmlConfig sqlxml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		//查询数据库中已采集的最新预警记录信息
		List<JSONObject> newlist = (List<JSONObject>)ApplicationListener.dao.listAll(sqlxml.getConfigValue("getNewAlarmRecord"),1,1);
		if(newlist!=null && newlist.size()==1){
			url = newlist.get(0).getString("pageurl");
		}
		log.info("最新采集的页面地址："+url);
		//读取所有的站点数据
		List<JSONObject> stations = StationUtil.getAllStation();
		//采集的页面地址集合
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		//当页的所有页面集合
		List<JSONObject> pageurlList = null;
		//读取最近的5页数据，直到已采集的最新的地址那条数据
		for(int i=1;i<5;i++){
			pageurlList = rearPageAlarm(pageurl+"/"+i,dataList);
			dataList.addAll(pageurlList);
			if(checkUrlExist(pageurlList, url)){
				log.info("已读取完所有的未采集的页面！");
				break;
			}
			try {
				//延迟2秒执行，防止页面屏蔽调用
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				log.error("InterruptedException", new Throwable(e));
			}
		}
		String dataurl = null;
		String title = null;
		JSONObject info = null;
		int dataCount = dataList.size();
		log.info("共采集到["+dataCount+"]条数据");
		String sql = null;
		List<JSONObject> newDataList = new ArrayList<JSONObject>();
		//处理未采集的数据
		for(int i=0;i<dataCount;i++){
			info = dataList.get(i);
			dataurl = info.getString("dataurl");
			if(!dataurl.equals(url)){
				newDataList.add(info);
			}else{
				log.info("该页面已采集！"+dataurl);
				break;
			}
		}
		dataCount = newDataList.size();
		log.info("共["+dataCount+"]条数据未采集！");
		JSONObject station = null;
		int okCount = 0;
		for (int i = dataCount-1; i >= 0; i--) {
			info = dataList.get(i);
			dataurl = info.getString("dataurl");
			title = info.getString("title");
			station = isHeNanAlarm(stations, title);
			sql = sqlxml.getParamConfig("saveAlarmRecord", info);
			ApplicationListener.dao.saveObject(sql);
			//如果为河南省预警信息则入库
			if(station != null){
				try {
					info.put("stationId", station.getString("stationid"));
					info.put("station_no", station.getString("station_no"));
					int ret = readAlarmInfo(dataurl, info, http, sqlxml);
					if(ret==1){
						okCount += ret;
						log.info("["+station.getString("name")+"]预警数据采集成功！"+title);
					}
				} catch (IOException e) {
					log.error("IOException", new Throwable(e));
				}
			}
		}
		msg = "共采集["+dataCount+"]条预警数据，其中["+okCount+"]条为河南省数据";
		return msg;
	}
	/**
	 * 采集预警数据.<br>
	 * @return void
	 */
	@SuppressWarnings("unchecked")
	public static String collectAlarm(){
		String msg = "";
		//最新采集的页面地址
		String url = "";
		XmlConfig xml = new XmlConfig(ApplicationListener.webconfigFilePath);
		String pageurl = xml.getConfigValue("alarmnews");
		String http = xml.getConfigValue("alarmiconHttp");
		XmlConfig sqlxml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		//查询数据库中已采集的最新预警记录信息
		List<JSONObject> newlist = (List<JSONObject>)ApplicationListener.dao.listAll(sqlxml.getConfigValue("getNewAlarmRecord"),1,1);
		if(newlist!=null && newlist.size()==1){
			url = newlist.get(0).getString("pageurl");
		}
		log.info("最新采集的页面地址："+url);
		//读取所有的站点数据
		List<JSONObject> stations = StationUtil.getAllStation();
		//采集的页面地址集合
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		//当页的所有页面集合
		List<JSONObject> pageurlList = null;
		//读取最近的5页数据，直到已采集的最新的地址那条数据
		for(int i=1;i<5;i++){
			pageurlList = rearPageAlarm(pageurl+"/"+i,dataList);
			dataList.addAll(pageurlList);
			if(checkUrlExist(pageurlList, url)){
				log.info("已读取完所有的未采集的页面！");
				break;
			}
			try {
				//延迟2秒执行，防止页面屏蔽调用
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				log.error("InterruptedException", new Throwable(e));
			}
		}
		String dataurl = null;
		String title = null;
		JSONObject info = null;
		int dataCount = dataList.size();
		log.info("共采集到["+dataCount+"]条数据");
		String sql = null;
		List<JSONObject> newDataList = new ArrayList<JSONObject>();
		//处理未采集的数据
		for(int i=0;i<dataCount;i++){
			info = dataList.get(i);
			dataurl = info.getString("dataurl");
			if(!dataurl.equals(url)){
				newDataList.add(info);
			}else{
				log.info("该页面已采集！"+dataurl);
				break;
			}
		}
		dataCount = newDataList.size();
		log.info("共["+dataCount+"]条数据未采集！");
		JSONObject station = null;
		int okCount = 0;
		for (int i = dataCount-1; i >= 0; i--) {
			info = dataList.get(i);
			dataurl = info.getString("dataurl");
			title = info.getString("title");
			station = isHeNanAlarm(stations, title);
			sql = sqlxml.getParamConfig("saveAlarmRecord", info);
			ApplicationListener.dao.saveObject(sql);
			//如果为河南省预警信息则入库
			if(station != null){
				try {
					info.put("stationId", station.getString("stationid"));
					info.put("station_no", station.getString("station_no"));
					int ret = readAlarmInfo(dataurl, info, http, sqlxml);
					if(ret==1){
						okCount += ret;
						log.info("["+station.getString("name")+"]预警数据采集成功！"+title);
					}
					try {
						//延迟2秒执行，防止页面屏蔽调用
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						log.error("InterruptedException", new Throwable(e));
					}
				} catch (IOException e) {
					log.error("IOException", new Throwable(e));
				}
			}
		}
		msg = "共采集["+dataCount+"]条预警数据，其中["+okCount+"]条为河南省数据";
		return msg;
	}
	/**
	 * 判断该条预警数据是否为河南省的预警数据.<br>
	 * @param stations 河南省所有城市站点数据
	 * @param title 预警信息标题，从标题中判断是否为河南省预警数据
	 * @return JSONObject
	 */
	public static JSONObject isHeNanAlarm(List<JSONObject> stations,String title){
		JSONObject station = null;
		if(stations != null){
			int count = stations.size();
			JSONObject record = null;
			String name = null;
			for(int i=0;i<count;i++){
				record = stations.get(i);
				name = record.getString("name");
				if(title.contains("河南") && title.contains(name)){
					station = record;
					break;
				}
			}
		}
		return station;
	}
	
	/**
	 * 判断采集的页面地址中是否有已经采集的地址List<JSONObject> 
	 * @param list 页面对象数组
	 * @param url 已经采集的最新记录页面地址
	 * @return boolean
	 */
	public static boolean checkUrlExist(List<JSONObject> list,String url){
		boolean isExist = false;
		if(list != null){
			int count = list.size();
			JSONObject record = null;
			for(int i=0;i<count;i++){
				record = list.get(i);
				if(record.getString("dataurl").equals(url)){
					isExist = true;
					break;
				}
			}
		}
		return isExist;
	}
	/**
	 * 读取预警数据列表分页数据.<br>
	 * @param pageurl 预警分页地址
	 * @param dataList 已添加的预警公页数据
	 * @return List<JSONObject> 返回当前页里面所有的预警数据
	 */
	public static List<JSONObject> rearPageAlarm(String pageurl,List<JSONObject> dataList){
		List<JSONObject> pageList = new ArrayList<JSONObject>();
		/**jsoup解析文档*/
		Document doc = null;
		try {
			// 定义HTTP连接对象
			Connection connection = Jsoup.connect(pageurl);
			// 定义 HTTP请求的用户代理头。
			connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
			// 定义请求响应对象
			Response response = connection.execute();
			doc = response.parse();
			//doc = Jsoup.connect(pageurl).get();
			if(doc != null){
				Element newslist_yj = doc.getElementsByClass("newslist_yj").first();
		        Elements datalist = newslist_yj.getElementsByTag("li");
		        int count = datalist.size();
		        Element element = null;
		        String dataurl = null;
		        String alarmtime = null;
		        Element linkElement = null;
		        String title = null;
		        JSONObject info = null;
		        for(int i=0;i<count;i++){
		        	info = new JSONObject();
		        	element = datalist.get(i);
		        	linkElement = element.getElementsByTag("a").first();
		        	dataurl = linkElement.attr("href");
		        	title = linkElement.text();
		        	title = title.substring(title.indexOf("]")+1);
		        	alarmtime = element.getElementsByTag("i").text();
		        	if(title.contains("发布")){
		        		info.put("alarmstate", "发布");
		        	}else{
		        		info.put("alarmstate", "解除");
		        	}
		        	info.put("dataurl", dataurl);
		        	info.put("title", title);
		        	info.put("alarmtime", alarmtime);
		        	if(!checkUrlExist(dataList, dataurl)){
		        		pageList.add(info);
		        	}
		        }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return pageList;
	}
	
	public static List<JSONObject> rearPageAlarmList(String pageurl){
		List<JSONObject> pageList = new ArrayList<JSONObject>();
		/**jsoup解析文档*/
		Document doc = null;
		try {
			String content =  FileUtils.readFileToString(new File("c:/alarm.txt"));
			doc = Jsoup.parse(content);
			//doc = Jsoup.connect(pageurl).get();
			System.out.println(doc);
			if(doc != null){
				Element newslist_yj = doc.getElementsByClass("newslist_yj").first();
		        Elements datalist = newslist_yj.getElementsByTag("li");
		        int count = datalist.size();
		        System.out.println(count);
		        Element element = null;
		        String dataurl = null;
		        String alarmtime = null;
		        Element linkElement = null;
		        String title = null;
		        JSONObject info = null;
		        for(int i=0;i<count;i++){
		        	info = new JSONObject();
		        	element = datalist.get(i);
		        	linkElement = element.getElementsByTag("a").first();
		        	dataurl = linkElement.attr("href");
		        	title = linkElement.text();
		        	title = title.substring(title.indexOf("]")+1);
		        	alarmtime = element.getElementsByTag("i").text();
		        	if(title.contains("发布")){
		        		info.put("alarmstate", "发布");
		        	}else{
		        		info.put("alarmstate", "解除");
		        	}
		        	info.put("dataurl", dataurl);
		        	info.put("title", title);
		        	info.put("alarmtime", alarmtime);
		        	pageList.add(info);
		        }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return pageList;
	}
	
	public static String getDataTime(String dataurl){
		String[] tArr = dataurl.split("[/]");
		String filename = tArr[tArr.length-1];
		filename =filename.replace(".html", "");
		return filename;
	}
	
	/**
	 * 读取预警信息页面内容并入库.<br>
	 * @param dataurl 预警信息页面地址
	 * @param info 预警信息入库参数
	 * @param http 预警图标地址前缀
	 * @param xml SQL语句配置XML文档对象
	 * @throws IOException
	 * @return int
	 */
	public static int readAlarmInfo(String dataurl,JSONObject info,String http,XmlConfig xml) throws IOException{
		//String xmlStr = FileUtils.readFileToString(new File("c:/234.txt"), "UTF-8");
		//Document doc = Jsoup.parse(xmlStr);
		// 定义HTTP连接对象
		Connection connection = Jsoup.connect(dataurl);
		// 定义 HTTP请求的用户代理头。
		connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
		// 定义请求响应对象
		Response response = connection.execute();
		Document doc = response.parse();
		//Document doc = Jsoup.connect(dataurl).get();
		String yj_icon = http+doc.getElementsByClass("yj_icon").first().getElementsByTag("img").attr("src");
		String text = doc.getElementById("newscont").text();
		String[] tArr = text.split("[　　]");
		String content = tArr[2];
		String yfzn = tArr[4].replace("防御指南：", "");
		info.put("alarmicon", yj_icon);
		info.put("content", content);
		info.put("defense_guide", yfzn);
		String sql = xml.getParamConfig("saveAlarmInfo", info);
		int ret = ApplicationListener.dao.saveObject(sql);
		return ret;
	}
	
	public static void main(String[] args) {
		try {
			String result = collectHeNanAlarm();
			System.out.println(result);
			//collectHeNanAlarm();
			//List<JSONObject> list = rearPageAlarmList("https://www.tianqi.com/alarmnews_18/");
			//System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
