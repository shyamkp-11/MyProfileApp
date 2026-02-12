package com.shyampatel.myprofileapp.message;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void save(Message message) {
        messageRepository.save(message);
    }

    public long countMessagesFromVistorSince24Hours(String clientIpAddress) {
        return messageRepository.countMessagesByCreatedAtAfterAndVisitorAddress(LocalDateTime.now(ZoneOffset.UTC).minusDays(1).toInstant(ZoneOffset.UTC), clientIpAddress);
    }
}
