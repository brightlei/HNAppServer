package com.zondy.web.json;

import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.collect.AgrProductPrice;
import com.zondy.collect.WeatherAlarm;
import com.zondy.collect.WeatherAlarmUtils;
import com.zondy.collect.WeatherForecast;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;
import com.zondy.service.DataService;
import com.zondy.util.JwtToken;
import com.zondy.util.MD5Utils;
import com.zondy.util.StationUtil;

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
public class AppAction extends BaseAction{
	private static Logger log = Logger.getLogger(AppAction.class);
	//long:serialVersionUID 用一句话描述这个变量
	private static final long serialVersionUID = 1L;
	
	private DataService dataService = (DataService)ApplicationListener.ctx.getBean("dataService");
	
	public DataService getDataService() {
		return dataService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	/**
	 * 获取找回密码问题列表.<br>
	 * @return String
	 */
	public String pwd_question_list(){
		XmlConfig sqlxml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlxml.getParamConfig("pwdQuestionList", requestParam);
		log.info("sql="+sql);
		List<JSONObject> list = (List<JSONObject>) ApplicationListener.dao.listAll(sql);
		this.dataMap.put("data", list);
		return "map";
	}
	
	/**
	 * 检查找回密码问题答案.<br>
	 * @return String
	 */
	public String check_question_answer(){
		XmlConfig sqlxml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlxml.getParamConfig("getUserInfo", requestParam);
		log.info("sql="+sql);
		JSONObject user = (JSONObject)ApplicationListener.dao.loadObject(sql);
		log.info("user="+user);
		if(user!=null){
			String answer = user.getString("pwd_answer");
			if(answer.equals(requestParam.getString("pwd_answer"))){
				this.dataMap.put("data", MD5Utils.MD5Encode(answer));
			}else{
				this.dataMap.put("code", 102);
				this.dataMap.put("msg", "找回密码问题答案不正确！");
			}
		}else{
			this.dataMap.put("code", 101);
			this.dataMap.put("msg", "该登录账号不存在！");
		}
		
		return "map";
	}
	/**
	 * 找回密码.<br>
	 * @return String
	 */
	public String findpwd(){
		XmlConfig sqlxml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlxml.getParamConfig("getUserInfo", requestParam);
		log.info("sql="+sql);
		JSONObject user = (JSONObject)ApplicationListener.dao.loadObject(sql);
		log.info("user="+user);
		if(user!=null){
			String answer = user.getString("pwd_answer");
			String md5answer = MD5Utils.MD5Encode(answer);
			System.out.println(md5answer);
			System.out.println(requestParam);
			if(md5answer.equals(requestParam.getString("answercode"))){
				requestParam.put("md5newpwd", MD5Utils.MD5Encode(requestParam.getString("userpwd")));
				System.out.println(requestParam);
				sql = sqlxml.getParamConfig("updateUserPwd", requestParam);
				int ret = ApplicationListener.dao.updateObject(sql);
				this.dataMap.put("data", ret+":用户密码修改成功！");
			}else{
				this.dataMap.put("code", 102);
				this.dataMap.put("msg", "找回密码问题答案不正确！");
			}
		}else{
			this.dataMap.put("code", 101);
			this.dataMap.put("msg", "该登录账号不存在！");
		}
		return "map";
	}
	
	/**
	 * 用户注册模块.<br>
	 * @return String
	 */
	public String regist(){
		String username = requestParam.getString("username");
		String nickname = requestParam.getString("nickname");
		if(checkParamNull(username, 101, "用户名不能为空！")){
			return "map";
		}
		if(checkParamNull(requestParam.getString("userpwd"), 102, "用户密码不能为空！")){
			return "map";
		}
		if(checkParamNull(requestParam.getString("nickname"), 103, "用户昵称不能为空！")){
			return "map";
		}
		if(checkParamNull(requestParam.getString("pwd_question"), 104, "找回密码问题不能为空！")){
			return "map";
		}
		if(checkParamNull(requestParam.getString("pwd_answer"), 105, "问题答案不能为空！")){
			return "map";
		}
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getParamConfig("checkUserExist", requestParam);
		System.out.println(sql);
		int count = ApplicationListener.dao.countAll(sql);
		if(count>0){
			this.dataMap.put("code", 201);
			this.dataMap.put("msg", "该用户名已存在！");
			return "map";
		}
		sql = sqlXml.getParamConfig("checkNickname", requestParam);
		count = ApplicationListener.dao.countAll(sql);
		if(count>0){
			this.dataMap.put("code", 202);
			this.dataMap.put("msg", "该用户昵称已存在！");
			return "map";
		}
		requestParam.put("userpwd", MD5Utils.MD5Encode(requestParam.getString("userpwd")));
		int ret = dataService.regist(requestParam);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	/**
	 * 用户登录验证.<br>
	 * @return String
	 */
	public String login(){
		JSONObject user = dataService.checkLogin(requestParam);
		if(user==null){
			this.dataMap.put("code", 101);
			this.dataMap.put("msg", "该用户名不存在！");
		}else{
			String username = requestParam.getString("username");
			String userpwd = user.getString("userpwd");
			String password = requestParam.getString("userpwd");
			String md5pwd = MD5Utils.MD5Encode(password);
			if(userpwd.equals(md5pwd)){
				List<JSONObject> user_stations = dataService.getUserStations(requestParam);
				user.put("user_stations", user_stations);
				user.remove("userpwd");
				dataService.updateUserUseTime(requestParam);
				int expiresMinute = 7*24*60;
				String accessToken = JwtToken.createToken(username, expiresMinute);
				String refreshToken = JwtToken.createToken(username, expiresMinute+5);
				this.dataMap.put("accessToken", accessToken);
				this.dataMap.put("refreshToken", refreshToken);
				this.dataMap.put("data", user);
			}else{
				this.dataMap.put("code", 102);
				this.dataMap.put("msg", "用户名密码不正确！");
			}
		}
		return "map";
	}
	/**
	 * 获取用户关注的站点.<br>
	 * @return String
	 */
	public String userStation(){
		List<JSONObject> userStationList = dataService.getUserStations(this.requestParam);
		this.dataMap.put("data", userStationList);
		return "map";
	}
	/**
	 * 获取所有站点信息.<br>
	 * @return String
	 */
	public String getAllStation(){
		List<JSONObject> stationList = StationUtil.getAllStation();
		this.dataMap.put("data", stationList);
		return "map";
	}
	
	/**
	 * 添加用户关注站点.<br>
	 * @return String
	 */
	public String addUserStation(){
		int result = dataService.addUserStation(this.requestParam);
		this.dataMap.put("data", result);
		return "map";
	}
	
	/**
	 * 删除用户关注站点.<br>
	 * @return
	 * @return String
	 */
	public String deleteUserStation(){
		int result = dataService.deleteUserStation(this.requestParam);
		this.dataMap.put("data", result);
		return "map";
	}
	/**
	 * 获取当前天气预报.<br>
	 * @return String
	 */
	public String getNowData(){
		JSONObject json = dataService.getNowData(this.requestParam);
		this.dataMap.put("data",json);
		return "map";
	}
	
	/**
	 * 获取小时天气预报.<br>
	 * @return
	 * @return String
	 */
	public String getHourData(){
		JSONObject json = dataService.getHourData(this.requestParam);
		this.dataMap.put("data", new JSONArray());
		return "map";
	}
	/**
	 * 获取每日天气预报.<br>
	 * @return String
	 */
	public String getDayData(){
		JSONArray list = dataService.getDayData(this.requestParam);
		this.dataMap.put("data", list);
		return "map";
	}
	/**
	 * 获取农用天气预报.<br>
	 * @return String
	 */
	public String getAgrWeather(){
		List<JSONObject> list = dataService.getAgrWeather(this.requestParam);
		this.dataMap.put("data", list);
		return "map";
	}
	/**
	 * 修改用户昵称.<br>
	 * @return String
	 */
	public String updateUserNickname(){
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getParamConfig("checkNickname", requestParam);
		log.info("sql="+sql);
		int count = ApplicationListener.dao.countAll(sql);
		if(count>0){
			this.dataMap.put("code", 101);
			this.dataMap.put("msg", "该昵称已被别人占用！请换一个试试！");
		}else{
			sql = sqlXml.getParamConfig("updateUserNickname", requestParam);
			int ret = ApplicationListener.dao.updateObject(sql);
			this.dataMap.put("data", ret);
		}
		return "map";
	}
	
	/**
	 * 修改用户密码.<br>
	 * @return
	 * @return String
	 */
	public String updateUserPwd(){
		int result = dataService.updateUserPwd(this.requestParam);
		if(result==1){
			this.dataMap.put("data", "用户密码修改成功！");
		}else if(result==-1){
			this.dataMap.put("code", 106);
			this.dataMap.put("msg", "用户原密码不正确！");
		}else{
			this.dataMap.put("code", 107);
			this.dataMap.put("msg", "用户不存在！");
		}
		return "map";
	}
	
	/**
	 * 更新用户头像.<br>
	 * @return String
	 */
	public String updateUserImg(){
		String result = dataService.updateUserImg(this.requestParam);
		String[] tArr = result.split("[:]");
		if(tArr[0].equals("1")){
			this.dataMap.put("data", tArr[1]);
		}else{
			this.dataMap.put("code", 108);
			this.dataMap.put("msg", "用户头像修改失败！");
		}
		return "map";
	}
	/**
	 * 获取农作物列表.<br>
	 * @return String
	 */
	public String getCropList(){
		List<JSONObject> data = dataService.getCropList(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	/**
	 * 获取农作物病虫害防治信息.<br>
	 * @return String
	 */
	public String getCropDisease(){
		List<JSONObject> data = dataService.getCropDisease(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	/**
	 * 根据ID获取病虫害详细信息.<br>
	 * @return String
	 */
	public String getCropDiseaseInfo(){
		JSONObject data = dataService.getCropDiseaseInfo(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	/**
	 * 添加用户农场信息.<br>
	 * @return String
	 */
	public String addUserFarm(){
		int data = dataService.addUserFarm(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	/**
	 * 获取用户农场信息.<br>
	 * @return String
	 */
	public String getUserFarm(){
		List<JSONObject> data = dataService.getUserFarm(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	/**
	 * 删除用户农场信息.<br>
	 * @return String
	 */
	public String deleteUserFarm(){
		int data = dataService.deleteUserFarm(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	/**
	 * 获取当日所有城市预警信息.<br>
	 * @return String
	 */
	public String getWeatherAlarm(){
		WeatherAlarm alarm = new WeatherAlarm();
		JSONArray alarms = alarm.getCityAlarm();
		this.dataMap.put("data", alarms);
		return "map";
	}
	
	/**
	 * @Description 添加用户农场农作物.<br>
	 * @author LZQ
	 * @date 2019年1月13日 上午1:50:51
	 * @return 返回结果对象
	 */
	public String addFarmCrop(){
		int data = dataService.addUserFarmCrop(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	
	/**
	 * @Description 修改用户农场农作物信息.<br>
	 * @author LZQ
	 * @date 2019年1月13日 上午1:51:18
	 * @return 返回结果对象
	 */
	public String editFarmCrop(){
		int data = dataService.editUserFarmCrop(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	
	/**
	 * @Description 获取用户农场农作物列表.<br>
	 * @author LZQ
	 * @date 2019年1月13日 上午1:51:37
	 * @return 返回结果对象
	 */
	public String getFarmCrop(){
		List data = dataService.getUserFarmCrop(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	
	/**
	 * @Description 删除用户农场农作物信息.<br>
	 * @author LZQ
	 * @date 2019年1月13日 上午1:51:56
	 * @return 返回结果对象
	 */
	public String deleteFarmCrop(){
		int data = dataService.deleteUserFarmCrop(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	/**
	 * @Description 获取农场作物天气预报.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午10:17:56
	 * @return
	 */
	public String getFarmCropForcast(){
		List data = dataService.getUserFarmCropForecast(this.requestParam);
		this.dataMap.put("data", data);
		return "map";
	}
	
	/**
	 * @Description 获取最近一周农产品价格.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午10:21:29
	 * @return
	 */
	public String getProductPrice(){
		AgrProductPrice price = new AgrProductPrice();
		List<?> data = price.loadPriceData();
		XmlConfig webXml = new XmlConfig(ApplicationListener.webconfigFilePath);
		String marketConfig = webXml.getConfigValue("marketConfig");
		String productConfig = webXml.getConfigValue("ncpmcConfig");
		this.dataMap.put("data", data);
		this.dataMap.put("markets", marketConfig);
		this.dataMap.put("products", productConfig);
		return "map";
	}
	
	/**
	 * @Description 保存用户登录日志.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午10:43:01
	 * @return
	 */
	public String saveUserLoginLog(){
		int ret = dataService.saveUserLoginLog(requestParam);
		this.dataMap.put("data", ret);
		return "map";
	}
	
	/**
	 * @Description 采集最新预警信息.<br>
	 * @author LZQ
	 * @date 2019年1月14日 上午12:59:16
	 * @return
	 */
	public String collectAlarmInfo(){
		String result = WeatherAlarmUtils.collectHeNanAlarm();
		this.dataMap.put("data", result);
		return "map";
	}
	/**
	 * @Description 采集所有站点最新天气预报.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午1:24:02
	 * @return
	 */
	public String collectNowData(){
		WeatherForecast.collectWeatherForecast();
		this.dataMap.put("data", "ok");
		return "map";
	}
	
	/**
	 * @Description 采集农产品价格数据.<br>
	 * @author LZQ
	 * @date 2019年1月15日 下午9:48:12
	 * @return
	 */
	public String collectProductPrice(){
		long st = System.currentTimeMillis();
		AgrProductPrice price = new AgrProductPrice();
		price.collectProductPrice();
		long et = System.currentTimeMillis();
		int second = Math.round((et-st)/1000);
		this.dataMap.put("data", "农产品价格数据采集完毕！耗时："+second+"秒");
		return "map";
	}
}
