package com.healthtech.meditriage.doctor;

import com.healthtech.meditriage.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByAvailableTrueOrderBySpecialtyAsc();
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    Optional<Doctor> findByUser(User user);
}
