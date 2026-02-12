package com.shyampatel.myprofileapp.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findMessagesByCreatedAtAfter(Instant createdAtAfter);

    List<Message> findMessagesByCreatedAtAfterAndVisitorAddress(Instant createdAtAfter, String visitorAddress);

    long countMessagesByCreatedAtAfterAndVisitorAddress(Instant createdAtAfter, String visitorAddress);
}
