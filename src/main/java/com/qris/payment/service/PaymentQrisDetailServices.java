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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);

		JsonHistoryPatner historyPatner = new JsonHistoryPatner();
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

			TransactionQris qris = new TransactionQris();
			qris.setTransactionNumber(root.get("TransId").asText());
			qris.setAmount(root.get("Amount").asText().substring(0,3));
			qris.setFlextrs(TransactionFlex.UNPAID);
			qris.setCreateDate(new Date());
			qris.setReturncallbackend(0);
			qris.setReturnRequest(0);
			qris.setJsonHistoryPatner(historyPatner);
			qris.setClientuser_id(client.getId());
			traRepo.save(qris);

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
		historyClient.setRequestmsg(transaction.toString());
		historyClient.setRequestTime(new Date());
		historyClient.setTransaksi_Id(String.valueOf(transaction.getId()));
		
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
		map.put("Amount",transaction.getAmount());
		map.put("Status", transaction.getFlextrs());

		// build the request
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		// send POST request
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		ObjectMapper mappers = new ObjectMapper();
		JsonNode root = mappers.readTree(response.getBody());
		String jsonResponse = mappers.writerWithDefaultPrettyPrinter().writeValueAsString(root);
		logger.info("Response :" + jsonResponse);
		historyClient.setResponsemsg(jsonResponse);
		historyClient.setResponseTime(new Date());
		
		historyClient.setResponsecode(root.get("code").asText());
		historyClient.setResponsemessage(root.get("message").asText());
		jsonhistoryclien.save(historyClient);
		ObjectNode callbackClient = JsonNodeFactory.instance.objectNode();


		return root;
	}

}
