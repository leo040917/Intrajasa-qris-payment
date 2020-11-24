package com.qris.payment.repo;

import org.springframework.data.repository.CrudRepository;

import com.qris.payment.model.JsonHistoryClient;

public interface JsonHistoryClienRepo extends CrudRepository<JsonHistoryClient, Long>{

}
