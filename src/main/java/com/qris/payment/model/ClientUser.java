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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;


import lombok.Data;


@Entity
@Table(name = "clientuser")
@Data
public class ClientUser  implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@JsonIgnore
	@Column
	private String firsname;
	@JsonIgnore
	@Column
	private String lastname;
	@JsonIgnore
	@NaturalId
	@NotBlank
	@Size(max = 50)
	@Email
	private String email;
	@Column
	@JsonIgnore
	private String phone;
	@Column
	@JsonIgnore
	@Size(max = 250)
	private String address;
	@Column
	private String merchandcode;
	@Column
	private String merchankey;
	@Column

	private String username;
	
	@Column	
	@JsonIgnore
	private String passwordencode;
	@Column	
	private String passworddecode;
	@Column
	@JsonIgnore
	private String urlBackend;
	@Column
	@JsonIgnore
	private String urlWebSite;
	@Column	
	@JsonIgnore
	private Date create_date;
	@Column	
	@JsonIgnore
	private Date update_date;
	@JsonIgnore
	@Column	
	private String create_user;
	
	@Column
	@JsonIgnore
	private String update_user;
	
	
	@Column
	@JsonIgnore
	private String ipaddress;
	
	
	@Column
	@JsonIgnore
	private String encodesingn;
	
	@JsonIgnore
	@OneToMany
	@JoinColumn(name = "clientuser_id") // we need to duplicate the physical information
	private List<TransactionQris> transaction;
	

}
