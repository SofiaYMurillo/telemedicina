package com.healthtech.meditriage.doctor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public List<DoctorResponse> findAvailableDoctors() {
        return doctorRepository.findByAvailableTrueOrderBySpecialtyAsc()
                .stream()
                .map(DoctorResponse::from)
                .toList();
    }
}
