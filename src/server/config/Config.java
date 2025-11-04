package server.config;

public class Config {
    public enum requests { SEND, LIST, AUTH, END };
	public enum feedbacks { UNAVAILABLE_SERVER, UNREACHABLE_CLIENT, FAILED_COMMAND, OK };
	public final static int[] codes = {401,402,403,200};
	public final static String ip = "192.168.8.103";
	public final static int port = 8086;
	
	/*
	 * 	[0]		401 	-> Il server non riceve la comunicazione
	 * 	[1]		402 	-> Il server non trova il destinatario (non è in contatto)
	 * 	[2]		403	-> Il comando non è andato a buon fine
	 * 	[3]		200 -> OK, la richiesta è andata a buon fine
	 */
}
