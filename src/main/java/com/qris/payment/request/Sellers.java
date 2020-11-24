package com.qris.payment.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Sellers {
	private Long Id;
	private String Name;
	private String url; 
	private String SellerIdNumber;
	private String Email;
	private DataCustomer address;

}
