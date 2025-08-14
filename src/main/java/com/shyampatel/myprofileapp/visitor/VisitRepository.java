package com.shyampatel.myprofileapp.visitor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, VisitId> {
    List<Visit> getVisitsByVisitTimeGreaterThanAndVisitorAddress(Instant visitTimeIsGreaterThan, String visitorAddress);
}
