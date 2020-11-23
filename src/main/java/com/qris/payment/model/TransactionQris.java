package com.qris.payment.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
	private  String amount;
	
	
	@Column
	private  TransactionFlex flextrs;
	
	
	@Column
	private  Date createDate;
	
	
	@OneToMany
	@JoinColumn(name = "transactionqris_id") // we need to duplicate the physical information
	private List<JsonHistoryPatner> history;
}
