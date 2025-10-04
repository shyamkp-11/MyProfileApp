package com.shyampatel.myprofileapp.visitor;

import jakarta.persistence.Id;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class VisitId implements Serializable {
    private String visitorAddress;
    private Instant visitTime;
    private String sessionId;
    private Integer visitCount;

    public VisitId() {

    }

    public VisitId(String visitorAddress, Instant visitTime, String sessionId, Integer visitCount) {
        this.visitorAddress = visitorAddress;
        this.visitTime = visitTime;
        this.sessionId = sessionId;
        this.visitCount = visitCount;
    }

    public String getVisitorAddress() {
        return visitorAddress;
    }

    public void setVisitorAddress(String visitorAddress) {
        this.visitorAddress = visitorAddress;
    }

    public Instant getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Instant visitTime) {
        this.visitTime = visitTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VisitId visitId = (VisitId) o;
        return Objects.equals(visitorAddress, visitId.visitorAddress) && Objects.equals(visitTime, visitId.visitTime) && Objects.equals(sessionId, visitId.sessionId) && Objects.equals(visitCount, visitId.visitCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitorAddress, visitTime, sessionId, visitCount);
    }
}
