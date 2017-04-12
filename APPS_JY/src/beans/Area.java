package beans;

public class Area {
	
	private Integer areaId;
	private Integer superiorArea;
	private String name;
	private byte[] map;
	private String intro;
	private String principal;
	private String tel;
	
	public Integer getAreaId() {
		return areaId;
	}
	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}
	public Integer getSuperiorArea() {
		return superiorArea;
	}
	public void setSuperiorArea(Integer superiorArea) {
		this.superiorArea = superiorArea;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getMap() {
		return map;
	}
	public void setMap(byte[] map) {
		this.map = map;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	
}
