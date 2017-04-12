package beans;

public class Message {
	String tel;
	String sendtime;
	int abnormaltype;
	String messagedetail;
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public int getAbnormaltype() {
		return abnormaltype;
	}

	public void setAbnormaltype(int  abnormaltype) {
		this.abnormaltype = abnormaltype;
	}

	public String getMessagedetail() {
		return messagedetail;
	}

	public void setMessagedetail(String messagedetail) {
		this.messagedetail = messagedetail;
	}

}
