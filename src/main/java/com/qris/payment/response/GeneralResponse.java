package com.qris.payment.response;

public class GeneralResponse<T> {
	private int code;

	private String message;

	private T data;

	public GeneralResponse(int code, String message, T data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}

	public GeneralResponse() {
		super();
	}

}
