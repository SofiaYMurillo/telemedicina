package com.healthtech.meditriage.appointment;

import com.healthtech.meditriage.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientOrderByScheduledAtDesc(User patient);
    List<Appointment> findByDoctor_UserOrderByScheduledAtDesc(User doctorUser);
}
