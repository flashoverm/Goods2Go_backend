package com.goods2go.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.NotificationMessage;

public interface NotificationMessageDao extends CrudRepository<NotificationMessage, Long> {

	public List<NotificationMessage> findByRecipient(String recipient);

}
