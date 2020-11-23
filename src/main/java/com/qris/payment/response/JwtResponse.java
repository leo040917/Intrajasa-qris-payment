package com.qris.payment.response;

import java.io.Serializable;

public class JwtResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private  String jwttoken;
	private  String token_type;
	private  long expires_in;
	public String getJwttoken() {
		return jwttoken;
	}
	public void setJwttoken(String jwttoken) {
		this.jwttoken = jwttoken;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public long getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	

}