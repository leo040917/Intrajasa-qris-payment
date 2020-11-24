package com.qris.payment.plugin;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.codec.binary.Base64;
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
	
	public String encrypt(String password) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(password.getBytes("UTF-8"));
			byte raw[] = md.digest();
			String encodedString = new String(new Base64().encode(raw));
			return encodedString;
		}

		catch (NoSuchAlgorithmException e) {
		}

		catch (java.io.UnsupportedEncodingException e) {
		}

		return null;
	}
	

}
