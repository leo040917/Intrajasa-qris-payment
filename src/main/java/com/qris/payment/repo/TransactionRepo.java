package com.qris.payment.repo;

import org.springframework.data.repository.CrudRepository;

import com.qris.payment.model.JsonHistoryPatner;
import com.qris.payment.model.TransactionQris;

public interface TransactionRepo extends CrudRepository<TransactionQris, Long> {
	
	TransactionQris findByJsonHistoryPatner(JsonHistoryPatner historyPatner);

}
