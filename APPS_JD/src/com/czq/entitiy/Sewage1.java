package com.czq.entitiy;

/**
 * @author apple entity of sewage
 */
public class Sewage1 {

	String countyName;// 所属地区 修改areaID 3
	int stationID;// 站点ID sewageID
	int controlId;// 控制系统ID controlID
	String shortTitle;// 简介 short_title
	String name;// 名称 name
	String address;// 地址 address
	String opNum;// 运营编号 operationum
	float coordinateX;// 纬度 coordinateY
	float coordinateY;// 经度 coordinateX
	float detection1dl;// T下限 detection1DL
	float detection1ul;// T上限 detection1UL
	float detection2dl;// PH下限 detection2DL
	float detection2ul;// PH上限 detection2UL
	float detection3dl;// ORP上限 detection3UL
	float detection3ul;// ORP下限detection3DL
	float detection5dl;// DO下限 detection5DL
	float detection5ul;// DO上限 detection5UL
	float reduceNH3N;// 削减NH3-N reduceNH3N
	float reduceCOD;// 削减COD量reduceCOD
	float reduceP;// 削减COD量reduceCOD
	int runtimeperiod1;// 风机运行时间runtimeperiod1
	int stoptimeperiod1;// 风机停止时间 stoptimeperiod1
	int runtimeperiod2;// 混合回流泵运行时间runtimeperiod2
	int stoptimeperiod2;//混合回流泵停止时间stoptimeperiod2
	int runtimeperiod3;// 污泥回流泵运行时间runtimeperiod3
	int stoptimeperiod3;// 污泥回流泵停止时间时间stoptimeperiod3
	int DCF11;// 电磁阀1运行时间runtimeperiod4
	int DCF12;// 电磁阀1停止时间stoptimeperiod4
	int DCF21;// 电磁阀2运行时间runtimeperiod5
	int DCF22;// 电磁阀2停止时间stoptimeperiod5
	int DCF31;// 电磁阀3运行时间runtimeperiod6
	int DCF32;// 电磁阀3停止时间stoptimeperiod6
	int DCF41;// 电磁阀4运行时间runtimeperiod6
	int DCF42;// 电磁阀4停止时间stoptimeperiod6
	float tonnage;//吨位
	String videourl;//视屏配置地址
	public String getCountyName() {
		return countyName;
	}
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	public int getStationID() {
		return stationID;
	}
	public void setStationID(int stationID) {
		this.stationID = stationID;
	}
	public int getControlId() {
		return controlId;
	}
	public void setControlId(int controlId) {
		this.controlId = controlId;
	}
	public String getShortTitle() {
		return shortTitle;
	}
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOpNum() {
		return opNum;
	}
	public void setOpNum(String opNum) {
		this.opNum = opNum;
	}
	public float getCoordinateX() {
		return coordinateX;
	}
	public void setCoordinateX(float coordinateX) {
		this.coordinateX = coordinateX;
	}
	public float getCoordinateY() {
		return coordinateY;
	}
	public void setCoordinateY(float coordinateY) {
		this.coordinateY = coordinateY;
	}
	public float getDetection1dl() {
		return detection1dl;
	}
	public void setDetection1dl(float detection1dl) {
		this.detection1dl = detection1dl;
	}
	public float getDetection1ul() {
		return detection1ul;
	}
	public void setDetection1ul(float detection1ul) {
		this.detection1ul = detection1ul;
	}
	public float getDetection2dl() {
		return detection2dl;
	}
	public void setDetection2dl(float detection2dl) {
		this.detection2dl = detection2dl;
	}
	public float getDetection2ul() {
		return detection2ul;
	}
	public void setDetection2ul(float detection2ul) {
		this.detection2ul = detection2ul;
	}
	public float getDetection3dl() {
		return detection3dl;
	}
	public void setDetection3dl(float detection3dl) {
		this.detection3dl = detection3dl;
	}
	public float getDetection3ul() {
		return detection3ul;
	}
	public void setDetection3ul(float detection3ul) {
		this.detection3ul = detection3ul;
	}
	public float getDetection5dl() {
		return detection5dl;
	}
	public void setDetection5dl(float detection5dl) {
		this.detection5dl = detection5dl;
	}
	public float getDetection5ul() {
		return detection5ul;
	}
	public void setDetection5ul(float detection5ul) {
		this.detection5ul = detection5ul;
	}
	public float getReduceNH3N() {
		return reduceNH3N;
	}
	public void setReduceNH3N(float reduceNH3N) {
		this.reduceNH3N = reduceNH3N;
	}
	public float getReduceCOD() {
		return reduceCOD;
	}
	public void setReduceCOD(float reduceCOD) {
		this.reduceCOD = reduceCOD;
	}
	public float getReduceP() {
		return reduceP;
	}
	public void setReduceP(float reduceP) {
		this.reduceP = reduceP;
	}
	public int getRuntimeperiod1() {
		return runtimeperiod1;
	}
	public void setRuntimeperiod1(int runtimeperiod1) {
		this.runtimeperiod1 = runtimeperiod1;
	}
	public int getStoptimeperiod1() {
		return stoptimeperiod1;
	}
	public void setStoptimeperiod1(int stoptimeperiod1) {
		this.stoptimeperiod1 = stoptimeperiod1;
	}
	public int getRuntimeperiod2() {
		return runtimeperiod2;
	}
	public void setRuntimeperiod2(int runtimeperiod2) {
		this.runtimeperiod2 = runtimeperiod2;
	}
	public int getStoptimeperiod2() {
		return stoptimeperiod2;
	}
	public void setStoptimeperiod2(int stoptimeperiod2) {
		this.stoptimeperiod2 = stoptimeperiod2;
	}
	public int getRuntimeperiod3() {
		return runtimeperiod3;
	}
	public void setRuntimeperiod3(int runtimeperiod3) {
		this.runtimeperiod3 = runtimeperiod3;
	}
	public int getStoptimeperiod3() {
		return stoptimeperiod3;
	}
	public void setStoptimeperiod3(int stoptimeperiod3) {
		this.stoptimeperiod3 = stoptimeperiod3;
	}
	public int getDCF11() {
		return DCF11;
	}
	public void setDCF11(int dCF11) {
		DCF11 = dCF11;
	}
	public int getDCF12() {
		return DCF12;
	}
	public void setDCF12(int dCF12) {
		DCF12 = dCF12;
	}
	public int getDCF21() {
		return DCF21;
	}
	public void setDCF21(int dCF21) {
		DCF21 = dCF21;
	}
	public int getDCF22() {
		return DCF22;
	}
	public void setDCF22(int dCF22) {
		DCF22 = dCF22;
	}
	public int getDCF31() {
		return DCF31;
	}
	public void setDCF31(int dCF31) {
		DCF31 = dCF31;
	}
	public int getDCF32() {
		return DCF32;
	}
	public void setDCF32(int dCF32) {
		DCF32 = dCF32;
	}
	public int getDCF41() {
		return DCF41;
	}
	public void setDCF41(int dCF41) {
		DCF41 = dCF41;
	}
	public int getDCF42() {
		return DCF42;
	}
	public void setDCF42(int dCF42) {
		DCF42 = dCF42;
	}
	public float getTonnage() {
		return tonnage;
	}
	public void setTonnage(float tonnage) {
		this.tonnage = tonnage;
	}
	public String getVideourl() {
		return videourl;
	}
	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}
	

}
