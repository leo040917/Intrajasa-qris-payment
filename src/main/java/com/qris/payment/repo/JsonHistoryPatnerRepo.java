package com.qris.payment.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qris.payment.model.JsonHistoryPatner;

public interface JsonHistoryPatnerRepo extends CrudRepository<JsonHistoryPatner, Long> {
	 @Query(value ="select * from jsonhistorypatner where  to_char(request_time, 'DD-MM-YYYY')=?1", 
			   nativeQuery = true)
	List<JsonHistoryPatner> findByfindNumber(String time);
    
	 JsonHistoryPatner findByRefno(String ref);
	 
}
