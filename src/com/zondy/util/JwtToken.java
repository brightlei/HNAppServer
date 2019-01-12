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
/**
 * 这里是文件说明
 */
package com.zondy.util;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sun.security.ntlm.Client;
import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;

/**
 * 模块名称：该模块名称
 * 功能描述：该文件详细功能描述
 * 文档作者：雷志强
 * 创建时间：2018年1月22日 下午5:58:40
 * 初始版本：V1.0
 * 修改记录：
 * *************************************************
 * 修改人：雷志强
 * 修改时间：2018年1月22日 下午5:58:40
 * 修改内容：
 * *************************************************
 */
public class JwtToken {
	
	private static Logger log = Logger.getLogger(JwtToken.class);
	/**
	 * 公共密钥，保存到服务端，客户端是不会知道的，以防被攻击
	 */
	public static String SECRET = "ZondyForHeNanApp";
	
	/**
	 * 功能描述：请用一句话描述这个方法实现的功能
	 * 创建作者：雷志强
	 * 创建时间：2018年1月22日 下午6:17:26
	 * @return
	 * @throws IllegalArgumentException
	 * @throws JWTCreationException
	 * @throws UnsupportedEncodingException
	 * @return String
	 */
	public static String createToken(String username,int minute){
		String token = null;
		//签发时间
		Date iatDate = new Date();
		//过期时间设置
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(Calendar.MINUTE, minute);
		Date expiresDate = nowTime.getTime();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");
		try {
			token = JWT.create()
					.withHeader(map)//header
					.withClaim("username", username)//payload
					//.withExpiresAt(expiresDate)//设置过期时间-过期时间要大于签发时间
					.withIssuedAt(iatDate)//签发时间
					.sign(Algorithm.HMAC256(SECRET));
		} catch (IllegalArgumentException e) {
			log.error("IllegalArgumentException", new Throwable(e));
			//e.printStackTrace();
		} catch (JWTCreationException e) {
			log.error("JWTCreationException", new Throwable(e));
			//e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			log.error("UnsupportedEncodingException", new Throwable(e));
			//e.printStackTrace();
		}//加密
		return token;
	}
	
	public static Map<String,Claim> verifyToken(String token){
		Map<String, Claim> claims = null;
		JWTVerifier verifier;
		try {
			verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
			DecodedJWT jwt = verifier.verify(token);
			if(jwt!=null){
				claims = jwt.getClaims();
			}
		} catch (Exception e) {
			log.error("Exception", new Throwable(e));
		}
		return claims;
	}
	
	/**
	 * 功能描述：请用一句话描述这个方法实现的功能
	 * 创建作者：雷志强
	 * 创建时间：2018年1月22日 下午5:58:40
	 * @param args
	 * @return void
	 */
	public static void main(String[] args) {
		long st = System.currentTimeMillis();
		//testCreateToken();
		testVerifyToken();
		long et = System.currentTimeMillis();
		System.out.println("exetime="+(et-st)+"ms");
		//testVerifyToken();
		System.out.println("OK");
	}
	
	public static void testCreateToken(){
		long st = System.currentTimeMillis();
		try {
			String token = JwtToken.createToken("admin",25);
			String fresh_token = JwtToken.createToken("admin", 26);
			System.out.println(token);
			Map<String, Claim> claims = JwtToken.verifyToken(token);
			System.out.println(claims);
			System.out.println(claims.get("username").asString());
		} catch (IllegalArgumentException e) {
			//e.printStackTrace();
		} catch (JWTCreationException e) {
			//e.printStackTrace();
		}
		long et = System.currentTimeMillis();
		System.out.println("[testCreateToken]exetime="+(et-st)+"ms");
	}
	
	public static void testVerifyToken(){
		long st = System.currentTimeMillis();
		try {
			String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTY2MTY2NzUsIm5hbWUiOiJhYWFhIiwiYWFhYSI6MjMsInZ2dnYiOiJzYWRhcyIsImlhdCI6MTUxNjYxNjYxNX0.RX0gDnhfxvvjog3aVtYp0-TZc52WzMrhH9JmseECLqo";
			token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1MTY2Mjk2MjgsIm5hbWUiOiAAAAAAAJhYWFhIiwiYWFhYSI6MjMsInZ2dnYiOiJzYWRhcyIsImlhdCI6MTUxNjYyOTMyN30.t3ntPWAKaoGifEI7lxS13M9ZUxa1A-8_9uI9jv_VpEA";
			token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiYWFhYSIsImFhYWEiOjIzLCJ2dnZ2Ijoic2FkYXMifQ.6sErJbA9KoAjrCf4J15VuYjhfdwFO-lrIGpqelzO0zw";
			token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2dnZ2Ijoic2FkYXMiLCJuYW1lIjoiYWFhYSIsImFhYWEiOjIzfQ.jdLyDhmgo4nduzgeczEL1f8Dc2hQWCMY96xbMlR2sT4";
			token = "22eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1Mzc4OTczMjMsImlhdCI6MTUzNzI5MjUyMywidXNlcm5hbWUiOiJhZG1pbiJ9.cv8weHerPgYhKjeFLJc8i7f5ThBniMcZFxdJXx33ToI";
			token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NDAyMzA4NDgsImlhdCI6MTUzOTYyNjA0OCwidXNlcm5hbWUiOiIxNTkyNjM2OTgyMyJ9.Q9mFGLC76IVDR8_LaDqdxiDfCrh3Ov_e9wWKHqQEz68";
			token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NDQwODYyMDgsImlhdCI6MTU0MzQ4MTQwOCwidXNlcm5hbWUiOiIxNTkyNjM2OTgyMyJ9.Qa9Ud3V6g77S8S2CDCDHMiOmzHoS8kM25gx3bzDcjFE";
			token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NDQ4OTI3NjUsImlhdCI6MTU0NDI4Nzk2NSwidXNlcm5hbWUiOiIxNTkyNjM2OTgyMyJ9.aSEf6Vn6heFl0_uiT_c9raThS9MNoS8JUd3N5TycMr0";
			Map<String, Claim> claims = JwtToken.verifyToken(token);
			System.out.println(claims);
			System.out.println(claims.get("username").asString());
			System.out.println(claims.get("exp").asString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (JWTCreationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long et = System.currentTimeMillis();
		System.out.println("[testCreateToken]exetime="+(et-st)+"ms");
	}
}
