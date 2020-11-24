package com.qris.payment.service;

import java.util.Date;
import java.util.Optional;

import javax.net.ssl.SSLEngineResult.Status;
import javax.transaction.Transaction;

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
import com.qris.payment.controller.PaymentQrisController;
import com.qris.payment.enums.TransactionFlex;
import com.qris.payment.model.ClientUser;
import com.qris.payment.model.JsonHistoryClient;
import com.qris.payment.model.JsonHistoryPatner;
import com.qris.payment.model.TransactionQris;
import com.qris.payment.repo.ClienRepo;
import com.qris.payment.repo.JsonHistoryRepo;
import com.qris.payment.repo.TransactionRepo;

import springfox.documentation.spring.web.json.Json;

@Service
public class PaymentQrisDetailServices {
	private final static Logger logger = LoggerFactory.getLogger(PaymentQrisDetailServices.class);
	@Autowired
	private TransactionRepo traRepo;

	@Autowired
	private JsonHistoryRepo historyRepo;

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
			qris.setAmount(root.get("Amount").asText());
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
		JsonHistoryPatner historyPatner = historyRepo.findByRefno(param.get("RefNo").asText());
		TransactionQris transaction = traRepo.findByJsonHistoryPatner(historyPatner);
		Optional<ClientUser> clientUser = clienRepo.findById(transaction.getClientuser_id());
		System.out.println(clientUser);

		transaction.setFlextrs(TransactionFlex.PAID);
		traRepo.save(transaction);
		JsonHistoryClient historyClient = new JsonHistoryClient();
		historyClient.setRequestmsg(transaction.toString());
		historyClient.setRequestTime(new Date());

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE.toString());
		HttpEntity formEntity = new HttpEntity<String>(transaction.toString(), headers);
		ObjectMapper mappers = new ObjectMapper();
		ResponseEntity<String> responseEntityStr = restTemplate.postForEntity(clientUser.get().getUrlBackend(),
				formEntity, String.class);

		JsonNode root;
		root = mappers.readTree(responseEntityStr.getBody());
		String jsonResponse = mappers.writerWithDefaultPrettyPrinter().writeValueAsString(root);
		
		
		historyClient.setResponsemsg(root.toString());
		historyClient.setResponseTime(new Date());
		
		historyClient.setResponsecode(root.get("code").asText());
		historyClient.setResponsemessage(root.get("message").asText());
		
		ObjectNode callbackClient = JsonNodeFactory.instance.objectNode();

		((ObjectNode) param).put("code", "200");
		((ObjectNode) param).put("message", "Sukses");
		((ObjectNode) param).put("data", param.toString());
		return callbackClient;
	}

}
