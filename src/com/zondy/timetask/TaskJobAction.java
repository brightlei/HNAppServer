package com.zondy.timetask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.zondy.listener.ApplicationListener;
import com.zondy.util.Dom4jUtils;

/**
 * 模块名称：TaskJobAction									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2015-1-20 下午05:15:09					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2015-1-20 下午05:15:09					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class TaskJobAction extends BaseJob{
	
	//private static Logger log = Logger.getLogger(TaskJobAction.class.getSimpleName());
	
	@SuppressWarnings("unchecked")
	public List<HashMap<String, String>> getTaskList(){
		List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		String rootPath = ApplicationListener.rootPath;
		String taskConfigPath = rootPath+"WEB-INF/resources/xml/taskConfig.xml";
		File file = new File(taskConfigPath);
		if(file.exists()){
			try {
				Document doc = Dom4jUtils.readXml(file);
				Element rootElement = doc.getRootElement();
				list = rootElement.elements();
			} catch (Exception e) {
				log(e,"getTaskList[Exception]读取定时任务配置文件异常！");
				//log.error("getTaskList[Exception] - ", new Throwable(e));
			}
		}else{
			log("getTaskList[message] - 定时任务配置文件不存在["+taskConfigPath+"]");
		}
		return list;
	}
	
	public void startTask(){
		List<HashMap<String, String>> list = getTaskList();
		int listCount = list.size();
		Element element = null;
		String jobName = "";
		String job = "";
		String trigger = "";
		HashMap<String, String> nodeMap = null;
		for(int i=0;i<listCount;i++){
			element = (Element)list.get(i);
			nodeMap = Dom4jUtils.getNodeAttr(element);
			jobName = nodeMap.get("taskName");
			job = nodeMap.get("taskClass");
			trigger = nodeMap.get("taskCronTrigger");
			log("定时任务名称:【"+jobName+"】");
			if(nodeMap.get("isExecute")!=null&&nodeMap.get("isExecute").equals("true")){
				log("【"+jobName+"】----启动成功----");
				logLine();
				QuartzManager.addJob(jobName, job, trigger,nodeMap);
			}
		}
	}
	
	public static void main(String[] args) {
		TaskJobAction action = new TaskJobAction();
		action.startTask();
	}
}
