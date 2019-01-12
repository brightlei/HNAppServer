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

package com.zondy.web.json;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zondy.collect.GrdDataCollect;
import com.zondy.collect.WeatherAlarmUtils;
import com.zondy.collect.WeatherForecast;
import com.zondy.collect.WeatherUtil;
import com.zondy.database.dao.BaseDAOImpl;

/**
 * 模块名称：ForecastAction									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2014-5-16 下午04:57:54					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2014-5-16 下午04:57:54					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class WebAction extends BaseAction{
	private static Logger log = Logger.getLogger(WebAction.class);
	//long:serialVersionUID 用一句话描述这个变量
	private static final long serialVersionUID = 1L;
	
	private BaseDAOImpl dao;
	
	public BaseDAOImpl getDao() {
		return dao;
	}

	public void setDao(BaseDAOImpl dao) {
		this.dao = dao;
	}

	public String sum(){
		int a = requestParam.getIntValue("a");
		int b = requestParam.getIntValue("b");
		int c = a + b;
		this.dataMap.put("data", c);
		return "map";
	}
	/**
	 * 执行实时天气预报数据采集.<br>
	 * @return String
	 */
	public String collect_now_weather(){
		WeatherForecast.collectWeatherForecast();
		this.dataMap.put("data", "实时天气预报数据采集成功！");
		return "map";
	}
	/**
	 * 执行15天天气预报数据采集.<br>
	 * @return String
	 */
	public String collect_day15_weather(){
		WeatherUtil.collectDay15Weather();
		this.dataMap.put("data", "15天天气预报数据采集成功！");
		return "map";
	}
	/**
	 * 天气预警数据采集.<br>
	 * @return String
	 */
	public String collect_weather_alarm(){
		WeatherAlarmUtils.collectAlarm();
		this.dataMap.put("data", "天气预警数据采集成功！");
		return "map";
	}
	/**
	 * 格点产品数据采集.<br>
	 * @return String
	 */
	public String collect_grddata(){
		GrdDataCollect.readGrdGroups();
		this.dataMap.put("data", "格点产品数据采集成功！");
		return "map";
	}
}
