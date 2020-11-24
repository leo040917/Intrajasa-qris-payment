package com.qris.payment.repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.qris.payment.model.JsonHistoryPatner;
import com.qris.payment.model.TransactionQris;

public interface TransactionRepo extends CrudRepository<TransactionQris, Long> {
	
	TransactionQris findByTransactionNumber(String Tran);
   


}
