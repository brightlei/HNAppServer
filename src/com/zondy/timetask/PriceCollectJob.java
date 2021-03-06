package com.zondy.timetask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.dom4j.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.collect.AgrProductPrice;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.Dom4jUtils;
import com.zondy.util.PinyinUtil;
/**
 * 农产品价格采集定时任务
 * @author LZQ  
 * @date 2019年1月15日 下午9:41:01
 * @version 1.0
 */
public class PriceCollectJob extends BaseJob {

	@SuppressWarnings("deprecation")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String instName = context.getJobDetail().getName();
		String groupName = context.getJobDetail().getGroup();
		log("[任务调度]--"+instName+"--开始");
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		HashMap<String, String> configMap = (HashMap<String, String>)dataMap.get("configMap");
		// 定时任务重复执行次数，默认执行一次
		int retryTimes = 1;
		// 重复执行间隔时间，默认为5分钟
		int retrySpaceTime = 5;
		if(configMap.get("retryTimes") != null){
			retryTimes = Integer.parseInt(configMap.get("retryTimes"));
		}
		if(configMap.get("retrySpaceTime") != null){
			retrySpaceTime = Integer.parseInt(configMap.get("retrySpaceTime"));
			log("执行["+retryTimes+"]次，每次间隔["+retrySpaceTime+"]分钟");
		}
		int doTimes = 1;
		while(doTimes <= retryTimes){
			log("第【"+doTimes+"】次执行【"+configMap.get("taskName")+"】定时任务");
			// 采集农产品价格
			AgrProductPrice agrProductPrice = new AgrProductPrice();
			agrProductPrice.collectProductPrice();
			//doAction();
			doTimes++;
			try {
				Thread.sleep(retrySpaceTime*60*1000);
			} catch (InterruptedException e) {
				System.err.println("InterruptedException:"+e);
			}
		}
		log("[任务调度]--"+instName+"--结束");
		logLine();
	}
	
	public void doAction(){
		try {
			Document doc = Jsoup.connect("http://www.sdnw.gov.cn/ncpjg/index.jhtml").get();
			System.out.println(doc);
			Elements listDiv = doc.getElementsByAttributeValue("class", "box-content");
			Element divElement = listDiv.first().getElementsByTag("ul").first();
			Elements links = divElement.getElementsByTag("li");
			Element firstElement = links.get(0);
			String date = firstElement.getElementsByTag("span").text();
			String url = firstElement.getElementsByTag("a").get(0).attr("href");
			String newDt = getPriceDateTime();
			log("data="+date+",pageurl="+url);
			if(!date.equals(newDt)){
				JSONArray jsonArray = getPriceData(date,url);
				savePriceInfo(date, jsonArray);
				log("定时抓取蔬菜价格["+date+"]保存到配置文件成功！");
			}else{
				log("网站数据未更新，最新数据时间为："+date);
			}
		} catch (IOException e) {
			log("doAction[IOException]-"+e.getLocalizedMessage());
		} catch (DocumentException e) {
			log("doAction[DocumentException]-"+e.getLocalizedMessage());
		} catch (TransformerException e) {
			log("doAction[TransformerException]-"+e.getLocalizedMessage());
		}
	}
	
	public static JSONArray getPriceData(String date,String url) throws IOException{
		JSONArray jsonArray = new JSONArray();
		Document doc = Jsoup.connect(url).get();
		Elements listDiv = doc.getElementsByAttributeValue("class", "news_content_content");
		Element el = listDiv.first();
		Elements bodys = el.getElementsByTag("tbody");
		int count = bodys.size();
		Elements trs = null;
		Elements tds = null;
		List<String> strList = null;
		JSONObject jsonObject = null;
		String firstString = null;
		String tdValue = "";
		for(int i=0;i<count;i++){
			trs = bodys.get(i).getElementsByTag("tr");
			for(int k=0;k<trs.size();k++){
				tds = trs.get(k).getElementsByTag("td");
				int tdcount = tds.size();
				if(tdcount==9){
					strList = new ArrayList<String>();
					for(int n=0;n<tdcount;n++){
						tdValue = tds.get(n).text();
						strList.add(tdValue);
					}
					firstString = strList.get(0);
					firstString = firstString.replaceAll(" ", "");
					firstString = firstString.replaceAll("	", "");
					firstString = firstString.replaceAll("　", "");
					if(firstString.contains("市场名称") || firstString.contains("信息员") || firstString.equals("")){
						continue;
					}
					jsonObject = new JSONObject();
					jsonObject.put("nodeName", PinyinUtil.getPingYin(strList.get(0)));
					jsonObject.put("name", strList.get(0));
					jsonObject.put("wbgjpfsc", strList.get(1));
					jsonObject.put("mzscpfsc", strList.get(2));
					jsonObject.put("czscpfsc", strList.get(3));
					jsonObject.put("gzscpfsc", strList.get(4));
					jsonObject.put("gpnmsc", strList.get(5));
					jsonObject.put("bsnmsc", strList.get(6));
					jsonObject.put("wslnmsc", strList.get(7));
					jsonObject.put("tbzlnmsc", strList.get(8));
					jsonArray.add(jsonObject);
				}
			}
		}
		return jsonArray;
	}
	
	public static void savePriceInfo(String date,JSONArray data) throws DocumentException, TransformerException, IOException{
		String xmlFilePath = ApplicationListener.rootPath+"/resources/price/agri-price.xml";
		File xmlFile = new File(xmlFilePath);
		org.dom4j.Document doc = Dom4jUtils.readXml(xmlFile);
		org.dom4j.Element rootElement = doc.getRootElement();
		List list = rootElement.elements();
		int count = list.size();
		org.dom4j.Element priceElement = rootElement.element("priceInfo-"+date);
		if(priceElement==null){
			if(count>=7){
				rootElement.remove((org.dom4j.Element)list.get(0));
			}
			priceElement = rootElement.addElement("priceInfo-"+date);
		}else{
			List els = priceElement.elements();
			int elCount = els.size();
			for(int i=0;i<elCount;i++){
				priceElement.remove((org.dom4j.Element)els.get(i));
			}
		}
		int dataCount = data.size();
		JSONObject rowInfo = null;
		org.dom4j.Element el = null;
		for(int i=0;i<dataCount;i++){
			rowInfo = data.getJSONObject(i);
			el = priceElement.addElement(rowInfo.getString("nodeName"));
			el.setAttributeValue("title", rowInfo.getString("name"));
			el.setAttributeValue("wbgjpfsc", rowInfo.getString("wbgjpfsc"));
			el.setAttributeValue("mzscpfsc", rowInfo.getString("mzscpfsc"));
			el.setAttributeValue("czscpfsc", rowInfo.getString("czscpfsc"));
			el.setAttributeValue("gzscpfsc", rowInfo.getString("gzscpfsc"));
			el.setAttributeValue("gpnmsc", rowInfo.getString("gpnmsc"));
			el.setAttributeValue("bsnmsc", rowInfo.getString("bsnmsc"));
			el.setAttributeValue("wslnmsc", rowInfo.getString("wslnmsc"));
			el.setAttributeValue("tbzlnmsc", rowInfo.getString("tbzlnmsc"));
		}
		Dom4jUtils.writeXml(doc, xmlFilePath);
	}
	
	private String getPriceDateTime() throws DocumentException{
		String dt = "";
		String xmlFilePath = ApplicationListener.rootPath+"/appdata/agri-price.xml";
		File xmlFile = new File(xmlFilePath);
		org.dom4j.Document doc = Dom4jUtils.readXml(xmlFile);
		org.dom4j.Element rootElement = doc.getRootElement();
		List list = rootElement.elements();
		int count = list.size();
		if(count>0){
			org.dom4j.Element element = (org.dom4j.Element)list.get(count-1);
			String nodeName = element.getName();
			dt = nodeName.replaceAll("priceInfo-", "");
		}
		return dt;
	}
	
	//读取txt一行内容转换成List数组
	public static List<String> getStringList(String str){
		List<String> list = new ArrayList<String>();
		if(str!=null&&!str.equals("")){
			String[] tmpArr = str.split(" ");
			int count = tmpArr.length;
			for(int i=0;i<count;i++){
				if(!tmpArr[i].trim().equals("")){
					list.add(tmpArr[i].trim());
				}
			}
		}
		return list;
	}
	
	public static void main(String[] args) {
		PriceCollectJob job = new PriceCollectJob();
		job.doAction();
		System.out.println("OK");
	}
}
