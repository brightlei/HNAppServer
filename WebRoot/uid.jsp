<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
    <base href="<%=basePath%>">
    <title>获取手机信息</title>
	<meta name="content-type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
    <%
    String userAgent = request.getHeader("User-Agent"); 
    String fromtype = ""; 
    if (userAgent!=null && !userAgent.isEmpty()) 
    { 
        if (-1 != userAgent.indexOf("iPhone")) 
        { 
            fromtype = "iPhone"; 
        } 
        else 
        { 
            if (-1 != userAgent.indexOf("zh-cn;")) 
            { 
                // "Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; C6602 Build/10.4.B.0.569) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 V1_AND_SQ_4.7.2_134_YYB_D " 
                fromtype = userAgent.substring(userAgent.indexOf("zh-cn;") + 7, userAgent.indexOf("Build")); 
            } 
            else if (-1 != userAgent.indexOf("en-us;")) 
            { 
                fromtype = userAgent.substring(userAgent.indexOf("en-us;") + 7, userAgent.indexOf("Build")); 
            } 
            else if ("".equals(fromtype)) 
            { 
                fromtype = userAgent.substring(0, userAgent.indexOf("/")); 
            } 
        }
    }
    out.println("操作系统："+fromtype);
    %>
  </body>
</html>
