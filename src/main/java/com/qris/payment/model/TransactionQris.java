package com.qris.payment.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qris.payment.enums.TransactionFlex;
import lombok.Data;

@Entity
@Table(name = "transactionqris")
@Data
public class TransactionQris implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private  String transactionNumber;
		
	
	@Column
	private  String amount;
	
	
	
	
	@Column
	private  TransactionFlex flextrs;
	
	
	@Column
	private  Date createDate;
	
	@JsonIgnore
	@OneToOne
	
	private JsonHistoryPatner jsonHistoryPatner;
	
	
	@Column
	private long clientuser_id;
	
	@Column
	private int returnRequest;
	
	@Column
	private int returncallbackend;
}
