/**
 * 这里是文件说明
 */
package com.zondy.agricultural;

/**
 * 模块名称：该模块名称
 * 功能描述：该文件详细功能描述
 * 文档作者：雷志强
 * 创建时间：2018年2月25日 下午9:04:55
 * 初始版本：V1.0
 * 修改记录：
 * *************************************************
 * 修改人：雷志强
 * 修改时间：2018年2月25日 下午9:04:55
 * 修改内容：
 * *************************************************
 */
public class GrdBean {
	
	private int row;
	private int col;
	private double xmin;
	private double ymin;
	private double xmax;
	private double ymax;
	private double zmin;
	private double zmax;
	private double xstep;
	private double ystep;

	public double getXstep() {
		return xstep;
	}
	public void setXstep(double xstep) {
		this.xstep = xstep;
	}
	public double getYstep() {
		return ystep;
	}
	public void setYstep(double ystep) {
		this.ystep = ystep;
	}

	private double[][] data;
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public double getXmin() {
		return xmin;
	}
	public void setXmin(double xmin) {
		this.xmin = xmin;
	}
	public double getYmin() {
		return ymin;
	}
	public void setYmin(double ymin) {
		this.ymin = ymin;
	}
	public double getXmax() {
		return xmax;
	}
	public void setXmax(double xmax) {
		this.xmax = xmax;
	}
	public double getYmax() {
		return ymax;
	}
	public void setYmax(double ymax) {
		this.ymax = ymax;
	}
	public double getZmin() {
		return zmin;
	}
	public void setZmin(double zmin) {
		this.zmin = zmin;
	}
	public double getZmax() {
		return zmax;
	}
	public void setZmax(double zmax) {
		this.zmax = zmax;
	}
	public double[][] getData() {
		return data;
	}
	public void setData(double[][] data) {
		this.data = data;
	}
	
	public GrdBean(){
		
	}
	
	public GrdBean(int row,int col,double xmin,double ymin,double xmax,double ymax,double zmin,double zmax,double[][] data){
		this.row = row;
		this.col = col;
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
		this.zmin = zmin;
		this.zmax = zmax;
		this.data = data;
	}
	
	public String toString(){
		return "row="+row+",col="+col+",xmin="+xmin+",ymin="+ymin+",xmax="+xmax+",ymax="+ymax+",zmin="+zmin+",zmax="+zmax+",xstep="+xstep+",ystep="+ystep;
	}
}
