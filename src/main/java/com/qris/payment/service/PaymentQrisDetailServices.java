package com.qris.payment.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qris.payment.enums.TransactionFlex;
import com.qris.payment.model.ClientUser;
import com.qris.payment.model.JsonHistoryClient;
import com.qris.payment.model.JsonHistoryPatner;
import com.qris.payment.model.TransactionQris;
import com.qris.payment.repo.ClienRepo;
import com.qris.payment.repo.JsonHistoryClienRepo;
import com.qris.payment.repo.JsonHistoryPatnerRepo;
import com.qris.payment.repo.TransactionRepo;

import lombok.Data;

@Service
@Data
public class PaymentQrisDetailServices {
	private final static Logger logger = LoggerFactory.getLogger(PaymentQrisDetailServices.class);
	@Autowired
	private TransactionRepo traRepo;

	@Autowired
	private JsonHistoryPatnerRepo historyRepo;

	@Autowired
	private JsonHistoryClienRepo jsonhistoryclien;

	@Autowired
	private ClienRepo clienRepo;

	public JsonNode sendPatner(JsonNode request, ClientUser client) throws JsonProcessingException {
		// TODO Auto-generated method stub
		TransactionQris qris = new TransactionQris();
		qris.setAmount(request.get("Amount").asText());
		JsonHistoryPatner historyPatner = new JsonHistoryPatner();
		ObjectMapper mapper = new ObjectMapper();
		((ObjectNode) request).put("Amount", request.get("Amount").asText()+00);
		
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
        System.out.println("request :"  +  json);
		
		historyPatner.setRequestmsg(json);
		historyPatner.setRequestTime(new Date());
		historyPatner.setRefno(request.get("RefNo").asText());
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE.toString());
		HttpEntity formEntity = new HttpEntity<String>(json, headers);
		ObjectMapper mappers = new ObjectMapper();
		ResponseEntity<String> responseEntityStr = restTemplate.postForEntity(
				"https://payment.ipay88.co.id/ePayment/WebService/PaymentAPI/Checkout", formEntity, String.class);
		JsonNode root = mappers.readTree(responseEntityStr.getBody());
		((ObjectNode) root).put("Amount", qris.getAmount());
		
		String jsonResponse = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
		logger.info("Response :" + jsonResponse);
		historyPatner.setResponsemsg(jsonResponse);
		historyPatner.setResponseTime(new Date());
		historyPatner.setCallBackend(null);
		historyPatner.setCallbackTime(null);
		historyPatner.setResponsecode(root.get("Status").asText());
		String rpsmsg = root.get("ErrDesc").asText().equals("") ? "Sukses" : root.get("ErrDesc").asText();
		historyPatner.setResponsemessage(rpsmsg);
		historyRepo.save(historyPatner);
		if ("6".equals(root.get("Status").asText())) {
			
			qris.setTransactionNumber(root.get("TransId").asText());
			qris.setFlextrs(TransactionFlex.UNPAID);
			qris.setCreateDate(new Date());
			qris.setReturncallbackend(0);
			qris.setReturnRequest(0);
			qris.setJsonHistoryPatner(historyPatner);
			qris.setClientuser_id(client.getId());
			traRepo.save(qris);
			((ObjectNode) root).remove("ErrDesc");
			((ObjectNode) root).put("massage","Sukses");

		}

		return root;
	}

	public JsonNode sendCallBack(JsonNode param) throws JsonMappingException, JsonProcessingException {
//	
		TransactionQris transaction = traRepo.findByTransactionNumber(param.get("TransId").asText());
		Optional<ClientUser> clientUser = clienRepo.findById(transaction.getClientuser_id());
		JsonHistoryPatner historyPatner = historyRepo.findByRefno(param.get("RefNo").asText());
		System.out.println(clientUser);
		historyPatner.setCallBackend(param.toString());
		historyPatner.setCallbackTime(new Date());
		historyRepo.save(historyPatner);
		transaction.setFlextrs(TransactionFlex.PAID);
		traRepo.save(transaction);
		JsonHistoryClient historyClient = new JsonHistoryClient();
		

		String url = clientUser.get().getUrlBackend();
		RestTemplate restTemplate = new RestTemplate();

		// create headers
		HttpHeaders headers = new HttpHeaders();
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		// request body parameters
		Map<String, Object> map = new HashMap<>();
		map.put("TransId", transaction.getTransactionNumber());
		map.put("Refno", historyPatner.getRefno());
		map.put("Amount", transaction.getAmount());
		map.put("Status", transaction.getFlextrs());

		// build the request
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
		
		ObjectMapper jsonmap = new ObjectMapper();
		String jsonRequest = jsonmap.writerWithDefaultPrettyPrinter().writeValueAsString(map);
		
		historyClient.setRequestmsg(jsonRequest);
		
		historyClient.setRequestTime(new Date());
		historyClient.setTransaksi_Id(String.valueOf(transaction.getId()));
		// send POST request
//		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		 JsonNode callbackClient = JsonNodeFactory.instance.objectNode();
		try {
			ObjectMapper mappers = new ObjectMapper();
	
			
			ResponseEntity<String> out = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			callbackClient = mappers.readTree(out.getBody());
			
			String jsonResponse = mappers.writerWithDefaultPrettyPrinter().writeValueAsString(callbackClient);
			logger.info("Response :" + jsonResponse);
		
			historyClient.setResponseTime(new Date());

		
		
			jsonhistoryclien.save(historyClient);
		} catch (HttpStatusCodeException e) {
			// TODO: handle exception
			  int statusCode = e.getStatusCode().value();
			  System.out.println("erro"+statusCode);
; 		;
			  ((ObjectNode) callbackClient).put("code", e.getStatusCode().value());
			  ((ObjectNode) callbackClient).put("message", e.getStatusCode().name());
				
			
				
		}
		historyClient.setResponsemsg(callbackClient.toString());
		historyClient.setResponsecode(callbackClient.get("code").asText());
		historyClient.setResponsemessage(callbackClient.get("message").asText());
		historyClient.setResponseTime(new Date());
		jsonhistoryclien.save(historyClient);
		
				
			return callbackClient;
	
	}
	

}
