package com.qris.payment.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qris.payment.model.ClientUser;
import com.qris.payment.model.JsonHistoryPatner;
import com.qris.payment.plugin.Signature;
import com.qris.payment.repo.JsonHistoryPatnerRepo;
import com.qris.payment.request.DataCustomer;
import com.qris.payment.request.Sellers;
import com.qris.payment.response.GeneralResponse;
import com.qris.payment.service.JwtUserDetailsService;
import com.qris.payment.service.PaymentQrisDetailServices;

@RestController
@CrossOrigin
@RequestMapping("/payment")
public class PaymentQrisController {
	private final static Logger logger = LoggerFactory.getLogger(PaymentQrisController.class);
	@Value("${com.ipay88.merchancode}")
	private String merchancode;
	@Value("${com.ipay88.key}")
	private String key;
	@Value("${com.ipay88.payment}")
	private String payment;
	@Value("${com.status}")
	private String status;
	
	@Autowired
	private Signature signature;
	
	@Autowired
	private JwtUserDetailsService clientservices;
	
	@Autowired
	private JsonHistoryPatnerRepo jsonrepo;
	
	@Autowired
	private PaymentQrisDetailServices paymentServices;
	

	
	@RequestMapping(value = "/qris/shopepay", method = RequestMethod.POST)
	public ResponseEntity ipay88(@RequestBody JsonNode body, @AuthenticationPrincipal UserDetails details,
			HttpServletRequest header) throws JsonProcessingException 
	{
		ClientUser clientUser= clientservices.checkUser(details.getUsername());
		String sha256hexnode = DigestUtils.sha256Hex(body.toString());
		String HTTPMethod = "POST";
		Instant instant = Instant.now();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		instant = timestamp.toInstant();
		String StringToSign = HTTPMethod + ":" + header.getHeader("X-merchant-code") + sha256hexnode.toLowerCase();
		System.out.println("StringToSign :" + StringToSign);
		
		String param =header.getHeader("X-Intra-Signature");
		
		try {
			boolean signaturSystem = signature.generated(StringToSign,clientUser.getMerchankey(),param);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			GeneralResponse response = new GeneralResponse(013,"invalid Singnatur" ,header.getHeader("X-Intra-Signature"));
			JsonHistoryPatner  historyPatner= new JsonHistoryPatner();
			historyPatner.setRefno(null);
		    historyPatner.setRequestmsg(body.toString());
		    historyPatner.setRequestTime(new Date());
		    historyPatner.setResponsecode("013");
			historyPatner.setResponsemsg(response.toString());
			historyPatner.setRequestTime(new Date());
			historyPatner.setCallBackend(null);
			historyPatner.setCallbackTime(null);

			historyPatner.setResponsemessage("invalid Singnatur");
			jsonrepo.save(historyPatner);
			ResponseEntity.ok(response);
			
		}
		Date date = new Date();
		SimpleDateFormat formatNowDay = new SimpleDateFormat("dd-MM-yyyy");
		String currentMonth = formatNowDay.format(date);
		System.out.println(currentMonth);
		List<JsonHistoryPatner> currentNum = jsonrepo.findByfindNumber(currentMonth);		
		int Number = currentNum.size() + 1;
//		System.out.println("no: " + Number);
		String Refno = currentMonth + String.format("%05d", Number);
//		System.out.println(Refno.replace("-", ""));
		((ObjectNode) body).put("MerchantCode", merchancode);
//		((ObjectNode) body).put("RefNo", Refno.replace("-", ""));
		((ObjectNode) body).put("PaymentId",payment);
		((ObjectNode) body).put("Amount",body.get("Amount").asInt()+ Integer.valueOf("00"));
				
//		Signatur Patner
		String sha1hex = key + merchancode + body.get("RefNo").asText() + body.get("Amount").asText()
				+ body.get("Currency").asText();
		System.out.println(" sha1hex"+ sha1hex);
		String generedsignature =signature.encrypt(sha1hex);
//		System.out.println("Signature Generated :" + generedsignature);
		logger.info("Signature Generated", generedsignature);

		((ObjectNode) body).put("Lang","UTF-8");
		((ObjectNode) body).put("Signature", generedsignature);
		((ObjectNode) body).put("ResponseURL","");
		((ObjectNode) body).put("BackendURL","http://182.23.65.25:8890/payment/ipay88/callback/shopepay");
		((ObjectNode) body).put("xfield1","");
		
		List<Sellers> listsellers =new ArrayList<>();
//	    clientUser client= clientservices.findByUsername(details.getUsername());	    
		Sellers sellers= new Sellers();
		sellers.setId(clientUser.getId());
		sellers.setName(clientUser.getFirsname()+clientUser.getFirsname());
		String sellerNum= String.valueOf(clientUser.getId());
		sellers.setSellerIdNumber(String.format("%05d", Integer.valueOf(sellerNum)));
		sellers.setUrl(clientUser.getUrlWebSite());
		sellers.setEmail(clientUser.getEmail());
		DataCustomer address=  new DataCustomer();
		address.setFirstName("Intrajasa");
		address.setLastName("TeknoSolusi");
		address.setAddress("Jl. Jend. Sudirman Kav. 25, RT.10/RW.1, Kuningan, Karet, Kecamatan Setiabudi, Kota Jakarta Selatan, Daerah Khusus Ibukota Jakarta");
		address.setCity("Jakarta Selatan");
		address.setState("DKI Jakarta");
		address.setPostalCode("12920");
		address.setPhone("021-3970-0424");
		address.setCountryCode("ID");
		sellers.setAddress(address);
		listsellers.add(sellers);
		((ObjectNode) body).set("Sellers", new ObjectMapper().valueToTree(listsellers));
		JsonNode request = body;
		logger.info("Request Param" + request);
		JsonNode Response= paymentServices.sendPatner(request,clientUser);
		((ObjectNode) Response).remove("MerchantCode");
		((ObjectNode) Response).remove("PaymentId");
		((ObjectNode) Response).remove("CheckoutURL");
		((ObjectNode) Response).remove("Signature");
		((ObjectNode) Response).remove("xfield1");
		((ObjectNode) Response).remove("AuthCode");
		
	
		return ResponseEntity.ok(Response);
		
	} 
	
	@RequestMapping(value = "/ipay88/callback", method = RequestMethod.POST)
	public JsonNode callback(@RequestParam String MerchantCode, @RequestParam String PaymentId,
			@RequestParam String RefNo, @RequestParam String Amount, @RequestParam String Currency,
			@RequestParam String Remark, @RequestParam String TransId, @RequestParam String AuthCode,
			@RequestParam String Status, @RequestParam String ErrDesc, @RequestParam String Signature,
			@RequestParam String PaymentDate, HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
		
		String clientIpAddress = request.getHeader("X-FORWARDED-FOR");
		if (clientIpAddress == null || clientIpAddress.equals(""))
			clientIpAddress = request.getRemoteAddr();
		
		ObjectNode param = JsonNodeFactory.instance.objectNode();
		((ObjectNode) param).put("MerchantCode", MerchantCode);
		((ObjectNode) param).put("PaymentId", PaymentId);
		((ObjectNode) param).put("RefNo", RefNo);
		((ObjectNode) param).put("Amount", Amount);
		((ObjectNode) param).put("Currency", Currency);
		((ObjectNode) param).put("Remark", Remark);
		((ObjectNode) param).put("TransId", TransId);
		((ObjectNode) param).put("AuthCode", AuthCode);
		((ObjectNode) param).put("Status", Status);
		((ObjectNode) param).put("ErrDesc", ErrDesc);
		((ObjectNode) param).put("Signature", Signature);
		((ObjectNode) param).put("PaymentDate", PaymentDate);
		
		

		
		ObjectMapper mappers = new ObjectMapper();
		String requestcallback = mappers.writerWithDefaultPrettyPrinter().writeValueAsString(param.toString());
	
		logger.info("call back :" , requestcallback);
		JsonNode  response= paymentServices.sendCallBack(param);
		return response;

	}

	@RequestMapping(value = "/emoney/ipay88/backend", method = RequestMethod.POST)
	public JsonNode callback(@RequestBody JsonNode body) throws JsonProcessingException{
		logger.info("call back client " , body);
		ObjectNode callbackClient = JsonNodeFactory.instance.objectNode();
		((ObjectNode) callbackClient).put("code", "200");
		((ObjectNode) callbackClient).put("message", "Sukses");
//		((ObjectNode) callbackClient).put("data", body);
		
	
		return callbackClient;
		
		
	}
}
