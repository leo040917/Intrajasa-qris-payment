package com.qris.payment.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DataCustomer {
	private String FirstName;
	private String LastName;
	private String Address;
	private String City;
	private String State;
	private String PostalCode;
	private String Phone;
	private String CountryCode;

}
