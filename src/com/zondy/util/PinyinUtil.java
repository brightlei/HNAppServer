package com.zondy.util;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
/**
 * 模块名称：该模块名称								<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2015-10-7 下午02:52:35					<br>
 * 初始版本：V1.0										<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2015-10-7 下午02:52:35					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class PinyinUtil {
	// 将汉字转换为全拼
	public static String getPingYin(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		for (int i = 0; i < t0; i++) {
			// 判断是否为汉字字符
			if (java.lang.Character.toString(t1[i]).matches(
					"[\\u4E00-\\u9FA5]+")) {
				t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
				t4 += t2[0];
			} else
				t4 += java.lang.Character.toString(t1[i]);
		}
		return t4;
	}

	// 返回中文的首字母
	public static String getPinYinHeadChar(String str) {
		String convert = "";
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			PinyinHelper.toHanyuPinyinString(str, t3, "");
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word,t3);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert;
	}

	// 将字符串转移为ASCII码
	public static String getCnASCII(String cnStr) {
		StringBuffer strBuf = new StringBuffer();
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}

	public static void main(String[] args) {
		System.out.println(getPinYinHeadChar("高寨蔬菜批发市场"));
		System.out.println(getPinYinHeadChar("岗坡农贸市场"));
		System.out.println(getPinYinHeadChar("百顺农贸市场"));
		System.out.println(getPinYinHeadChar("纬四路农贸市场"));
		System.out.println(getPinYinHeadChar("桐柏中路农贸市场"));
		//System.out.println(getCnASCII("綦江县"));
	}
}
