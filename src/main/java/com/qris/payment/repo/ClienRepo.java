package com.qris.payment.repo;

import org.springframework.data.repository.CrudRepository;

import com.qris.payment.model.ClientUserModel;

public interface ClienRepo extends CrudRepository<ClientUserModel, Long> {

}
