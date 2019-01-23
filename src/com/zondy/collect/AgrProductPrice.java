package com.zondy.collect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Request;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.DateUtils;
import com.zondy.util.PinyinUtil;

/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class AgrProductPrice {
	/**
	 * 日志对象
	 */
	private static Logger log = Logger.getLogger(AgrProductPrice.class);

	private static String INDEX_URL = "http://zzny.zhengzhou.gov.cn/";
	
	private static String USER_AGENT = "User-Agent";
	
	private static String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36";
	
	/**
	 * @Description 采集农产品价格.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午9:04:11
	 */
	public void collectProductPrice(){
		// webconfig.xml配置文件路径
		String xmlFilePath = ApplicationListener.rootPath+"/WEB-INF/resources/xml/webconfig.xml";
		XmlConfig config = new XmlConfig(xmlFilePath);
		// 读取农产品价格首页地址，必须要从首页进入子页面才能获取到正确的页面内容，否则会直接跳转到首页
		String pageUrl = config.getConfigValue("ncpjgPageUrl");
		log.info("pageUrl="+pageUrl);
		// 定义HTTP连接对象
		Connection connection = Jsoup.connect(INDEX_URL);
		// 配置模拟浏览器
		connection.header(USER_AGENT, USER_AGENT_VALUE);   
        Response rs = null;
		try {
			rs = connection.execute();
			/*
	         * 第二次请求，以post方式提交表单数据以及cookie信息
	         */
	        Connection con2 = Jsoup.connect("http://zzny.zhengzhou.gov.cn/ncpjg/index.jhtml");
	        con2.header(USER_AGENT, USER_AGENT_VALUE);
	        // 设置cookie和post上面的map数据
	        Response targetResponse = con2.ignoreContentType(true)
	        		.followRedirects(true)
	        		.cookies(rs.cookies()).execute();
	        int statusCode = targetResponse.statusCode();
	        log.info("请求响应状态码："+statusCode);
	        if(statusCode == 200){
	        	// 读取页面源代码信息
		        String html = targetResponse.body();
		        // 拼接页面本地文件路径
		        //String htmlPath = ApplicationListener.rootPath+"/appdata/price-pagelist.html";
		        // 创建页面本地文件
		        //FileUtils.writeStringToFile(new File(htmlPath), html);
		        // 登陆成功后的cookie信息，可以保存到本地，以后登陆时，只需一次登陆即可
		        Map<String, String> map = targetResponse.cookies();
		        for (String s : map.keySet()) {
		            System.out.println(s + " : " + map.get(s));
		        }
		        readPriceURLList(html);
	        }else{
	        	log.info("请求响应出错，状态码："+statusCode);
	        }
		} catch (IOException e) {
			log.error("写入本地文件出现异常！");
		}  
	}
	
	/**
	 * @Description 读取农产品价格页面中每天的农产品价格.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午9:05:10
	 * @param html 农产品价格页面html内容
	 * @throws IOException
	 */
	public void readPriceURLList(String html) throws IOException{
		Document doc = Jsoup.parse(html);
		Element ulElement = doc.getElementsByClass("news-list").first();
		Elements lis = ulElement.getElementsByTag("li");
		int count = lis.size();
		log.info("共读取到["+count+"]条数据！");
		Element li = null;
		String datatime = null;
		String dataurl = null;
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		JSONObject sqlParam = new JSONObject();
		JSONArray dataArray = null;
		String sql = null;
		int dataCount = 0;
		count = 8;
		for(int i = count - 1; i >= 0; i--){
			li = lis.get(i);
			datatime = li.getElementsByTag("span").text();
			dataurl = li.getElementsByTag("a").attr("href");
			sqlParam.put("datatime", datatime);
			sqlParam.put("dataurl", dataurl);
			log.info("待采集信息："+datatime+"|"+dataurl);
			sql = sqlXml.getParamConfig("checkProductPriceExist", sqlParam);
			// 先判断数据库中是否存在该数据
			dataCount = ApplicationListener.dao.countAll(sql);
			// 如果不存在则插入数据
			if(dataCount == 0){
				dataArray = getProductPriceData(datatime, dataurl);
				sqlParam.put("content", dataArray.toJSONString());
				sql = sqlXml.getParamConfig("saveProductPrice", sqlParam);
				int ret = ApplicationListener.dao.saveObject(sql);
				if(ret == 1){
					log.info("["+datatime+"]农产品价格数据采集入库成功!");
				}else{
					log.info("["+datatime+"]农产品价格数据采集入库失败!");
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					log.error("线程休眠出现异常！");
				}
			}else{
				log.info("["+datatime+"]农产品价格数据已采集入库!");
			}
		}
	}
	
	/**
	 * 读取本地缓存文件.<br>
	 * @throws IOException
	 * @return String 缓存的农产品价格数据
	 */
	public String readCacheData() throws IOException{
		String jsonstr = "[]";
		String jsonfilePath = ApplicationListener.rootPath+"/appdata/price-data.json";
		File file = new File(jsonfilePath);
		if(file.exists()){
			jsonstr = FileUtils.readFileToString(file, "UTF-8");
		}
		return jsonstr;
	}
	
	public List<?> loadPriceData(){
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getConfigValue("getProductPrice");
		List<?> datalist = ApplicationListener.dao.listAll(sql);
		return datalist;
	}
	/**
	 * 保存更新农产品价格数据.<br>
	 * @param data 农产品价格数据
	 * @throws IOException
	 */
	public void saveCacheData(JSONArray data) throws IOException{
		String jsonfilePath = ApplicationListener.rootPath+"/appdata/price-data.json";
		File file = new File(jsonfilePath);
		FileUtils.writeStringToFile(file, data.toJSONString(), "UTF-8");
		log.info("保存农产品价格缓存数据成功！");
	}
	
	/**
	 * @Description 根据每天的农产品价格页面读取页面中的所有农产品价格.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午9:06:58
	 * @param date 发布日期
	 * @param url 发布日期的农产品价格页面地址
	 * @return JSONArray 农产品价格数据
	 * @throws IOException
	 */
	public JSONArray getProductPriceData(String date,String url) throws IOException{
//		Document doc = Jsoup.parse(FileUtils.readFileToString(new File("c:/price-data.html"), "UTF-8"));
		Connection connection = Jsoup.connect(url);
		connection.header(USER_AGENT, USER_AGENT_VALUE);
		Document doc = connection.get();
		Elements listDiv = doc.getElementsByAttributeValue("class", "news_content_content");
		Element el = listDiv.first();
		Elements bodys = el.getElementsByTag("tbody");
		int count = bodys.size();
		log.info("count="+count);
		Elements trs = null;
		Elements tds = null;
		JSONObject jsonObject = null;
		String headerString = null;
		JSONArray jsonArray = new JSONArray();
		List<String> fieldList = new ArrayList<String>();
		List<String> fieldNameList = new ArrayList<String>();
		String productName = null;
		XmlConfig webXml = new XmlConfig(ApplicationListener.webconfigFilePath);
		// 农产品名称配置信息
		List<String> productConfigList = new ArrayList<String>();
		for(int i=0;i<count;i++){
			trs = bodys.get(i).getElementsByTag("tr");
			for(int k=0;k<trs.size();k++){
				tds = trs.get(k).getElementsByTag("td");
				if(i==0 && k==0){
					headerString = tds.text();
					fieldNameList = Arrays.asList(headerString.split("[ ]"));
					fieldList = getDataFieldList(headerString);
					updataMarketConfig(fieldList,fieldNameList);
				}
				if(tds.text().contains("信息员") || tds.text().contains("市场名称")){
					continue;
				}
				productName = tds.get(0).text();
				productConfigList.add(PinyinUtil.getPingYin(productName)+","+productName);
				jsonObject = readRowData(tds,fieldList);
				jsonArray.add(jsonObject);
			}
		}
		String productConfig = StringUtils.join(productConfigList,"|");
		webXml.saveConfig("ncpmcConfig", productConfig, "农产品名称配置信息"+DateUtils.date2String("yyyyMMdd"));
		return jsonArray;
	}
	
	/**
	 * @Description 配置配置文件中农产品价格菜市场配置信息.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午9:35:38
	 * @param fieldList 菜市场字段字符串
	 * @param fieldNameList 菜市场名称字符串
	 */
	public void updataMarketConfig(List<String> fieldList,List<String> fieldNameList){
		XmlConfig webXml = new XmlConfig(ApplicationListener.webconfigFilePath);
		String configValue = StringUtils.join(fieldList,",")+"|"+StringUtils.join(fieldNameList,",");
		webXml.saveConfig("marketConfig", configValue, "农产品批发市场配置信息"+DateUtils.date2String("yyyyMMdd"));
	}
	
	/**
	 * @Description 根据表格头内容生成数据字段集合.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午9:09:51
	 * @param headerString 表格头部信息
	 * @return
	 */
	public static List<String> getDataFieldList(String headerString){
		List<String> fieldList = new ArrayList<String>();
		String[] strArr = headerString.split("[ ]");
		for(int i=0;i<strArr.length;i++){
			fieldList.add(PinyinUtil.getPinYinHeadChar(strArr[i]));
		}
		return fieldList;
	}
	
	/**
	 * @Description 根据头部字段集合和表格行的数据生成JSON对象.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午9:10:51
	 * @param tds 表格行各列数据
	 * @param fieldList 头部字段集合
	 * @return
	 */
	public static JSONObject readRowData(Elements tds,List<String> fieldList){
		JSONObject data = new JSONObject();
		if(tds != null){
			int count = tds.size();
			String tdText = null;
			String pinyin = null;
			for(int i=0;i<count;i++){
				tdText = tds.get(i).text();
				pinyin = PinyinUtil.getPingYin(tdText);
				data.put(fieldList.get(i), pinyin);
			}
		}
		return data;
	}
	
	public static void main(String[] args) throws IOException {
		AgrProductPrice productPrice = new AgrProductPrice();
//		productPrice.collectProductPrice();
//		productPrice.readPriceURLList(FileUtils.readFileToString(new File("c:/price-pagelist.html")));
		JSONArray dataArray = productPrice.getProductPriceData("201", "");
		System.out.println(dataArray);
		//List<?> list = productPrice.loadPriceData();
		//System.out.println(list);
		System.out.println("OK");
	}
}
