package com.tickstock.stocks.accessor;

public class InvalidBackendResponse extends Exception {
	private static final long serialVersionUID = 1L;
	private int code;
	private String message;
	public InvalidBackendResponse(int code, String message){
		this.code = code;
		this.message = message;
	}
	public int getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "Code:"+code + "\nMessage:"+ message;
	}
}
