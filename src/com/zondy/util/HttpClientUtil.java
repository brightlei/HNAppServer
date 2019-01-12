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
package com.zondy.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 该文件详细功能描述
 * @author 雷志强  2019年1月12日 下午2:51:02
 * @version 1.0
 */
public class HttpClientUtil {
	
	
	public static String pickData(String pageurl){
		// 定义HTTP请求客户端
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 定义get请求
		HttpGet getRequest = new HttpGet(pageurl);
		getRequest.addHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
		// 定义response响应对象
		try {
			CloseableHttpResponse response = httpClient.execute(getRequest);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				// 打印响应实体
				if(entity != null){
					return EntityUtils.toString(entity);
				}
			} finally{
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		String html = pickData("https://www.tianqi.com/fengqiu/");
		try {
			FileUtils.write(new File("c:/fengqiu.html"), html);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
}
