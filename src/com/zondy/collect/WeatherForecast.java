package com.zondy.collect;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.DateUtils;
import com.zondy.util.StationUtil;

/**
 * 天气预报数据采集
 * @author 雷志强
 * @version 1.0
 */
public class WeatherForecast {
	
	private static Logger log = Logger.getLogger(WeatherForecast.class);
	
	public static void collectWeatherForecast(){
		XmlConfig webXml = new XmlConfig(ApplicationListener.webconfigFilePath);
		//获取城市当前预报页面地址
		String basePageUrl = webXml.getConfigValue("weekWeatherPage");
		//查询所有站点信息
		List<JSONObject> stations = StationUtil.getAllStation();
		int count = stations.size();
		JSONObject station = null;
		log.info("查询河南省所有城市：["+count+"]个");
		String pageurl = basePageUrl;
		for(int i=0;i<count;i++){
			station = stations.get(i);
			pageurl = basePageUrl.replaceAll("#cityName#", station.getString("ename"));
			readForeCastData(pageurl, station);
			try {
				//延迟调用防止网站屏蔽请求
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				log.error("InterruptedException",new Throwable(e));
			}
		}
	}
	
	public static void readForeCastData(String pageurl,JSONObject station){
		String stationId = station.getString("stationid");
		String stationName = station.getString("name");
		log.info("站点["+stationName+"]页面采集地址："+pageurl);
		Document doc = null;
		JSONObject info = new JSONObject();
		try {
			// 定义HTTP连接对象
			Connection connection = Jsoup.connect(pageurl);
			// 定义 HTTP请求的用户代理头。
			connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
			// 定义请求响应对象
			Response response = connection.execute();
			doc = response.parse();
			//String xmlStr = FileUtils.readFileToString(new File("c:/tianqi.txt"), "UTF-8");
			//doc = Jsoup.parse(xmlStr);
			log.info("站点["+stationName+"]页面内容读取成功!");
			//saveHtml(pageurl, doc.html());
			Element weatherBoxElement = doc.getElementsByClass("weatherbox").first();
			String city_name = weatherBoxElement.getElementsByClass("name").first().getElementsByTag("h2").text();
			info.put("city_name", city_name);
			String week = weatherBoxElement.getElementsByClass("week").first().text();
			info.put("week", week);
			Element weatherElement = weatherBoxElement.getElementsByClass("weather").first();
			//天气预报图标
			String iconurl = "http:"+weatherElement.getElementsByTag("img").first().attr("src");
			//当前温度
			String temp = weatherElement.getElementsByClass("now").first().getElementsByTag("b").text();
			//当前天气
			String weather = weatherElement.getElementsByTag("span").first().getElementsByTag("b").text();
			//高温和低温
			String tempinfo = weatherElement.getElementsByTag("span").first().text();
			tempinfo = tempinfo.replaceAll(weather, "");
			tempinfo = tempinfo.replaceAll("℃", "");
			tempinfo = tempinfo.replaceAll(" ", "");
			Elements blist = weatherBoxElement.getElementsByClass("shidu").first().getElementsByTag("b");
			Element kqElement =  weatherBoxElement.getElementsByClass("kongqi").first();
			//湿度
			String humi = blist.get(0).text();//湿度
			humi = humi.replaceAll("湿度：", "").replaceAll("%", "");
			//风向风力
			String fxfl = blist.get(1).text();//风向风力
			fxfl = fxfl.replace("风向：", "");
			//紫外线
			String zwx = blist.get(2).text();//紫外线
			zwx = zwx.replaceAll("紫外线：", "");
			//空气质量
			String kqzl = kqElement.getElementsByTag("h5").text();
			kqzl = kqzl.replaceAll("空气质量：", "");
			//PM
			String pm = kqElement.getElementsByTag("h6").text();
			pm = pm.replaceAll("PM: ", "");
			//日出日落
			String rcrl = kqElement.getElementsByTag("span").get(0).text();
			rcrl = rcrl.replaceAll("日出: ", "");
			rcrl = rcrl.replaceAll("日落: ", "");
			info.put("stationId", stationId);
			info.put("datetime", DateUtils.date2String("yyyy-MM-dd"));
			info.put("iconurl", iconurl);
			info.put("temp", temp);
			info.put("weather", weather);
			info.put("lotemp", tempinfo.split("[~]")[0]);
			info.put("hitemp", tempinfo.split("[~]")[1]);
			info.put("humi", humi.replaceAll("湿度：", ""));
			info.put("windf", fxfl.split("[ ]")[0]);
			info.put("winds", fxfl.split("[ ]")[1]);
			info.put("zwx", zwx);
			info.put("kqzl", kqzl);
			info.put("pm", pm);
			info.put("rc", rcrl.split("[ ]")[0]);
			info.put("rl", rcrl.split("[ ]")[1]);
			info.put("updatetime", DateUtils.date2String("HH:mm"));
			XmlConfig xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
			//先检查当天的预报是否创建了，如果没有创建则新增数据，如果有的话就更新数据
			String sql = xml.getParamConfig("checkNowWeather", info);
			List<?> list = ApplicationListener.dao.listAll(sql);
			if(list.size()==0){
				sql = xml.getParamConfig("saveNowWeather", info);
			}else{
				sql = xml.getParamConfig("updateNowWeather", info);
			}
			int ret = ApplicationListener.dao.saveObject(sql);
			if(ret == 1){
				log.info("站点["+stationName+"]实时天气预报数据采集更新成功！");
			}
		} catch (IOException e) {
			log.info("站点["+stationName+"]页面内容读取失败!");
			log.error("IOException",new Throwable(e));
		}
	}
	
	/**
	 * @Description 将抓取的网页内容保存到本地.<br>
	 * @author 雷志强  2019年1月12日 下午2:45:16
	 * @param pageurl 当前页面地址，用于获取城市英文名称
	 * @param text 页面内容
	 * @return void
	 */
	public static void saveHtml(String pageurl,String text){
		File file = new File(pageurl);
		String cityEnName = file.getName();
		System.out.println(cityEnName);
		String filepath = "D:/tianqi/"+cityEnName+".html";
		try {
			FileUtils.write(new File(filepath), text, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//collectWeatherForecast();
		JSONObject json = new JSONObject();
		readForeCastData("https://www.tianqi.com/huixian/",json);
		System.out.println("Ok");
	}
	
	public static void test(){
		String pageurl = "https://www.tianqi.com/fengqiu/";
		pageurl = "https://www.tianqi.com/huixian/";
		try {
			Document doc = Jsoup.connect(pageurl).get();
			System.out.println(doc);
			FileUtils.write(new File("c:/fengqiu.html"), doc.html());
			System.out.println("保存成功！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
