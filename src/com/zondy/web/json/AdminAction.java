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

import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;

/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class AdminAction extends BaseAction{
	
	private static Logger log = LoggerFactory.getLogger(AdminAction.class);
	/**
	 * long:serialVersionUID 用一句话描述这个变量
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * SQL语句配置文件
	 */
	public XmlConfig sqlxml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
	
	/**
	 * 获取字典分类.<br>
	 * @return String
	 */
	public String getDicType(){
		String sql = sqlxml.getParamConfig("admin_getDicType", requestParam);
		log.info("sql={}",sql);
		List<JSONObject> list = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		this.dataMap.put("data", list);
		return "map";
	}
	
	/**
	 * 获取字典分类明细数据.<br>
	 * @return String
	 */
	public String getDicData(){
		String sql = sqlxml.getParamConfig("admin_getDicData", requestParam);
		log.info("sql={}",sql);
		List<JSONObject> list = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		this.dataMap.put("data", list);
		return "map";
	}
	/**
	 * 保存字典数据.<br>
	 * @return String
	 */
	public String saveDicData(){
		String sql = sqlxml.getParamConfig("admin_saveDicData", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.saveObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	/**
	 * 修改字典数据.<br>
	 * @return String
	 */
	public String editDicData(){
		String sql = sqlxml.getParamConfig("admin_editDicData", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	/**
	 * 删除字典数据.<br>
	 * @return String
	 */
	public String deleteDicData(){
		String sql = sqlxml.getParamConfig("admin_deleteDicData", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	/**
	 * 获取App手机安装信息.<br>
	 * @return String
	 */
	public String getAppInstall(){
		String sql = sqlxml.getParamConfig("admin_getAppInstallCount",requestParam);
		int count = ApplicationListener.dao.countAll(sql);
		this.dataMap = new LinkedHashMap<String, Object>();
		this.dataMap.put("total", count);
		sql = sqlxml.getParamConfig("admin_getAppInstall", requestParam);
		log.info("sql={}",sql);
		int page = requestParam.getIntValue("page");
		int pagesize = requestParam.getIntValue("rows");
		List<JSONObject> list = (List<JSONObject>)ApplicationListener.dao.listAll(sql,page,pagesize);
		this.dataMap.put("rows", list);
		return "map";
	}
	
	public String getStations(){
		String sql = sqlxml.getParamConfig("admin_getStationList",requestParam);
		log.info("sql={}",sql);
		List<JSONObject> list = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		this.dataMap.put("data", list);
		return "map";
	}
	
	public String getCropDocument(){
		String sql = sqlxml.getParamConfig("admin_getCropDocument",requestParam);
		log.info("sql={}",sql);
		List<JSONObject> list = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		this.dataMap.put("data", list);
		return "map";
	}
	
	public String addCropDocument(){
		String sql = sqlxml.getParamConfig("admin_addCropDocument", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.saveObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String editCropDocument(){
		String sql = sqlxml.getParamConfig("admin_editCropDocument", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String deleteCropDocument(){
		String sql = sqlxml.getParamConfig("admin_deleteCropDocument", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String getDistDocument(){
		String sql = sqlxml.getParamConfig("admin_getDistDocument",requestParam);
		log.info("sql={}",sql);
		List<JSONObject> list = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		this.dataMap.put("data", list);
		return "map";
	}
	
	public String addDistDocument(){
		String sql = sqlxml.getParamConfig("admin_addDistDocument", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.saveObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String editDistDocument(){
		String sql = sqlxml.getParamConfig("admin_editDistDocument", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String deleteDistDocument(){
		String sql = sqlxml.getParamConfig("admin_deleteDistDocument", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String getCropList(){
		String sql = sqlxml.getParamConfig("admin_getCropList",requestParam);
		log.info("sql={}",sql);
		List<JSONObject> list = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		this.dataMap.put("data", list);
		return "map";
	}
	
	public String addCrop(){
		String sql = sqlxml.getParamConfig("admin_addCrop", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.saveObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String editCrop(){
		String sql = sqlxml.getParamConfig("admin_editCrop", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String deleteCrop(){
		String sql = sqlxml.getParamConfig("admin_deleteCrop", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String getCropDisease(){
		String sql = sqlxml.getParamConfig("admin_getCropDisease",requestParam);
		log.info("sql={}",sql);
		List<JSONObject> list = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		this.dataMap.put("data", list);
		return "map";
	}
	
	public String addCropDisease(){
		String sql = sqlxml.getParamConfig("admin_addCropDisease", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.saveObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String editCropDisease(){
		String sql = sqlxml.getParamConfig("admin_addCropDisease", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String deleteCropDisease(){
		String sql = sqlxml.getParamConfig("admin_deleteCropDisease", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String getRegistUser(){
		String sql = sqlxml.getParamConfig("admin_getUserCount",requestParam);
		int count = ApplicationListener.dao.countAll(sql);
		this.dataMap = new LinkedHashMap<String, Object>();
		this.dataMap.put("total", count);
		sql = sqlxml.getParamConfig("admin_getUserList", requestParam);
		log.info("sql={}",sql);
		int page = requestParam.getIntValue("page");
		int pagesize = requestParam.getIntValue("rows");
		List<JSONObject> list = (List<JSONObject>)ApplicationListener.dao.listAll(sql,page,pagesize);
		this.dataMap.put("rows", list);
		return "map";
	}
	
	public String getUserStation(){
		String sql = sqlxml.getParamConfig("admin_getUserStation",requestParam);
		log.info("sql={}",sql);
		List<JSONObject> list = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		this.dataMap.put("data", list);
		return "map";
	}
	
	public String resetUserPwd(){
		String sql = sqlxml.getParamConfig("admin_resetUserPwd", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	public String deleteUser(){
		String sql = sqlxml.getParamConfig("admin_deleteUser", requestParam);
		log.info("sql={}",sql);
		int ret = ApplicationListener.dao.updateObject(sql);
		this.dataMap.put("data", ret);
		return "map";
	}
}
