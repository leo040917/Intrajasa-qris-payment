package com.qris.payment.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.qris.payment.model.ClientUser;
import com.qris.payment.plugin.CryptoIntra;
import com.qris.payment.repo.ClienRepo;

import ch.qos.logback.core.net.server.Client;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private ClienRepo clienRepo;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Autowired
	CryptoIntra cryptoIntra;

	public UserDetails loadUserByUsername(String username) {
		ClientUser users = clienRepo.findByUsername(username); 
		if (username == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(users.getUsername(), users.getPasswordencode(),
				new ArrayList<>());
	}

	

	public ClientUser save(JsonNode user, String clientIpAddress) {
		// TODO Auto-generated method stub
		ClientUser clientUser = new ClientUser();
		Base64 base64 = new Base64();
		clientUser.setFirsname(user.get("firstname").asText());
		clientUser.setLastname(user.get("lastname").asText());
		clientUser.setEmail(user.get("email").asText());
		clientUser.setPhone(user.get("phone").asText());
		clientUser.setAddress(user.get("address").asText());
		List<ClientUser> listuser= (List<ClientUser>) clienRepo.findAll();
		clientUser.setMerchandcode( "ITS" +String.format("%04d", listuser.size()+1));
		clientUser.setMerchankey(cryptoIntra.randomAlphanum(clientUser.getMerchandcode()+"0123456789", 6));
		clientUser.setUsername(clientUser.getLastname()+"_" + cryptoIntra.randomAlphanum(clientUser.getLastname()+clientUser.getEmail().replace(".", ""), 4));
		clientUser.setPassworddecode(cryptoIntra.randomAlphanum("01234567890qwertyuioasdfghjklzxcvbnm", 6).replace("_", ""));
		clientUser.setPasswordencode(bcryptEncoder.encode(clientUser.getPassworddecode()));
		clientUser.setUrlBackend(user.get("urlBackend").asText());
		clientUser.setUrlWebSite(user.get("website").asText());
		String bases = clientUser.getUsername() + ":" + clientUser.getPassworddecode();
		String encodedString = new String(base64.encode(bases.getBytes()));
		clientUser.setCreate_date(new Date());
		clientUser.setUpdate_date(null);
		clientUser.setCreate_user(clientIpAddress);
		clientUser.setUpdate_user(null);

		clientUser.setEncodesingn(encodedString);
		clientUser.setIpaddress(clientIpAddress);
		return clienRepo.save(clientUser);
	}

	public ClientUser check(String authorization) {
		// TODO Auto-generated method stub
		ClientUser clientUserModel= clienRepo.findByEncodesingn(authorization);
		return clientUserModel;
	}


}
