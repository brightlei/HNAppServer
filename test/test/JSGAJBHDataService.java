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
package test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.spi.orb.StringPair;

/**
 * 江苏公安进博会数据应用服务
 * @author 雷志强
 * @version 1.0
 */
public class JSGAJBHDataService {
	/**
	 * API接口返回数据
	 */
	private static JSONObject api_data = new JSONObject();
	
	public static void initJson(){
		api_data = new JSONObject();
		api_data.put("code", 0);
		api_data.put("msg", "");
	}
	
	public static void main(String[] args) {
		//testSsyssjgxJson();
		//testSssjzxJson();
		//testCsjsjzygxJson();
		//testSjtszgJson();
		//testSjsclbJson();
		testXttysjfwqkJson();
		//testJbhdzsjfwJson();
		System.out.println(api_data);
		System.out.println("OK");
	}
	/**
	 * 三省一市数据共享JSON数据.<br>
	 * @return void
	 */
	public static void testSsyssjgxJson(){
		initJson();
		JSONArray data = new JSONArray();
		String[] xAxis = new String[]{"江苏","浙江","上海","安徽"};
		Long[] values = new Long[]{3431800000L,930985000L,296560000L,462200000L};
		for(int i=0;i<xAxis.length;i++){
			JSONObject json = new JSONObject();
			json.put("name", xAxis[i]);
			json.put("count", values[i]);
			data.add(json);
		}
		api_data.put("data", data);
	}
	
	/**
	 * 实时数据在线JSON数据
	 */
	public static void testSssjzxJson(){
		System.out.println("实时数据在线JSON数据处理");
		initJson();
		JSONArray data = new JSONArray();
		String filepath = "G:/88.项目资料/43.江苏省项目/03.进博会大屏/对接数据/省厅大屏/实时数据在线.txt";
		List<String> datalist = null;
		try {
			datalist = FileUtils.readLines(new File(filepath));
			String fieldstring = datalist.get(0).replaceAll("\"", "");
			System.out.println(fieldstring);
			String[] fieldArray = fieldstring.split("[,]");
			System.out.println(fieldstring);
			String rowstring = null;
			String[] valueArray = null;
			for(int i=1;i<datalist.size();i++){
				rowstring = datalist.get(i).replaceAll("\"", "");
				System.out.println(rowstring);
				valueArray = rowstring.split("[,]");
				JSONObject record = new JSONObject();
				for(int k=0;k<fieldArray.length;k++){
					record.put(fieldArray[k].toLowerCase(), valueArray[k]);
				}
				data.add(record);
			}
			api_data.put("data", data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 长三角数据资源共享JSON数据.<br>
	 * @return void
	 */
	public static void testCsjsjzygxJson(){
		System.out.println("长三角数据资源共享JSON数据处理");
		initJson();
		JSONObject data = new JSONObject();
		String[] yAxis = new String[]{"江苏","上海","浙江","安徽"};
		//统计类型
		String[] xAxis = new String[]{"常住人口","暂住人口","出入境","吸贩毒人员",
				"嫌疑人","上访人员","精神病","高危人员","六合一数据","涉恐人员","车辆识别",
				"被盗抢机动车","涉案事件","视频监控","旅馆住宿","铁路售票","民航售票","驾驶员","人员、车辆","违法犯罪"};
		data.put("xAxis", xAxis);
		data.put("yAxis", yAxis);
		JSONArray serieArray = new JSONArray();
		String filepath = "G:/88.项目资料/43.江苏省项目/03.进博会大屏/对接数据/省厅大屏/长三角数据资源共享.csv";
		List<String> datalist = null;
		try {
			datalist = FileUtils.readLines(new File(filepath));
			JSONObject serie = new JSONObject();
			JSONArray serieData = null;
			long value = 0L;
			for(int i=0;i<yAxis.length;i++){
				serie = new JSONObject();
				serie.put("name", yAxis[i]);
				serieData = new JSONArray();
				for(int k=0;k<xAxis.length;k++){
					value = getSeriesValue(yAxis[i], xAxis[k], datalist);
					serieData.add(value);
				}
				serie.put("data", serieData);
				serieArray.add(serie);
			}
			data.put("series", serieArray);
			api_data.put("data", data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 匹配城市和统计类型对应的数据.<br>
	 * @param yAxis 城市名称
	 * @param xAxis 统计类型
	 * @param rowList 数据库中的数据
	 * @return long
	 */
	private static long getSeriesValue(String yAxis,String xAxis,List<String> rowList){
		long data = 0L;
		String rowString = null;
		String[] valueArray = null;
		for(int i=1;i<rowList.size();i++){
			rowString = rowList.get(i).replaceAll("\"", "");
			valueArray = rowString.split("[,]");
			System.out.println(yAxis+"|"+xAxis+"|"+"|"+rowString);
			if(yAxis.equals(valueArray[0]) && xAxis.equals(valueArray[1])){
				System.out.println(valueArray[2]);
				data = formatNumberString(valueArray[2]);
			}
		}
		return data;
	}
	/**
	 * 将带汉字的字符处理成格数值.<br>
	 * @param str 数值字符串
	 * @return long
	 */
	private static long formatNumberString(String str){
		long value = 0L;
		if(str.contains("亿")){
			int index = str.indexOf("亿");
			String string1 = str.substring(0,str.indexOf("亿"));
			value += Double.parseDouble(string1)*100000000;
			str = str.substring(index+1,str.length());
		}
		if(str.contains("万")){
			int index = str.indexOf("万");
			String string1 = str.substring(0,index);
			value += Double.parseDouble(string1)*10000;
		}else{
			value += Double.parseDouble(str);
		}
		return value;
	}
	
	/**
	 * 华东数据共享资源JSON数据.<br>
	 * @return void
	 */
	public static void testHdsjgxzy(){
		initJson();
		api_data.put("data", "aaaaa");
	}
	
	/**
	 * 长三角数据服务运行状态JSON数据.<br>
	 * @return void
	 */
	public static void testCsjsjfwyxztJson(){
		initJson();
		api_data.put("data", "aaaaa");
	}
	/**
	 * 进博会定制数据服务JSON数据.<br>
	 * @return void
	 */
	public static void testJbhdzsjfwJson(){
		initJson();
		String[] keys = new String[]{"客运汽车购票-发车","当日客运汽车购票","高速公路收费站车辆入口","高速公路收费站车辆出口",
				"七类重点人员","涉毒人员（有车）基本信息","吸毒人员（有车）基本信息","涉毒、吸毒人员机动车基本信息",
				"涉毒、吸毒人员照片信息","铁路旅客实名订购票信息","七类重点人员（从业人员）档案信息","被盗窃车牌案件关联",
				"网约车","新疆车牌","手机WIFI身份信息"};
		String[] citys = new String[]{"苏州","南通","启东","海门","昆山","太仓"};
		JSONArray data = new JSONArray();
		JSONObject json = null;
		JSONArray citydata = null;
		String filepath = "G:/88.项目资料/43.江苏省项目/03.进博会大屏/对接数据/进博会定制数据服务.txt";
		List<String> datalist = null;
		try {
			datalist = FileUtils.readLines(new File(filepath));
			System.out.println(datalist);
			long value = 0L;
			for(int i=0;i<citys.length;i++){
				json = new JSONObject();
				json.put("name", citys[i]);
				citydata = new JSONArray();
				for(int k=0;k<keys.length;k++){
					JSONObject child = new JSONObject();
					child.put("name", keys[k]);
					value = getSeriesValue(citys[i], keys[k], datalist);
					System.out.println(value);
					child.put("num", value);
					citydata.add(child);
				}
				json.put("data", citydata);
				data.add(json);
			}
			api_data.put("data", data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 前方资源调用量JSON数据.<br>
	 * @return void
	 */
	public static void testQfzydylJson(){
		
	}
	/**
	 * 数据推送战果JSON数据.<br>
	 * @return void
	 */
	public static void testSjtszgJson(){
		System.out.println("数据推送战果JSON数据");
		initJson();
		JSONArray data = new JSONArray();
		String filepath = "G:/88.项目资料/43.江苏省项目/03.进博会大屏/对接数据/在逃人员.txt";
		List<String> datalist = null;
		try {
			datalist = FileUtils.readLines(new File(filepath));
			String fieldstring = datalist.get(0).replaceAll("\"", "");
			System.out.println(fieldstring);
			String[] fieldArray = fieldstring.split("[,]");
			System.out.println(fieldstring);
			String rowstring = null;
			String[] valueArray = null;
			for(int i=1;i<datalist.size();i++){
				rowstring = datalist.get(i).replaceAll("\"", "");
				System.out.println(rowstring);
				valueArray = rowstring.split("[,]");
				JSONObject record = new JSONObject();
				for(int k=0;k<fieldArray.length;k++){
					record.put(fieldArray[k].toLowerCase(), valueArray[k]);
				}
				data.add(record);
			}
			api_data.put("data", data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 数据上传列表JSON数据.<br>
	 * @return void
	 */
	public static void testSjsclbJson(){
		System.out.println("数据上传列表JSON数据");
		initJson();
		JSONArray data = new JSONArray();
		String filepath = "G:/88.项目资料/43.江苏省项目/03.进博会大屏/对接数据/数据上传列表.txt";
		List<String> datalist = null;
		try {
			datalist = FileUtils.readLines(new File(filepath));
			String fieldstring = datalist.get(0).replaceAll("\"", "");
			System.out.println(fieldstring);
			String[] fieldArray = fieldstring.split("[,]");
			System.out.println(fieldstring);
			String rowstring = null;
			String[] valueArray = null;
			for(int i=1;i<datalist.size();i++){
				rowstring = datalist.get(i).replaceAll("\"", "");
				System.out.println(rowstring);
				valueArray = rowstring.split("[,]");
				JSONObject record = new JSONObject();
				for(int k=0;k<fieldArray.length;k++){
					record.put(fieldArray[k].toLowerCase(), valueArray[k]);
				}
				data.add(record);
			}
			api_data.put("data", data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 系统调用数据服务情况.<br>
	 * @return void
	 */
	public static void testXttysjfwqkJson(){
		System.out.println("系统调用数据服务情况JSON数据");
		initJson();
		JSONArray data = new JSONArray();
		String filepath = "G:/88.项目资料/43.江苏省项目/03.进博会大屏/对接数据/服务接口数据2.txt";
		List<String> datalist = null;
		try {
			datalist = FileUtils.readLines(new File(filepath));
			String fieldstring = datalist.get(0).replaceAll("\"", "");
			System.out.println(fieldstring);
			String[] fieldArray = fieldstring.split("[,]");
			System.out.println(fieldstring);
			String rowstring = null;
			String[] valueArray = null;
			for(int i=1;i<datalist.size();i++){
				rowstring = datalist.get(i).replaceAll("\"", "");
				System.out.println(rowstring);
				valueArray = rowstring.split("[,]");
				JSONObject record = new JSONObject();
				for(int k=0;k<fieldArray.length;k++){
					if(valueArray.length==fieldArray.length){
						record.put(fieldArray[k].toLowerCase(), valueArray[k]);
					}else{
						record.put(fieldArray[k].toLowerCase(), "");
					}
				}
				data.add(record);
			}
			api_data.put("data", data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
