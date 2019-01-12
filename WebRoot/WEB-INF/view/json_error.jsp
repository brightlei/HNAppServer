<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@ page language="java" isErrorPage="true" import="java.util.*" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

response.setStatus(HttpServletResponse.SC_OK);
//定义json返回对象
JSONObject json = new JSONObject();
json.put("code", 500);
//接口地址
String apiurl = request.getAttribute("javax.servlet.forward.request_uri").toString();
//异常类名称
String className = exception.getClass().toString();
String exInfo = exception.getMessage();
String msg = "服务地址："+apiurl+"，异常信息："+className+"："+exInfo;
out.clear();
json.put("msg", msg);
out.println(json.toJSONString());
%>