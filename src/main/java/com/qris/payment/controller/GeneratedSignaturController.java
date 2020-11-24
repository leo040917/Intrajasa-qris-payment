package com.qris.payment.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/generated")
public class GeneratedSignaturController {

	@RequestMapping(value = "/generatedSignatur", method = RequestMethod.POST)
	private ResponseEntity<?> generatedsignatur(@RequestBody JsonNode body, HttpServletRequest header)
			throws InvalidKeyException, NoSuchAlgorithmException {

		String sha256hexnode = DigestUtils.sha256Hex(body.toString());
		String HTTPMethod = "POST";
		Instant instant = Instant.now();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		instant = timestamp.toInstant();
		String StringToSign = HTTPMethod + ":" + header.getHeader("X-Merchant-Code") + sha256hexnode.toLowerCase();
		System.out.println("StringToSign api :" + StringToSign);
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		String validationKey = header.getHeader("X-Merchant-key");
		SecretKeySpec secret_key = new SecretKeySpec(validationKey.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		String hash = Hex.encodeHexString(sha256_HMAC.doFinal(StringToSign.getBytes()));
		return ResponseEntity.ok(hash);

	}

}
