package com.business.travel.app.exceptions;

public class ApiException extends RuntimeException {

	private int code;

	public ApiException(int code) {
		this.code = code;
	}

	public ApiException(int code, String message) {
		super(message);
		this.code = code;
	}

	public ApiException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public ApiException(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}
}
