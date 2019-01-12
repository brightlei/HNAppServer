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
package org.hibernate.transform;

/**
 * 自定义hibernate查询返回结果.
 * 覆盖原来hibernate3.jar里面的类.
 * @author 雷志强
 * @version 1.0
 */
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSONObject;

public class AliasToEntityMapResultTransformer extends BasicTransformerAdapter
		implements Serializable {

	/**
	 * long:serialVersionUID 用一句话描述这个变量
	 */
	private static final long serialVersionUID = 1L;

	public AliasToEntityMapResultTransformer() {
		
	}

	public JSONObject transformTuple(Object tuple[], String aliases[]) {
		JSONObject result = new JSONObject();
		String alias = null;
		Object obj = null;
		String value = "";
		for (int i = 0; i < tuple.length; i++) {
			alias = aliases[i];
			obj = tuple[i];
			if(obj != null){
				if(obj instanceof Clob){
					value = clobToString((Clob)obj);
				}else if (obj instanceof Blob) {
					value = convertBlobToBase64String((Blob)obj);
				}else{
					value = obj.toString();
				}
			}else{
				value = "";
			}
			if (alias != null){
				result.put(alias.toLowerCase(), value);
			}
		}
		return result;
	}

	private Object readResolve() {
		return INSTANCE;
	}

	public boolean equals(Object other) {
		return other != null
				&& (org.hibernate.transform.AliasToEntityMapResultTransformer.class)
						.isInstance(other);
	}

	public int hashCode() {
		return getClass().getName().hashCode();
	}

	public static final AliasToEntityMapResultTransformer INSTANCE = new AliasToEntityMapResultTransformer();
	
	/**
	 * 将Clob字段转换成字符串.<br>
	 * @param clob 字段
	 * @return String 转换后的字符串
	 */
	public String clobToString(Clob clob){
		String reString = "";
		try {
			Reader is = clob.getCharacterStream();//得到流
			BufferedReader br = new BufferedReader(is);
			String s = br.readLine();
			StringBuffer sb = new StringBuffer();
			while (s != null) {//执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
				sb.append(s);
				s = br.readLine();
			}
			reString = sb.toString();
		} catch (Exception e) {
			System.err.println("Exception:"+e.getMessage());
		}
		return reString;
	}
	
	/**
	 * 将Blob字段转Base64
	 * @param blob
	 * @return String base64编码字符串
	 */
	public String convertBlobToBase64String(Blob blob) {
		String result = "";
		if (null != blob) {
			try {
				InputStream msgContent = blob.getBinaryStream();
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				byte[] buffer = new byte[100];
				int n = 0;
				while (-1 != (n = msgContent.read(buffer))) {
					output.write(buffer, 0, n);
				}
				result = new BASE64Encoder().encode(output.toByteArray());
				output.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		} else {
			return null;
		}
	}
}