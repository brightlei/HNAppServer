package com.zondy.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.zondy.database.dao.BaseDAOImpl;
import com.zondy.timetask.TaskJobAction;
import com.zondy.util.DateUtils;
import com.zondy.util.StationUtil;

/**
 * 模块名称：ApplicationListener									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2013-9-17 上午09:44:15					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2013-9-17 上午09:44:15					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class ApplicationListener implements ServletContextListener{
	
	private static Logger log = Logger.getLogger(ApplicationListener.class);
	
	public static JSONObject onlineUsers = new JSONObject();
	
	/**应用缓存对象*/
	public static HashMap<String, Object> appCache = new LinkedHashMap<String, Object>();
	/**系统发布目录*/
	public static String rootPath = "D:/JavaWorkSpaces/HNAppServer/WebRoot/";
	//public static String rootPath = "D:/JavaApp/tomcat-8.0.50/webapps/HNAppServer/";
	/**系统web目录*/
	public static String basePath = "";
	/**web配置文件路径**/
	public static String webconfigFilePath = rootPath + "WEB-INF/resources/xml/webconfig.xml";
	/**SQL语句配置文件路径**/
	public static String sqlconfigFilePath = rootPath + "WEB-INF/resources/xml/SQLConfig.xml";
	/**web请求上下文*/
	private static ServletContext context;
	/**应用上下文**/
	public static ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
	/**dao操作对象**/		
	public static BaseDAOImpl dao = (BaseDAOImpl)ctx.getBean("dao");
	/**河南气象站点数据*/
	//public static List<HashMap<String, String>> stationList = StationUtil.readStations();
	public static List<JSONObject> stationList = StationUtil.readAllStations();//StationUtil.getAllStation();
	
	/**
	 * 应用程序停止
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		String webName = event.getServletContext().getContextPath();
		webName = webName.substring(1, webName.length());
		log.info("WEB应用程序[" + webName + "]停止  时间：" + DateUtils.date2String("yyyy-MM-dd HH:mm:ss", new Date()));
	}
	/**
	 * 应用程序启动
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		String webName = event.getServletContext().getContextPath();
		webName = webName.substring(1, webName.length());
		log.info("WEB应用程序[" + webName + "]启动  时间：" + DateUtils.date2String("yyyy-MM-dd HH:mm:ss", new Date()));
		context = event.getServletContext();
		rootPath = context.getRealPath("/");
		rootPath = rootPath.replace("\\", "/");
		log.info("WEB应用程序根目录：" + rootPath);
		webconfigFilePath = rootPath + "WEB-INF/resources/xml/webconfig.xml";
		sqlconfigFilePath = rootPath + "WEB-INF/resources/xml/SQLConfig.xml";
		if(ctx==null){
			ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		}else{
			if(dao==null){
				dao = (BaseDAOImpl)ctx.getBean("dao");
			}
		}
		//设置环境变量
		Properties properties = new Properties();
		properties.setProperty("catalina.base", rootPath);
		//启动定时任务
		TaskJobAction action = new TaskJobAction();
		//action.startTask();
	}
}

