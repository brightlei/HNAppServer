package com.zondy.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import com.zondy.util.JwtToken;

public class PageFilter extends HttpServlet implements Filter {

	private static final long serialVersionUID = 1L;
	private FilterConfig filterConfig = null;
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String excludePage = this.filterConfig.getInitParameter("exclude");
		String[] pages = excludePage.split(";");
		HttpServletRequest req=(HttpServletRequest)request;
		HttpServletResponse res=(HttpServletResponse)response;
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		//获取当前请求地址
		String currentUrl = req.getRequestURI();
		if(isMatch(pages, currentUrl)){
			chain.doFilter(request, response);
		}else{
			String token = req.getHeader("token");
			JSONObject json = new JSONObject();
			if(token==null){
				json.put("code", 201);
				json.put("msg", "缺少token参数，无法执行请求！");
				res.getWriter().println(json.toJSONString());
			}else if(token.equals("")){
				json.put("code", 202);
				json.put("msg", "token参数值为空，无法执行请求！");
				res.getWriter().println(json.toJSONString());
			}else{
				Map<String, Claim> jwt = JwtToken.verifyToken(token);
				if(jwt==null){
					json.put("code", 203);
					json.put("msg", "token已过期，请重新登录！");
					res.getWriter().println(json.toJSONString());
				}else{
					String username = jwt.get("username").asString();
					Map<String, Object> extraParams = new HashMap<String, Object>();
			        extraParams.put("username", username);
					//利用原始的request对象创建自己扩展的request对象并添加自定义参数
			        RequestParameterWrapper requestParameterWrapper = new RequestParameterWrapper(req);
			        requestParameterWrapper.addParameters(extraParams);
					chain.doFilter(requestParameterWrapper, response);
				}
			}
		}
	}
	public void init(FilterConfig config) throws ServletException {
		this.filterConfig = config;
	}
	/**
	 * 判断当前访问页面是不是放行页面
	 * @param pagelist 放行页面字符串
	 * @param currentPage 当前页面
	 * @return
	 */
	public boolean isMatch(String[] pagelist,String currentPage){
		boolean isMatch = false;
		int count = pagelist.length;
		for(int i=0;i<count;i++){
			if(currentPage.contains(pagelist[i])){
				isMatch = true;
				break;
			}
		}
		return isMatch;
	}
}
