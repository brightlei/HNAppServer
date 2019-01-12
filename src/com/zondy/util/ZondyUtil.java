package com.zondy.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zondy.config.XmlConfig;
import com.zondy.listener.ApplicationListener;

/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class ZondyUtil {
	/***
	 * 中国电信号段 133、149、153、173、177、180、181、189、199
		中国联通号段 130、131、132、145、155、156、166、175、176、185、186
		中国移动号段 134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188、198
		其他号段
		14号段以前为上网卡专属号段，如中国联通的是145，中国移动的是147等等。
		虚拟运营商
		电信：1700、1701、1702
		移动：1703、1705、1706
		联通：1704、1707、1708、1709、171
		卫星通信：1349
	 * 验证是否为手机号.<br>
	 * @param phone
	 * @return boolean
	 */
	public static boolean isPhone(String phone) {
		XmlConfig webxml = new XmlConfig(ApplicationListener.webconfigFilePath);
		String phoneReg = webxml.getConfigValue("phoneReg");
	    String regex = phoneReg;
	    if (phone.length() != 11) {
	        System.out.println("手机号应为11位数");
	        return false;
	    } else {
	        Pattern p = Pattern.compile(regex);
	        Matcher m = p.matcher(phone);
	        boolean isMatch = m.matches();
	        if (!isMatch) {
	        	System.out.println("请填入正确的手机号");
	        }
	        return isMatch;
	    }
	}
	/**
	 * 获取sql语句中未传入的参数.<br>
	 * @param sql SQl语句
	 * @return List<String> 未传入的参数数组
	 */
	public static List<String> getSqlParam(String sql){
		int length = 0;
		if(sql!=null){
			length = sql.length();
		}
		List<String> list = new ArrayList<String>();
		List<Integer> indexList = new ArrayList<Integer>();
		for(int i=0;i<length;i++){
			if(sql.charAt(i)=='#'){
				indexList.add(i);
			}
		}
		String param = "";
		int firstIndex = 0;
		int nextIndex = 0;
		for(int i=0;i<indexList.size()-1;i=i+2){
			firstIndex = indexList.get(i);
			nextIndex = indexList.get(i+1);
			param = sql.substring(firstIndex+1, nextIndex);
			list.add(param);
		}
		return list;
	}
	
	public static void main(String[] args) {
		System.out.println(isPhone("15926369822"));
		System.out.println(isPhone("159263698222"));
		System.out.println(isPhone("159263q69822"));
		System.out.println(isPhone("159263qe69822"));
		System.out.println(isPhone("159ab369822"));
	}
}
