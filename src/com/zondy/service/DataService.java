package com.zondy.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.impl.PublicClaims;
import com.zondy.config.XmlConfig;
import com.zondy.database.dao.BaseDAOImpl;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.Base64Utils;
import com.zondy.util.DateUtils;
import com.zondy.util.MD5Utils;

/**
 * 数据服务
 * @author 雷志强
 * @version 1.0
 */
public class DataService {
	
	private static Logger log = Logger.getLogger(DataService.class);
	
	private BaseDAOImpl dao;

	public BaseDAOImpl getDao() {
		return dao;
	}

	public void setDao(BaseDAOImpl dao) {
		this.dao = dao;
	}
	
	/**
	 * 检查用户登录验证.<br>
	 * @param paramJson 登录参数
	 * @return JSONObject
	 */
	public JSONObject checkLogin(JSONObject paramJson){
		JSONObject user = null;
		String filepath = ApplicationListener.sqlconfigFilePath;
		XmlConfig xml = new XmlConfig(filepath);
		String sql = xml.getParamConfig("checkLogin", paramJson);
		log.info("sql="+sql);
		user = (JSONObject)dao.loadObject(sql);
		return user;
	}
	
	/**
	 * 获取用户信息.<br>
	 * @param paramJson
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getUser(JSONObject paramJson){
		JSONObject user = null;
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getParamConfig("getUserInfo", paramJson);
		log.info("sql="+sql);
		List<JSONObject> list = (List<JSONObject>) dao.listAll(sql,1,1);
		if(list != null && list.size()>0){
			user = list.get(0);
		}
		return user;
	}
	
	public int regist(JSONObject paramJson){
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getParamConfig("registUser", paramJson);
		log.info("sql="+sql);
		int ret = ApplicationListener.dao.saveObject(sql);
		return ret;
	}
	
	/**
	 * 更新用户最后使用App时间.<br>
	 * @param paramJson 
	 * @return int
	 */
	public int updateUserUseTime(JSONObject paramJson){
		int result = 0;
		String filepath = ApplicationListener.sqlconfigFilePath;
		XmlConfig xml = new XmlConfig(filepath);
		String sql = xml.getParamConfig("updateUserUsetime", paramJson);
		result = dao.updateObject(sql);
		return result;
	}
	
	/**
	 * 获取用户关注的城市列表(包括当前天气).<br>
	 * @param paramJson
	 * @return List<JSONObject>
	 */
	public List<JSONObject> getUserStations(JSONObject paramJson){
		List<JSONObject> stations = new ArrayList<JSONObject>();
		XmlConfig xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = xml.getParamConfig("getUserStation", paramJson);
		log.info("sql="+sql);
		stations = (List<JSONObject>)dao.listAll(sql);
		return stations;
	}
	
	/**
	 * 保存用户关注的城市列表.<br>
	 * @param paramJson
	 * @return int
	 */
	public int addUserStation(JSONObject paramJson){
		int result = 0;
		String filepath = ApplicationListener.sqlconfigFilePath;
		XmlConfig xml = new XmlConfig(filepath);
		String stationIds = paramJson.getString("stationIds");
		String[] idArr = stationIds.split("[,]");
		JSONObject sqlParam = null;
		String sql = null;
		List<String> sqlList = new ArrayList<String>();
		for(int i=0;i<idArr.length;i++){
			sqlParam = new JSONObject();
			sqlParam.put("username", paramJson.getString("username"));
			sqlParam.put("stationId", idArr[i]);
			sql = xml.getParamConfig("addUserStation", sqlParam);
			sqlList.add(sql);
		}
		result = dao.betchSaveData(sqlList);
		return result;
	}
	
	/**
	 * 删除用户关注的城市列表.<br>
	 * @param paramJson
	 * @return int
	 */
	public int deleteUserStation(JSONObject paramJson){
		int result = 0;
		String filepath = ApplicationListener.sqlconfigFilePath;
		XmlConfig xml = new XmlConfig(filepath);
		String sql = xml.getParamConfig("deleteUserStation", paramJson);
		log.info("sql="+sql);
		result = dao.updateObject(sql);
		return result;
	}
	
	public int updateUserNickname(JSONObject paramJson){
		int result = 0;
		String filepath = ApplicationListener.sqlconfigFilePath;
		XmlConfig xml = new XmlConfig(filepath);
		String nickname = paramJson.getString("nickname");
		//检测用户昵称是否存在
		String sql = xml.getParamConfig("checkNickname",paramJson);
		int count = dao.countAll(sql);
		if(count>0){
			
		}else{
			
		}
//		String sql = xml.getParamConfig("deleteUserStation", paramJson);
//		log.info("sql="+sql);
//		result = dao.updateObject(sql);
		return result;
	}
	
	/**
	 * 修改用户密码.<br>
	 * @param paramJson
	 * @return int
	 */
	public int updateUserPwd(JSONObject paramJson){
		int result = 0;
		String filepath = ApplicationListener.sqlconfigFilePath;
		XmlConfig xml = new XmlConfig(filepath);
		String sql = xml.getParamConfig("getUserInfo", paramJson);
		JSONObject user = (JSONObject)dao.loadObject(sql);
		if(user!=null){
			String userpwd = user.getString("userpwd");
			String oldpwd = paramJson.getString("oldpwd");
			String newpwd = paramJson.getString("newpwd");
			String md5oldpwd = MD5Utils.MD5Encode(oldpwd);
			if(userpwd.equals(md5oldpwd)){
				String md5newpwd = MD5Utils.MD5Encode(newpwd);
				paramJson.put("md5newpwd", md5newpwd);
				sql = xml.getParamConfig("updateUserPwd", paramJson);
				result = dao.updateObject(sql);
			}else{
				result = -1;
			}
		}
		return result;
	}
	
	/**
	 * 更新用户头像.<br>
	 * @param paramJson 参数对象{username:"",userimg:""}
	 * @return int
	 */
	public String updateUserImg(JSONObject paramJson){
		String result = "0:保存用户头像失败";
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		XmlConfig webXml = new XmlConfig(ApplicationListener.webconfigFilePath);
		String userimg = paramJson.getString("userimg");
		String uploadPath = webXml.getConfigValue("uploadFolder");
		String absolutePath = "userimg/user-"+paramJson.getString("username")+".jpg";
		String imgfilepath = uploadPath+"/"+absolutePath;
		try {
			FileUtils.forceMkdir(new File(imgfilepath).getParentFile());
			boolean saveimg = Base64Utils.base64codeToFile(userimg, imgfilepath);
			if(saveimg){
				paramJson.put("userimg", absolutePath);
				String sql = sqlXml.getParamConfig("updateUserImg", paramJson);
				int ret = ApplicationListener.dao.updateObject(sql);
				result = ret+":"+absolutePath;
			}
		} catch (IOException e) {
			result = "0:保存用户头像异常！";
			log.error("[保存用户头像图片异常]IOException", new Throwable(e));
		}
		return result;
	}
	
	public JSONObject getNowData(JSONObject paramJson){
		JSONObject json = new JSONObject();
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		XmlConfig webXml = new XmlConfig(ApplicationListener.webconfigFilePath);
		String grdGroups = webXml.getConfigValue("grdGroups");
		String dataConfig = webXml.getConfigValue("dataFieldConfig");
		String sql = sqlXml.getParamConfig("getNowWeather", paramJson);
		System.out.println(sql);
		JSONObject today = (JSONObject) ApplicationListener.dao.loadObject(sql);
		json.put("weather", today);
		sql = sqlXml.getParamConfig("getTodayForecast", paramJson);
		System.out.println(sql);
		List<JSONObject> list = (List<JSONObject>) ApplicationListener.dao.listAll(sql);
		int count = list.size();
		String[] groupArr = grdGroups.split("[,]");
		JSONObject record = null;
		String key = null;
		JSONObject groupData = null;
		for(int i=0;i<groupArr.length;i++){
			groupData = new JSONObject();
			for(int k=0;k<count;k++){
				record = list.get(k);
				if(record.getString("group_dir").equals(groupArr[i])){
					key = record.getString("element_name")+"_"+record.getString("crop_code");
					groupData.put(key, record.getDoubleValue("element_value"));
				}
			}
			json.put(groupArr[i], groupData);
		}
		json.put("fieldinfo", dataConfig);
		sql = sqlXml.getParamConfig("getStationAlarm", paramJson);
		JSONObject alarminfo = (JSONObject)dao.loadObject(sql);
		if(alarminfo != null){
			String alarmstate = alarminfo.getString("alarmstate");
			if(alarmstate.equals("发布")){
				String alarmtime = alarminfo.getString("alarmtime");
				Date alarmDate = DateUtils.dateString2Date(alarmtime, "yyyy-MM-dd HH:mm:ss");
				Date nowDate = new Date();
				long exetime = nowDate.getTime() - alarmDate.getTime();
				if(exetime<=(8*3600*1000)){
					json.put("alarminfo", alarminfo);
				}else{
					System.out.println("预警已过期！");
				}
			}
		}
		return json;
	}
	
	public JSONObject getHourData(JSONObject paramJson){
		JSONObject json = new JSONObject();
		json.put("data", new JSONArray());
		return json;
	}
	
	public JSONArray getDayData(JSONObject paramJson){
		JSONArray data = new JSONArray();
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getParamConfig("getWeatherDay15Data", paramJson);
		JSONObject record = (JSONObject) ApplicationListener.dao.loadObject(sql);
		if(record != null){
			data = JSON.parseArray(record.getString("jsondata"));
		}
		return data;
	}
	
	public List<JSONObject> getAgrWeather(JSONObject paramJson){
		JSONObject json = new JSONObject();
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getParamConfig("getAgrWeatherData", paramJson);
		log.info("sql="+sql);
		List<JSONObject> list = (List<JSONObject>) ApplicationListener.dao.listAll(sql);
		return list;
	}
	
	/**
	 * 获取农作物列表.<br>
	 * @param paramJson
	 * @return List<JSONObject>
	 */
	public List<JSONObject> getCropList(JSONObject paramJson){
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getParamConfig("getCropList", paramJson);
		log.info("sql="+sql);
		List<JSONObject> list = (List<JSONObject>) ApplicationListener.dao.listAll(sql);
		return list;
	}
	
	/**
	 * 获取农作物病虫病诊治信息.<br>
	 * @param paramJson
	 * @return List<JSONObject>
	 */
	public List<JSONObject> getCropDisease(JSONObject paramJson){
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getParamConfig("getCropDisease", paramJson);
		log.info("sql="+sql);
		List<JSONObject> list = (List<JSONObject>) ApplicationListener.dao.listAll(sql);
		return list;
	}
	/**
	 * 根据ID获取农作物病虫害防治详细信息.<br>
	 * @param paramJson
	 * @return JSONObject
	 */
	public JSONObject getCropDiseaseInfo(JSONObject paramJson){
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getParamConfig("getCropDiseaseInfo", paramJson);
		log.info("sql="+sql);
		JSONObject record = (JSONObject) ApplicationListener.dao.loadObject(sql);
		return record;
	}
	/**
	 * 获取用户农场列表.<br>
	 * @param paramJson
	 * @return JSONObject
	 */
	public List<JSONObject> getUserFarm(JSONObject paramJson){
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getParamConfig("getUserFarm", paramJson);
		log.info("sql="+sql);
		List<JSONObject> list = (List<JSONObject>) ApplicationListener.dao.listAll(sql);
		return list;
	}
	/**
	 * 添加用户农场信息.<br>
	 * @param paramJson
	 * @return int
	 */
	public int addUserFarm(JSONObject paramJson){
		int result = 0;
		String filepath = ApplicationListener.sqlconfigFilePath;
		XmlConfig sqlXml = new XmlConfig(filepath);
		String sql = sqlXml.getParamConfig("addUserFarm", paramJson);
		log.info("sql="+sql);
		result = dao.saveObject(sql);
		return result;
	}
	/**
	 * 删除用户农场信息.<br>
	 * @param paramJson
	 * @return int
	 */
	public int deleteUserFarm(JSONObject paramJson){
		int result = 0;
		String filepath = ApplicationListener.sqlconfigFilePath;
		XmlConfig sqlXml = new XmlConfig(filepath);
		String sql = sqlXml.getParamConfig("deleteUserFarm", paramJson);
		log.info("sql="+sql);
		result = dao.saveObject(sql);
		return result;
	}
}
