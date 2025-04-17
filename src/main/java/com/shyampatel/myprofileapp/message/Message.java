package com.shyampatel.myprofileapp.message;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;


/*
  Not using lombok as it showing error when shown in the html
 */
@Entity
@Table(name="message")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @NotNull(message = "is required")
    @Column(name = "visitor_name", nullable = false)
    @Size(min = 2, max = 30, message = "is required")
    private String visitorName;

    @NotNull(message = "is required")
    @Column(name = "visitor_email", nullable = false)
    @Email(message = "Please add a valid email to get back")
    private String visitorEmail;

    @Column(name = "title")
    private String subject;

    @NotNull
    @Size(min = 2, max = 300, message = "is required")
    @Column(name = "message", nullable = false)
    private String message;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

}
