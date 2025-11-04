package server.comms;
import server.config.Config;

public class Response {
	
	public Config.feedbacks feedback;
	public String from;
	public int code;
	public String field;
	
	//----------------------------------------------------------
	
	public Response(Config.feedbacks feedback, String from, int code, String field) {
		super();
		this.feedback = feedback;
		this.from = from;
		this.code = code;
		this.field = field;
	}

	public Response() {}
	
	//----------------------------------------------------------

	public Config.feedbacks getFeedback() {
		return feedback;
	}


	public void setField(String field) {
		this.field = field;
	}
	
	public String getField() {
		return field;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getFrom() {
		return from;
	}



	public void setFeedback(Config.feedbacks feedback) {
		this.feedback = feedback;
	}


	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}
	
	
	
}
