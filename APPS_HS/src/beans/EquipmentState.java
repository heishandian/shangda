package beans;

//import java.sql.Timestamp;

/**
 * @author apple
 * entity of equipments' states
 */
public class EquipmentState {
	private String testingtime;
    private Short equipment1state;
    private Short equipment2state;
    private Short equipment3state;
    private Short equipment4state;
	public String getTestingtime() {
		return testingtime;
	}
	public void setTestingtime(String testingtime) {
		this.testingtime = testingtime;
	}
	public Short getEquipment1state() {
		return equipment1state;
	}
	public void setEquipment1state(Short equipment1state) {
		this.equipment1state = equipment1state;
	}
	public Short getEquipment2state() {
		return equipment2state;
	}
	public void setEquipment2state(Short equipment2state) {
		this.equipment2state = equipment2state;
	}
	public Short getEquipment3state() {
		return equipment3state;
	}
	public void setEquipment3state(Short equipment3state) {
		this.equipment3state = equipment3state;
	}
	public Short getEquipment4state() {
		return equipment4state;
	}
	public void setEquipment4state(Short equipment4state) {
		this.equipment4state = equipment4state;
	}
    
}
