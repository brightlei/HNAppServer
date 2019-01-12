package com.zondy.web.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import com.opensymphony.xwork2.ActionSupport;
import com.zondy.util.JwtToken;

@SuppressWarnings("serial")
public class BaseAction extends ActionSupport implements SessionAware,
		ServletRequestAware, ServletResponseAware {
	private static Logger log = Logger.getLogger(BaseAction.class);
	/**
	 * 当需要返回JSON格式数据时，将需要返回到前台的数据加入到dataMap中，
	 * 应用场景： 后台JsonAction中 dataMap.put("data",需要往前台发送的数据)
	 * 则前台获取时  function(data,status) {alert(data.data);//data.data就是后台放入的数据}
	 */
	public Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
	/**
	 * session对象集合
	 */
	public Map<String, Object> map = new LinkedHashMap<String,Object>();
	public HttpServletRequest request;
	public HttpServletResponse response;
	/**
	 * 请求参数json对象
	 */
	public JSONObject requestParam = new JSONObject();
	public String requestSessionId = "";
	
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		requestSessionId = request.getSession().getId();
		requestSessionId = "";
		String requestMethod = request.getMethod();
		//将request中的请求参数转换成json对象
		this.requestParam = paramToJson(request.getParameterMap());
		if(requestMethod.toLowerCase().equals("post")){
			JSONObject postparam = getParam();
			requestParam.putAll(postparam);
		}
		String token = request.getHeader("token");
		log.info("token="+token);
		if(token != null){
			Map<String, Claim> claims = JwtToken.verifyToken(token);
			if(claims != null){
				String username = claims.get("username").asString();
				log.info("login_username="+username);
				if(username != null && !username.equals("")){
					this.requestParam.put("username", username);
				}
			}
		}
		//请求的URL地址
		String uri = request.getRequestURI();
		log.info("["+requestSessionId+"]getRequestURI="+uri);
		log.info("["+requestSessionId+"]requestParam="+requestParam);
		this.dataMap.put("code",0);
		this.dataMap.put("msg","");
		this.dataMap.put("data", "");
	}
	
	public boolean checkParamNull(String param,int errcode,String errmsg){
		boolean isNull = false;
		if(param == null || param.equals("")){
			this.dataMap.put("code", errcode);
			this.dataMap.put("msg", errmsg);
			isNull = true;
		}
		return isNull;
	}
	
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}

	public void setSession(Map<String, Object> map) {
		this.map=map;
	}


	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject paramToJson(Map<String, String[]> pMap){
		JSONObject paraJson = new JSONObject();
		Set<?> set = pMap.entrySet();
		Iterator<?> iterator =  set.iterator();
		Entry<String, Object> entry = null;
		while(iterator.hasNext()){
			entry = (Entry<String, Object>)iterator.next();
			String key = entry.getKey().toString();
			String[] value = (String[])entry.getValue();
			paraJson.put(key, value[0]);
		}
		return paraJson;
	}
	
	protected JSONObject getParam() {
		// System.out.println("接收参数");
		JSONObject jsonObject = new JSONObject();// 用来接收移动端发送的请求
		InputStream input = null;// 用流来接收请求参数
		BufferedReader br = null;// 使用缓冲区提高效率
		String param = "";// 接收请求
		try {
			input = request.getInputStream();// 获取输入流
			// 用缓冲去包装输入流提高效率以及设置编码，否则客户端发送中文乱码
			br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			param = br.readLine();// 从输入流中接收参数
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(param!=null && !param.equals("")){
			// 将String类型请求参数转换成json类型，方便解析
			jsonObject = JSONObject.parseObject(param);
		}
		return jsonObject;
	}
}