package com.qris.payment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.qris.payment.model.TransactionQris;

public class checkrest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "http://182.23.65.25:8890/payment/emoney/ipay88/backend";

		// create an instance of RestTemplate
		RestTemplate restTemplate = new RestTemplate();

		// create headers
		HttpHeaders headers = new HttpHeaders();
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
          TransactionQris qris = new TransactionQris();
          qris.setAmount("200");
		// request body parameters
		Map<String, Object> map = new HashMap<>();
		map.put("userId", qris.getAmount());
		map.put("title", "Spring Boot 101");
		map.put("body", "A powerful tool for building web apps.");

		// build the request
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		// send POST request
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
             System.out.println(response);
		
	}

}
