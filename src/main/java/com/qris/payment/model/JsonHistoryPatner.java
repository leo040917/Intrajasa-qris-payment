package com.qris.payment.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
@Data
@Entity
@Table(name = "jsonhistorypatner")
public class JsonHistoryPatner implements Serializable {
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    
	@Column
	private  String refno;
	
	@Column
	@Length(max = 7000)
	private  String requestmsg;
	
	@Column
	private Date requestTime;
	
	@Column
	@Length(max = 7000)
	private  String responsemsg;
	@Column
	private Date responseTime;
	
	@Column
	private String totalTime_rqs_rps;
		
	@Column
	@Length(max = 7000)
	private  String callBackend;

	@Column
	private Date callbackTime;
	@Column
	private String responsecode;
	@Column
	private String responsemessage;
	
	@Column
	private int returnRequest;
	
	@Column
	private int returncallbackend;
}
