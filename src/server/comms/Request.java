package server.comms;
import server.config.Config;

public class Request {
	
	public Config.requests request;
	public String to;
	public String message;
	
	//---------------------------------------------------------------
	
	public Request(Config.requests request, String to, String message) {
		super();
		this.request = request;
		this.to = to;
		this.message = message;
	}
	
	public Request() {}
	
	//---------------------------------------------------------------

	public Config.requests getRequest() {
		return this.request;
	}

	public void setRequest(Config.requests request) {
		this.request = request;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
