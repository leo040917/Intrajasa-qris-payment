package com.qris.payment.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@CrossOrigin
@RequestMapping("/payment")
public class PaymentQrisController {
	
	
	@RequestMapping(value = "/qris/shopepay", method = RequestMethod.POST)
	public JsonNode ipay88(@RequestBody JsonNode body) {
		
		
		
		return null;
		
	} 

}
