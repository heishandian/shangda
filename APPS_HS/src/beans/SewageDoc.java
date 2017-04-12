package beans;

public class SewageDoc {
String short_title;//站点名称
String operationnum;//运营编号
String controlID;//控制ID
String controlMethod;//处理工艺
String tonnage ;//吨位
String emissionStandard; //排放标准
public String getShort_title() {
	return short_title;
}
public void setShort_title(String short_title) {
	this.short_title = short_title;
}
public String getOperationnum() {
	return operationnum;
}
public void setOperationnum(String operationnum) {
	this.operationnum = operationnum;
}
public String getControlID() {
	return controlID;
}
public void setControlID(String controlID) {
	this.controlID = controlID;
}
public String getControlMethod() {
	return controlMethod;
}
public void setControlMethod(String controlMethod) {
	this.controlMethod = controlMethod;
}
public String getTonnage() {
	return tonnage;
}
public void setTonnage(String tonnage) {
	this.tonnage = tonnage;
}
public String getEmissionStandard() {
	return emissionStandard;
}
public void setEmissionStandard(String emissionStandard) {
	this.emissionStandard = emissionStandard;
}

}
