package com.qris.payment.plugin;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.stereotype.Service;
@Service
public class CryptoIntra {
//
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		randomAlphanum("123456");
//         
//	}

	public String randomAlphanum(String param,int panjang) {

		String chrs = param;
		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstanceStrong();
			String customTag = secureRandom.ints(panjang, 0, chrs.length()).mapToObj(i -> chrs.charAt(i))
					.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
			System.out.println(customTag);
			
			return customTag;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		// 9 is the length of the string you want
		

	}
	
	
	
	

}
