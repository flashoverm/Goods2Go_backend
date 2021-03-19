package com.goods2go.repositories;

import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.PaymentInformation;

public interface PaymentInformationDao extends CrudRepository<PaymentInformation, Long> {

}
