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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.DateUtils;
import com.zondy.util.HttpClientUtils;

/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class WeatherAlarm {
	//统一日志对象
	private static Logger log = LoggerFactory.getLogger(WeatherAlarm.class);
	/**
	 * 获取预警信息的token值.<br>
	 * @return void
	 */
	public String getToken(){
		String token = null;
		XmlConfig webXml = new XmlConfig(ApplicationListener.webconfigFilePath);
		String apiurl = webXml.getConfigValue("getYuJingTokenUrl");
		log.info("获取token的接口地址{}",apiurl);
		String tokenparam = webXml.getConfigValue("getYuJingTokenParam");
		log.info("获取token接口的参数：{}",tokenparam);
		try {
			//调用API接口获取token值
			String jsonstr = HttpClientUtils.sendPost(apiurl, tokenparam, null);
			if(jsonstr != null && !jsonstr.equals("")){
				JSONObject json = JSONObject.parseObject(jsonstr);
				token = json.getString("access_token");
				int expiresIn = json.getIntValue("expires_in");
				Date tokenDate = DateUtils.calculateBySecond(new Date(), expiresIn);
				String tokenDateTime = DateUtils.date2String("yyyy-MM-dd HH:mm:ss", tokenDate);
				String yuJingToken = webXml.getConfigValue("yuJingToken");
				if(yuJingToken==null || (yuJingToken != null && !yuJingToken.equals(token))){
					System.out.println("aaaaaaa");
					webXml.saveConfig("yuJingToken", token, "预警token值90天有效");
					webXml.saveConfig("yuJingTokenTime", tokenDateTime, "预警token有效期时间");
				}else{
					System.out.println("bbbbbbb");
				}
			}
		} catch (IOException e) {
			log.error("获取预警token异常：{}", e.getMessage());
		}
		return token;
	}
	
	public void checkToken(){
		XmlConfig webXml = new XmlConfig(ApplicationListener.webconfigFilePath);
		String tokenTime = webXml.getConfigValue("yuJingTokenTime");
		Date tokenDt = DateUtils.dateString2Date(tokenTime, "yyyy-MM-dd HH:mm:ss");
		Date nowDt = new Date();
		long nowtime = nowDt.getTime();
		long token_time = tokenDt.getTime();
		if(nowtime >= token_time){
			log.info("token值已过期，重新生成token信息并保存！");
			//getToken();
		}else{
			log.debug("token值未过期！到期时间为："+tokenTime.toString());
		}
	}
	
	/**
	 * 获取所有城市的预警信息.<br>
	 * @return JSONArray
	 */
	public JSONArray getCityAlarm(){
		JSONArray alarms = new JSONArray();
		XmlConfig webXml = new XmlConfig(ApplicationListener.webconfigFilePath);
		checkToken();
		String token = webXml.getConfigValue("yuJingToken");
		JSONObject param = new JSONObject();
		param.put("token", token);
		String cityAlarmUrl = webXml.getParamConfig("getYuJingInfoUrl", param);
		String jsonstr;
		try {
			System.out.println(cityAlarmUrl);
			HashMap<String, String> headMap = new LinkedHashMap<String, String>();
			jsonstr = HttpClientUtils.sendGet(cityAlarmUrl, headMap);
			System.out.println(jsonstr);
			if(jsonstr!=null && !jsonstr.equals("")){
				JSONObject json = JSONObject.parseObject(jsonstr);
				if(json.getString("status").equals("OK")){
					alarms = json.getJSONArray("alarms");
				}
			}
		} catch (IOException e) {
			log.error("获取预警信息异常：{}",e.getMessage());
			getToken();
			//e.printStackTrace();
		}
		return alarms;
	}
	
	public static void main(String[] args) {
		WeatherAlarm alarm = new WeatherAlarm();
//		alarm.getToken();
		alarm.getCityAlarm();
		//alarm.checkToken();
		System.out.println("OK");
	}
}
