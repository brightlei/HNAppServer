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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;
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
	
	public void collectProductPrice(){
		String xmlFilePath = ApplicationListener.rootPath+"/WEB-INF/resources/xml/webconfig.xml";
		XmlConfig config = new XmlConfig(xmlFilePath);
		String pageUrl = config.getConfigValue("ncpjgPageUrl");
		Document doc;
		try {
			doc = Jsoup.connect(pageUrl).get();
			Elements listDiv = doc.getElementsByAttributeValue("class", "box-content");
			Element divElement = listDiv.first().getElementsByTag("ul").first();
			Elements links = divElement.getElementsByTag("li");
			Element firstElement = links.get(0);
			String date = firstElement.getElementsByTag("span").text();
			String url = firstElement.getElementsByTag("a").get(0).attr("href");
			JSONObject json = getProductPrieceData(date,url);
//			String jsonstr = readCacheData();
//			JSONArray cacheData = JSON.parseArray(jsonstr);
//			cacheData.add(json);
//			int count = cacheData.size();
//			if(count>7){
//				cacheData.remove(0);
//			}
//			saveCacheData(cacheData);
		} catch (IOException e) {
			log.error("IOException", new Throwable(e));
			e.printStackTrace();
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
	
	public JSONObject getProductPrieceData(String date,String dataurl) throws IOException{
		JSONObject json = new JSONObject();
		String xmlString = FileUtils.readFileToString(new File("c:/price.html"));
		Document doc = Jsoup.parse(xmlString);
		//Document doc = Jsoup.connect(dataurl).get();
		System.out.println(doc);
		Elements listDiv = doc.getElementsByAttributeValue("class", "news_content_content");
		Element el = listDiv.first();
		Elements bodys = el.getElementsByTag("tbody");
		int count = bodys.size();
		System.out.println("count="+count);
		Elements trs = null;
		Elements tds = null;
		List<String> strList = null;
		String firstString = null;
		String tdValue = "";
		JSONArray data = new JSONArray();
		Element tableElement = null;
		Element pElement = null;
		for(int i=0;i<count;i++){
			tableElement = bodys.get(i).parent();
			pElement = tableElement.firstElementSibling();
			System.err.println(tableElement.nodeName());
			System.out.println(pElement.nodeName());
			System.out.println("title="+pElement.text());
			trs = bodys.get(i).getElementsByTag("tr");
			for(int k=0;k<trs.size();k++){
				tds = trs.get(k).getElementsByTag("td");
				firstString = tds.get(0).text().trim();
				if(firstString.contains("信息员")){
					continue;
				}
				if(i>0){
					if(firstString.contains("市场名称")){
						continue;
					}
				}
				int tdcount = tds.size();
				if(tdcount==9){
					strList = new ArrayList<String>();
					for(int n=0;n<tdcount;n++){
						tdValue = tds.get(n).text();
						if(i==0&&k==0){
							//如果是第一行"市场名称"，则不进行任何处理
						}else{
							if(tdValue.equals("无")){
								tdValue = "0.00";
							}else{
								if(n==0){
									tdValue = tdValue+"_"+PinyinUtil.getPingYin(tdValue);
								}else{
									tdValue = PinyinUtil.getPingYin(tdValue);
								}
							}
						}
						strList.add(tdValue);
					}
					//System.out.println(strList);
					data.add(strList);
				}
			}
		}
		json.put("time", date);
		json.put("data", data);
		return json;
	}
	
	
	
	public static void main(String[] args) {
		AgrProductPrice productPrice = new AgrProductPrice();
		//productPrice.collectProductPrice();
		String dataurl = "http://zzny.zhengzhou.gov.cn/ncpjg/index.jhtml";
		try {
			productPrice.getProductPrieceData("20181028", dataurl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
