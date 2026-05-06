package com.healthtech.meditriage.triage;

import com.healthtech.meditriage.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TriageRepository extends JpaRepository<TriageRecord, Long> {
    List<TriageRecord> findByPatientOrderByCreatedAtDesc(User patient);
}
