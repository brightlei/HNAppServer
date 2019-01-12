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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.DateUtils;
import com.zondy.util.StationUtil;

/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class WeatherUtil {
	
	private static Logger log = LoggerFactory.getLogger(WeatherUtil.class);
	
	/**
	 * 获取一周天气预报.<br>
	 * @param cityName 城市名称
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @return JSONArray 天气预报
	 */
	public static JSONArray getWeekWeather(String cityName) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		JSONArray data = new JSONArray();
		String xmlFilePath = ApplicationListener.rootPath+"/WEB-INF/resources/xml/webconfig.xml";
		XmlConfig config = new XmlConfig(xmlFilePath);
		String pageUrl = config.getConfigValue("weekWeatherPage");
		pageUrl = pageUrl.replaceAll("#cityName#", cityName);
		log.info("pageUrl="+pageUrl);
		/** HtmlUnit请求web页面 */
		WebClient wc = new WebClient();
		wc.getOptions().setUseInsecureSSL(false);
		wc.getOptions().setJavaScriptEnabled(false); // 启用JS解释器，默认为true
		wc.getOptions().setCssEnabled(false); // 禁用css支持
		wc.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
		wc.getOptions().setTimeout(100000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
		wc.getOptions().setDoNotTrackEnabled(false);
		HtmlPage page = wc.getPage(pageUrl);
		/**jsoup解析文档*/
        Document doc = Jsoup.parse(page.asXml());
        Element day7Node = doc.getElementsByClass("day7").first();
        Element weekNode = day7Node.getElementsByClass("week").first();
		Element tmpNode = day7Node.getElementsByClass("zxt_shuju").first().getElementsByTag("ul").first();
		Element windNode = day7Node.getElementsByClass("txt").first();
		Element tqNode = day7Node.getElementsByClass("txt2").first();
		Elements weekList = weekNode.getElementsByTag("li");
		Elements tmpList = tmpNode.getElementsByTag("li");
		Elements windList = windNode.getElementsByTag("li");
		Elements tqList = tqNode.getElementsByTag("li");
		int count = weekList.size();
		JSONObject rcd = null;
		for(int i=0;i<count;i++){
			rcd = new JSONObject();
			rcd.put("date", weekList.get(i).getElementsByTag("b").text());
			rcd.put("week", weekList.get(i).getElementsByTag("span").text());
			rcd.put("icon", "https:"+weekList.get(i).getElementsByTag("img").attr("src"));
			rcd.put("tq", tqList.get(i).text());
			rcd.put("wind", windList.get(i).text());
			rcd.put("hitemp", tmpList.get(i).getElementsByTag("span").text());
			rcd.put("lotemp", tmpList.get(i).getElementsByTag("b").text());
			data.add(rcd);
		}
		wc.close();
		return data;
	}
	
	/**
	 * 获取15天天气预报.<br>
	 * @param cityName 城市拼音
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @return JSONArray 天气预报数据
	 */
	public static JSONArray getDay15Weather(String cityName){
		JSONArray data = new JSONArray();
		String xmlFilePath = ApplicationListener.rootPath+"/WEB-INF/resources/xml/webconfig.xml";
		XmlConfig config = new XmlConfig(xmlFilePath);
		String pageUrl = config.getConfigValue("day15WeatherPage");
		String iconReplaceString = config.getConfigValue("replaceIconUrl");
		pageUrl = pageUrl.replaceAll("#cityName#", cityName);
		log.info("pageUrl={}",pageUrl);
		/** HtmlUnit请求web页面 */
		WebClient wc = new WebClient();
		wc.getOptions().setUseInsecureSSL(false);
		wc.getOptions().setJavaScriptEnabled(false); // 启用JS解释器，默认为true
		wc.getOptions().setCssEnabled(false); // 禁用css支持
		wc.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
		wc.getOptions().setTimeout(100000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
		wc.getOptions().setDoNotTrackEnabled(false);
		HtmlPage page = null;
		try {
			page = wc.getPage(pageUrl);
			/**jsoup解析文档*/
	        Document doc = Jsoup.parse(page.asXml());
	        Element day15Node = doc.getElementsByClass("box_day").first();
	        if(day15Node != null){
	        	Elements dataList = day15Node.getElementsByTag("div");
				int count = dataList.size();
				JSONObject rcd = null;
				Element node = null;
				String[] tmpArr = null;
				String icon = null;
				for(int i=1;i<count;i++){
					rcd = new JSONObject();
					node = dataList.get(i);
					icon = node.getElementsByTag("img").attr("src");
					icon = icon.replaceAll(iconReplaceString, "");
					tmpArr = node.text().split(" ");
					rcd.put("date", tmpArr[0]);
					rcd.put("week", tmpArr[1]);
					rcd.put("icon", icon);
					rcd.put("tq", tmpArr[2]);
					rcd.put("windf", tmpArr[7]);
					rcd.put("winds", tmpArr[8]);
					rcd.put("hitemp", tmpArr[4]);
					rcd.put("lotemp", tmpArr[3].replaceAll("~", ""));
					rcd.put("kqzl", tmpArr[6]);
					data.add(rcd);
				}
				wc.close();
	        }else{
	        	log.warn("找不到[{}]对应城市的天气！",cityName);
	        }
		} catch (FailingHttpStatusCodeException e) {
			//e.printStackTrace();
			log.error("FailingHttpStatusCodeException",e);
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			log.error("MalformedURLException",e);
		} catch (IOException e) {
			//e.printStackTrace();
			log.error("IOException",e);
		}
		return data;
	}
	/**
	 * 15天天气预报数据采集.<br>
	 */
	public static void collectDay15Weather(){
		//获取所有站点信息
		List<JSONObject> stations = StationUtil.getAllStation();
		int count = stations.size();
		JSONObject station = null;
		JSONArray jsondata = null;
		for(int i=0;i<count;i++){
			station = stations.get(i);
			jsondata = getDay15Weather(station.getString("ename"));
			if(jsondata != null && jsondata.size()>0){
				saveOrUpdateDay15Weather(station.getString("stationid"),station.getString("name"),jsondata);
			}
			try {
				//延迟调用防止网站屏蔽请求
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}
	/**
	 * 保存更新15天天气预报数据.<br>
	 * @param stationId 站点编号
	 * @param stationName 站点名称
	 * @param data 站点天气预报数据
	 */
	public static void saveOrUpdateDay15Weather(String stationId,String stationName,JSONArray data){
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		JSONObject sqlParam = new JSONObject();
		sqlParam.put("datetime", DateUtils.date2String("yyyy-MM-dd"));
		sqlParam.put("stationId", stationId);
		sqlParam.put("jsondata", data.toJSONString());
		sqlParam.put("updatetime", DateUtils.date2String("HH:mm:ss"));
		String sql = sqlXml.getParamConfig("checkDay15Weather", sqlParam);
		List<JSONObject> list = (List<JSONObject>) ApplicationListener.dao.listAll(sql,1,1);
		if(list != null && list.size()>0){
			sql = sqlXml.getParamConfig("updateDay15Weather", sqlParam);
		}else{
			sql = sqlXml.getParamConfig("saveDay15Weather", sqlParam);
		}
		int ret = ApplicationListener.dao.saveObject(sql);
		if(ret == 1){
			log.info("站点["+stationName+"]15天预报数据采集更新成功！");
		}
	}
	
	public static void main(String[] args) {
		try {
			//ApplicationListener.rootPath = "D:/JavaWorkSpaces/HNAppServer/WebRoot/";
			collectDay15Weather();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
	
	public static void testGetWeekWeather() throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		String cityName = "wuhan";
		JSONArray data = getDay15Weather(cityName);
		System.out.println(data);
	}
}
