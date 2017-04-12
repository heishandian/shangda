package beans;
/**
 * @author apple
 * entity of detection data 
 */
public class Detection {
	
    private String testingtime;
    private Float detection1;
    private Float detection2;
    private Float detection3;
    private int	  detection4;//液位
    private Float detection5;
	public String getTestingtime() {
		return testingtime;
	}
	public void setTestingtime(String testingtime) {
		this.testingtime = testingtime;
	}
	public Float getDetection1() {
		return detection1;
	}
	public void setDetection1(Float detection1) {
		this.detection1 = detection1;
	}
	public Float getDetection2() {
		return detection2;
	}
	public void setDetection2(Float detection2) {
		this.detection2 = detection2;
	}
	public Float getDetection3() {
		return detection3;
	}
	public void setDetection3(Float detection3) {
		this.detection3 = detection3;
	}
	public int getDetection4() {
		return detection4;
	}
	public void setDetection4(int detection4) {
		this.detection4 = detection4;
	}
	public Float getDetection5() {
		return detection5;
	}
	public void setDetection5(Float detection5) {
		this.detection5 = detection5;
	}
	
}
