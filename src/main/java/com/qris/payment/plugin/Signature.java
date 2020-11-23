package com.qris.payment.plugin;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

@Service
public class Signature {

	public boolean generated(String stringToSign, String validasikey,String param) throws NoSuchAlgorithmException, InvalidKeyException {
		// TODO Auto-generated method stub
		
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(validasikey.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		String hash = Hex.encodeHexString(sha256_HMAC.doFinal(stringToSign.getBytes()));
		return hash.equals(param);
	}
	
	
	

}
