package com.shyampatel.myprofileapp.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.ToString;

@ToString
public class MessageRequest{

    @NotNull(message = "is required")
    @Size(min = 2, max = 30, message = "is required")
    private String visitorName;
    @NotNull(message = "is required")
    @Email(message = "Please add a valid email to get back")
    private String visitorEmail;
    private String subject;
    @NotNull
    @Size(min = 2, max = 300, message = "is required")
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
}