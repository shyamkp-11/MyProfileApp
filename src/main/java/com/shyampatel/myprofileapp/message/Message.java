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
@Builder
@ToString
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorEmail() {
        return visitorEmail;
    }

    public void setVisitorEmail(String visitorEmail) {
        this.visitorEmail = visitorEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Message() {
    }

    public Message(Long id, String visitorName, String visitorEmail, String subject, String message, Instant createdAt) {
        this.id = id;
        this.visitorName = visitorName;
        this.visitorEmail = visitorEmail;
        this.subject = subject;
        this.message = message;
        this.createdAt = createdAt;
    }

    public static class Builder {
        private Long id;
        private String visitorName;
        private String visitorEmail;
        private String subject;
        private String message;
        private Instant createdAt;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setVisitorName(String visitorName) {
            this.visitorName = visitorName;
            return this;
        }

        public Builder setVisitorEmail(String visitorEmail) {
            this.visitorEmail = visitorEmail;
            return this;
        }

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Message build() {
            return new Message(id, visitorName, visitorEmail, subject, message, createdAt);
        }
    }
}
