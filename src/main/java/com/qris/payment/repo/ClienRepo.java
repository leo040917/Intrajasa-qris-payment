package com.qris.payment.repo;

import org.springframework.data.repository.CrudRepository;

import com.qris.payment.model.ClientUser;

public interface ClienRepo extends CrudRepository<ClientUser, Long> {
     ClientUser findByEncodesingn(String base);
	ClientUser findByUsername(String username);
}
