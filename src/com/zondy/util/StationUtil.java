package com.zondy.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;

/**
 * 站点工具类
 * @author 雷志强
 * @version 1.0
 */
public class StationUtil {
	
	private static Logger log = Logger.getLogger(StationUtil.class);
	
	@SuppressWarnings("unchecked")
	public static List<HashMap<String, String>> readStations(){
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		String filepath = ApplicationListener.rootPath+"/WEB-INF/resources/xml/stations.xml";
		File file = new File(filepath);
		if(file.exists()){
			Document doc = null;
			try {
				doc = Dom4jUtils.readXml(new File(filepath));
				Element root = doc.getRootElement();
				list = (List<HashMap<String, String>>)Dom4jUtils.getXmlNodeList(root.elements("Station"));
			} catch (DocumentException e) {
				log.error("DocumentException", new Throwable(e));
			}
		}
		return list;
	}
	
	public static List<JSONObject> readAllStations(){
		List<JSONObject> list = new ArrayList<JSONObject>();
		String filepath = ApplicationListener.rootPath+"/WEB-INF/resources/xml/stations.xml";
		File file = new File(filepath);
		if(file.exists()){
			Document doc = null;
			try {
				doc = Dom4jUtils.readXml(new File(filepath));
				Element root = doc.getRootElement();
				List<?> dataList = Dom4jUtils.getXmlNodeList(root.elements("Station"));
				JSONObject record = null;
				JSONObject row = null;
				for(int i=0;i<dataList.size();i++){
					record = new JSONObject();
					record.put("data", dataList.get(i));
					row = record.getJSONObject("data");
					row.remove("nodevalue");
					row.remove("nodename");
					row.put("name", row.getString("sname"));
					row.put("station_no", row.getString("sno"));
					list.add(row);
				}
			} catch (DocumentException e) {
				log.error("DocumentException", new Throwable(e));
			}
		}
		return list;
	}
	
	public static void updateStation() throws DocumentException, TransformerException, IOException{
		String filepath = ApplicationListener.rootPath+"/WEB-INF/resources/xml/stations.xml";
		File file = new File(filepath);
		if(file.exists()){
			Document doc = Dom4jUtils.readXml(new File(filepath));
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> list = root.elements("Station");
			Element element = null;
			String pingyin = null;
			for(int i=0;i<list.size();i++){
				element = list.get(i);
				pingyin = PinyinUtil.getPingYin(element.attributeValue("sname"));
				element.addAttribute("ename", pingyin);
			}
			Dom4jUtils.writeXml(doc, filepath);
		}
	}
	
	public static List<JSONObject> getAllStation(){
		List<JSONObject> stations = new ArrayList<JSONObject>();
		XmlConfig xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = xml.getConfigValue("getAllStation");
		stations = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		return stations;
	}
	
	public static void main(String[] args) {
		List<JSONObject> stations = getAllStation();
		System.out.println(stations);
	}
}
