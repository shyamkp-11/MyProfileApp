package com.shyampatel.myprofileapp.visitor;

import jakarta.persistence.Id;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class VisitId implements Serializable {
    private String visitorAddress;
    private Instant visitTime;

    public VisitId() {

    }

    public VisitId(String visitorAddress, Instant visitTime) {
        this.visitorAddress = visitorAddress;
        this.visitTime = visitTime;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VisitId visitId = (VisitId) o;
        return Objects.equals(visitorAddress, visitId.visitorAddress) && Objects.equals(visitTime, visitId.visitTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitorAddress, visitTime);
    }
}
