package beans;
public class AlertMessage {
	String short_title;//污水站名称
	String operationnum;//运营编号
	String alertInfo;//故障类型
	String testingtime;//故障检测时间
	String lasttestingtime;//最后一次故障发生时间		
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
	public String getAlertInfo() {
		return alertInfo;
	}
	public void setAlertInfo(String alertInfo) {
		this.alertInfo = alertInfo;
	}
	public String getTestingtime() {
		return testingtime;
	}
	public void setTestingtime(String testingtime) {
		this.testingtime = testingtime;
	}
	public String getLasttestingtime() {
		return lasttestingtime;
	}
	public void setLasttestingtime(String lasttestingtime) {
		this.lasttestingtime = lasttestingtime;
	}
						
	
}
