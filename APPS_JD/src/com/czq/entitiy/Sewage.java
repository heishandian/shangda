package com.czq.entitiy;


/**
 * @author apple
 * entity of sewage
 */
public class Sewage {
	
	private Integer sewageId;
    //private Area area;
    //private Administrator administrator;
	private Integer areaId;
	private Integer administratorId;
	private Integer controlId;
	private String shortTitle;
	private String name;
	private String address;
	private Float coordinateX;
	private Float coordinateY;
	private String equipment1state;
	private String equipment2state;
	private String equipment3state;
	private String equipment4state;
	private String equipment5state="1";
	private Float detection1;
	private Float detection1ul;
	private Float detection1dl;
	private Float detection2;
	private Float detection2ul;
	private Float detection2dl;
	private Float detection3ul;
	private Float detection3;
	private Float detection3dl;
	private Float detection4;
	private Float detection4ul;
	private Float detection4dl;
	private Float detection5;
	private Float detection5ul;
	private Float detection5dl;
	private Float detection6=0f;
	private Float detection6ul=0f;
	private Float detection6dl=0f;
	private String confirmGratingTime;
	private int gratingDays=0;
	private String control_strategy;
	private String device_alert;
	////////////////////////////////////
	private float waterflow;
	private float reduceCOD;
	private float reduceNH3N;
	private float reduceP;
	////////////////////////////////////
	private String AdministratorName;
	private String countyName;
	
	public Integer getSewageId() {
		return sewageId;
	}

	public void setSewageId(Integer sewageId) {
		this.sewageId = sewageId;
	}

	public String getAdministratorName() {
		return AdministratorName;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public Integer getAdministratorId() {
		return administratorId;
	}

	public void setAdministratorId(Integer administratorId) {
		this.administratorId = administratorId;
	}

	public void setAdministratorName(String administratorName) {
		AdministratorName = administratorName;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getDevice_alert() {
		return device_alert;
	}

	public void setDevice_alert(String deviceAlert) {
		device_alert = deviceAlert;
	}

	public String getControl_strategy() {
			return control_strategy;
		}

	public void setControl_strategy(String controlStrategy) {
			control_strategy = controlStrategy;
		}
	public String getConfirmGratingTime() {
			return confirmGratingTime;
		}
	
	public void setConfirmGratingTime(String confirmGratingTime) {
			this.confirmGratingTime = confirmGratingTime;
		} 
	public Integer getControlId() {
		return controlId;
	}
	
	public void setControlId(Integer controlId) {
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
	public Float getCoordinateX() {
		return coordinateX;
	}
	public void setCoordinateX(Float coordinateX) {
		this.coordinateX = coordinateX;
	}
	public Float getCoordinateY() {
		return coordinateY;
	}
	public void setCoordinateY(Float coordinateY) {
		this.coordinateY = coordinateY;
	}
	public String getEquipment1state() {
		return equipment1state;
	}
	public void setEquipment1state(String equipment1state) {
		this.equipment1state = equipment1state;
	}
	public String getEquipment2state() {
		return equipment2state;
	}
	public void setEquipment2state(String equipment2state) {
		this.equipment2state = equipment2state;
	}
	public String getEquipment3state() {
		return equipment3state;
	}
	public void setEquipment3state(String equipment3state) {
		this.equipment3state = equipment3state;
	}
	public String getEquipment4state() {
		return equipment4state;
	}
	public void setEquipment4state(String equipment4state) {
		this.equipment4state = equipment4state;
	}
	public String getEquipment5state() {
		return equipment5state;
	}
	public void setEquipment5state(String equipment5state) {
		this.equipment5state = equipment5state;
	}
	public Float getDetection1() {
		return detection1;
	}
	public void setDetection1(Float detection1) {
		this.detection1 = detection1;
	}
	public Float getDetection1ul() {
		return detection1ul;
	}
	public void setDetection1ul(Float detection1ul) {
		this.detection1ul = detection1ul;
	}
	public Float getDetection1dl() {
		return detection1dl;
	}
	public void setDetection1dl(Float detection1dl) {
		this.detection1dl = detection1dl;
	}
	public Float getDetection2() {
		return detection2;
	}
	public void setDetection2(Float detection2) {
		this.detection2 = detection2;
	}
	public Float getDetection2ul() {
		return detection2ul;
	}
	public void setDetection2ul(Float detection2ul) {
		this.detection2ul = detection2ul;
	}
	public Float getDetection2dl() {
		return detection2dl;
	}
	public void setDetection2dl(Float detection2dl) {
		this.detection2dl = detection2dl;
	}
	public Float getDetection3ul() {
		return detection3ul;
	}
	public void setDetection3ul(Float detection3ul) {
		this.detection3ul = detection3ul;
	}
	public Float getDetection3() {
		return detection3;
	}
	public void setDetection3(Float detection3) {
		this.detection3 = detection3;
	}
	public Float getDetection3dl() {
		return detection3dl;
	}
	public void setDetection3dl(Float detection3dl) {
		this.detection3dl = detection3dl;
	}
	public Float getDetection4() {
		return detection4;
	}
	public void setDetection4(Float detection4) {
		this.detection4 = detection4;
	}
	public Float getDetection4ul() {
		return detection4ul;
	}
	public void setDetection4ul(Float detection4ul) {
		this.detection4ul = detection4ul;
	}
	public Float getDetection4dl() {
		return detection4dl;
	}
	public void setDetection4dl(Float detection4dl) {
		this.detection4dl = detection4dl;
	}
	public Float getDetection5() {
		return detection5;
	}
	public void setDetection5(Float detection5) {
		this.detection5 = detection5;
	}
	public Float getDetection5ul() {
		return detection5ul;
	}
	public void setDetection5ul(Float detection5ul) {
		this.detection5ul = detection5ul;
	}
	public Float getDetection5dl() {
		return detection5dl;
	}
	public void setDetection5dl(Float detection5dl) {
		this.detection5dl = detection5dl;
	}
	public Float getDetection6() {
		return detection6;
	}
	public void setDetection6(Float detection6) {
		this.detection6 = detection6;
	}
	public Float getDetection6ul() {
		return detection6ul;
	}
	public void setDetection6ul(Float detection6ul) {
		this.detection6ul = detection6ul;
	}
	public Float getDetection6dl() {
		return detection6dl;
	}
	public void setDetection6dl(Float detection6dl) {
		this.detection6dl = detection6dl;
	}
	public int getGratingDays() {
		return gratingDays;
	}
	public void setGratingDays(int gratingDays) {
		this.gratingDays = gratingDays;
	}
	public float getWaterflow() {
		return waterflow;
	}
	public void setWaterflow(float waterflow) {
		this.waterflow = waterflow;
	}
	public float getReduceCOD() {
		return reduceCOD;
	}
	public void setReduceCOD(float reduceCOD) {
		this.reduceCOD = reduceCOD;
	}
	public float getReduceNH3N() {
		return reduceNH3N;
	}
	public void setReduceNH3N(float reduceNH3N) {
		this.reduceNH3N = reduceNH3N;
	}
	public float getReduceP() {
		return reduceP;
	}
	public void setReduceP(float reduceP) {
		this.reduceP = reduceP;
	}
	 
	 
}
