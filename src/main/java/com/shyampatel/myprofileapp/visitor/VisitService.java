package com.shyampatel.myprofileapp.visitor;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class VisitService {
    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public void save(Visit visit) {
        visitRepository.save(visit);
    }

    public List<Visit> getVisitWithTimeGreaterThan(String visitorAddress, Instant visitTime) {
        return visitRepository.getVisitsByVisitTimeGreaterThanAndVisitorAddress(visitTime, visitorAddress);
    }
}
