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

package com.zondy.agricultural;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;

import org.apache.log4j.Logger;

/**
 * 模块名称：GrdUtils									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2018-1-18 上午11:37:48					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2018-1-18 上午11:37:48					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class GrdUtils {
	
	private static Logger log = Logger.getLogger(GrdUtils.class);
	
	public static GrdBean loadGrdFile(String filepath) throws IOException{
		GrdBean grdBean = new GrdBean();
		RandomAccessFile rdfile = new RandomAccessFile(new File(filepath), "r");
    	FileChannel fc = rdfile.getChannel();
    	MappedByteBuffer br = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
    	log.debug("文件大小："+fc.size());
    	br.order(ByteOrder.LITTLE_ENDIAN);
    	br.position(0);
    	//文件标识
    	String flag = readChars(br, 4);
    	log.debug("文件标识："+flag);
    	//数据列数
    	int col = br.getShort();
    	//数据行数
    	int row = br.getShort();
    	log.debug("网格大小："+row+"行，"+col+"列");
    	double xmin = br.getDouble();//X最小值
    	double xmax = br.getDouble();//X最大值
    	double ymin = br.getDouble();//Y最小值
    	double ymax = br.getDouble();//Y最大值
    	double zmin = br.getDouble();//Z最小值
    	double zmax = br.getDouble();//Z最大值
    	log.debug("坐标范围："+xmin+","+ymin+","+xmax+","+ymax);
    	log.debug("数值范围："+zmin+","+zmax);
    	double[][] data = new double[row][col];
    	double dx = xmax - xmin;
    	double dy = ymax - ymin;
    	double xstep = dx/(col-1);
    	double ystep = dy/(row-1);
    	for(int r=0;r<row;r++){
    		for(int c=0;c<col;c++){
    			data[r][c] = br.getFloat();
    		}
    	}
    	fc.close();
    	rdfile.close();
    	grdBean = new GrdBean(row,col,xmin, ymin, xmax, ymax, zmin, zmax, data);
    	grdBean.setXstep(xstep);
    	grdBean.setYstep(ystep);
    	return grdBean;
	}
	
	//读取短字节
	public static short getShort(byte buf1, byte buf2) 
    {
        short r = 0;
        r |= (buf1 & 0x00ff);
        r <<= 8;
        r |= (buf2 & 0x00ff);
        return r;
    }
	
	// 读取字符串
	public static String readChars(ByteBuffer br, int btsize) {
		byte[] buf = new byte[btsize];
		br.get(buf);
		try {
			return new String(buf, "GBK");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	/**
	 * 根据经纬度获取对应的格点值.<br>
	 * @param grdBean 格点对象
	 * @param x 经度
	 * @param y 纬度
	 * @return double 格点值
	 */
	public static double getGrdValue(GrdBean grdBean,double x,double y){
		double v = 0;
		int row = grdBean.getRow();
		int col = grdBean.getCol();
		//获取格点左下角坐标
		double xmin = grdBean.getXmin();
		double ymin = grdBean.getYmin();
		double xstep = grdBean.getXstep();
		double ystep = grdBean.getYstep();
		double[][] data = grdBean.getData();
		double dx = x - xmin;
		double dy = y - ymin;
		//log.info("dx="+dx+",dy="+dy);
		DecimalFormat df = new DecimalFormat("#");
		//计算该坐标点在哪一列
		int xindex = Integer.parseInt(df.format(dx/xstep));
		//计算该坐标点在哪一行
		int yindex = Integer.parseInt(df.format(dy/ystep));
		log.debug("xindex="+xindex+",yindex="+yindex);
		if(xindex>=0 && xindex<col && yindex>=0 && yindex<row){
			v = data[yindex][xindex];
		}else{
			log.warn("该坐标点不在格点范围内，无法获取格点数值！");
		}
		return v;
	}
	
	/**
	 * 功能描述：请用一句话描述这个方法实现的功能<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：2018-1-18 上午11:37:48<br>
	 * @param args
	 * @return void
	 */
	public static void main(String[] args) {
		String filepath = "G:/88.项目资料/05.河南农业气象移动平台/业务数据/GridData/SmForecast/fcode_test.grd";
		filepath = "G:/88.项目资料/05.河南农业气象移动平台/业务数据/GridData/SmForecast/RSM10_00000_20170420_0000.grd";
		filepath = "E:/项目数据资料目录/河南农业/AgmForest/AgmWeather/MB_000000_20181023_0000.grd";
		//filepath = "E:/项目数据资料目录/河南农业/AgmForest/MeteDay/T_00000_20180917_0000.grd";
		try {
			GrdBean grdBean = loadGrdFile(filepath);
			System.out.println(grdBean.toString());
			double x = 113.6500;
			double y = 34.7100;
			double v = getGrdValue(grdBean, x, y);
			System.out.println(x+","+y+"="+v);
			//testData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void testData() throws IOException{
		String str = "T,Tmax,Tmin,R,U,Umin,F,Fmax,S,V";
		String[] strArr = str.split("[,]");
		String filepath = null;
		for(int i=0;i<strArr.length;i++){
			filepath = "E:/项目数据资料目录/河南农业/AgmForest/MeteDay/"+strArr[i]+"_00000_20180917_0000.grd";
			GrdBean grdBean = loadGrdFile(filepath);
			System.out.println(grdBean.toString());
			double x = 112.8;
			double y = 33.88;
			double v = getGrdValue(grdBean, x, y);
			System.out.println(strArr[i]+":"+x+","+y+"="+v);
		}
	}
}
