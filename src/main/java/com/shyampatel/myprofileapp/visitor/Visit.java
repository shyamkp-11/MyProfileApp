package com.shyampatel.myprofileapp.visitor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name="visit")
@ToString
@IdClass(VisitId.class)
public class Visit {
    @Id
    private String visitorAddress;
    @Id
    private Instant visitTime;

    private String userAgent;

    public Visit(String visitorAddress, Instant visitTime, String userAgent) {
        this.visitorAddress = visitorAddress;
        this.visitTime = visitTime;
        this.userAgent = userAgent;
    }

    public Visit() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Visit visit = (Visit) o;
        return Objects.equals(visitorAddress, visit.visitorAddress) && Objects.equals(visitTime, visit.visitTime) && Objects.equals(userAgent, visit.userAgent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitorAddress, visitTime, userAgent);
    }

    public static class Builder {

        private String visitorAddress;
        private Instant visitTime;
        private String userAgent;

        public Builder setVisitorAddress(String visitorAddress) {
            this.visitorAddress = visitorAddress;
            return this;
        }

        public Builder setVisitTime(Instant visitTime) {
            this.visitTime = visitTime;
            return this;
        }
        public Builder setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Visit build() {
            return new Visit(visitorAddress, visitTime, userAgent);
        }
    }
}
