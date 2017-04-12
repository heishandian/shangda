package beans;

public class MapInfo {
	String county;
	int sewageID;
	String short_title;
	double coordinateX;
	double coordinateY;
	int isabnormal;

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public int getSewageID() {
		return sewageID;
	}

	public void setSewageID(int sewageID) {
		this.sewageID = sewageID;
	}

	public String getShort_title() {
		return short_title;
	}

	public void setShort_title(String short_title) {
		this.short_title = short_title;
	}

	public double getCoordinateX() {
		return coordinateX;
	}

	public void setCoordinateX(double coordinateX) {
		this.coordinateX = coordinateX;
	}

	public double getCoordinateY() {
		return coordinateY;
	}

	public void setCoordinateY(double coordinateY) {
		this.coordinateY = coordinateY;
	}

	public int getIsabnormal() {
		return isabnormal;
	}

	public void setIsabnormal(int isabnormal) {
		this.isabnormal = isabnormal;
	}

}
