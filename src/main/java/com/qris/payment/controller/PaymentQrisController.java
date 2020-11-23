package com.qris.payment.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.qris.payment.model.ClientUser;
import com.qris.payment.model.JsonHistoryPatner;
import com.qris.payment.plugin.Signature;
import com.qris.payment.repo.JsonHistoryRepo;
import com.qris.payment.response.GeneralResponse;
import com.qris.payment.service.JwtUserDetailsService;

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
	private JsonHistoryRepo jsonrepo;
	
	@RequestMapping(value = "/qris/shopepay", method = RequestMethod.POST)
	public ResponseEntity ipay88(@RequestBody JsonNode body, @AuthenticationPrincipal UserDetails details,
			HttpServletRequest header) 
	{
		ClientUser clientUser= clientservices.check(details.getUsername());
		String sha256hexnode = DigestUtils.sha256Hex(body.toString());
		String HTTPMethod = "POST";
		Instant instant = Instant.now();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		instant = timestamp.toInstant();
		String StringToSign = HTTPMethod + ":" + clientUser.getMerchankey() + sha256hexnode.toLowerCase();
		System.out.println("StringToSign :" + StringToSign);
		
		String param =header.getHeader("X-Intra-Signature");
		
		try {
			boolean signaturSystem = signature.generated(StringToSign,clientUser.getMerchankey(),param);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ResponseEntity.ok(new GeneralResponse(013,"invalid Singnatur" ,header.getHeader("X-Intra-Signature")));
			
		}
		Date date = new Date();
		SimpleDateFormat formatNowDay = new SimpleDateFormat("dd-MM-yyyy");
		String currentMonth = formatNowDay.format(date);
		System.out.println(currentMonth);
		List<JsonHistoryPatner> currentNum = jsonrepo.findByfindNumber(currentMonth);		
		int Number = currentNum.size() + 1;
		System.out.println("no: " + Number);
		String Refno = currentMonth + String.format("%05d", Number);
		System.out.println(Refno.replace("-", ""));
		return null;
		
	} 

}
