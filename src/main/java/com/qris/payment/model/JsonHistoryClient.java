package com.qris.payment.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
@Entity
@Table(name = "jsonhistoryclient")
public class JsonHistoryClient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    @Column
	private  String Transaksi_Id;
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
	private String responsecode;
	@Column
	private String responsemessage;
}
