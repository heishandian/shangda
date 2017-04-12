package beans;

public class Alert {
	private String alertTime;//预警时间
	private String sewage_name;//站点名
	private int alertInfo;//预警信息
	private int state;//处理状态
	private String admin;//管理员
	public String getAlertTime() {
		return alertTime;
	}
	public void setAlertTime(String alertTime) {
		this.alertTime = alertTime;
	}
	public String getSewage_name() {
		return sewage_name;
	}
	public void setSewage_name(String sewage_name) {
		this.sewage_name = sewage_name;
	}
	public int getAlertInfo() {
		return alertInfo;
	}
	public void setAlertInfo(int alertInfo) {
		this.alertInfo = alertInfo;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}


	
}
