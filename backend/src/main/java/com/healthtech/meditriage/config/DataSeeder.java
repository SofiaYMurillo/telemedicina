package com.healthtech.meditriage.config;

import com.healthtech.meditriage.doctor.Doctor;
import com.healthtech.meditriage.doctor.DoctorRepository;
import com.healthtech.meditriage.user.Role;
import com.healthtech.meditriage.user.User;
import com.healthtech.meditriage.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedData(UserRepository userRepository, DoctorRepository doctorRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            User patient = createUserIfMissing(userRepository, passwordEncoder,
                    "Paciente", "patient@meditriage.com", "Patient123*", Role.PATIENT);
            createUserIfMissing(userRepository, passwordEncoder,
                    "Admin ", "admin@meditriage.com", "Admin123*", Role.ADMIN);

            User cardio = createUserIfMissing(userRepository, passwordEncoder,
                    "Dra. Laura Cardona", "cardio@meditriage.com", "Doctor123*", Role.DOCTOR);
            User general = createUserIfMissing(userRepository, passwordEncoder,
                    "Dr. Miguel Torres", "general@meditriage.com", "Doctor123*", Role.DOCTOR);
            User pediatria = createUserIfMissing(userRepository, passwordEncoder,
                    "Dra. Sofia Rojas", "pediatria@meditriage.com", "Doctor123*", Role.DOCTOR);
            User neurologia = createUserIfMissing(userRepository, passwordEncoder,
                    "Dr. Andres Mejia", "neurologia@meditriage.com", "Doctor123*", Role.DOCTOR);
            User dermatologia = createUserIfMissing(userRepository, passwordEncoder,
                    "Dra. Valeria Navas", "dermatologia@meditriage.com", "Doctor123*", Role.DOCTOR);

            createDoctorIfMissing(doctorRepository, cardio, "Cardiologia", "MED-CARD-001");
            createDoctorIfMissing(doctorRepository, general, "Medicina General", "MED-GEN-001");
            createDoctorIfMissing(doctorRepository, pediatria, "Pediatria", "MED-PED-001");
            createDoctorIfMissing(doctorRepository, neurologia, "Neurologia", "MED-NEU-001");
            createDoctorIfMissing(doctorRepository, dermatologia, "Dermatologia", "MED-DER-001");

            patient.getEmail();
        };
    }

    private User createUserIfMissing(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String name,
            String email,
            String rawPassword,
            Role role
    ) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(name, email, passwordEncoder.encode(rawPassword), role)));
    }

    private void createDoctorIfMissing(DoctorRepository doctorRepository, User user, String specialty, String licenseNumber) {
        doctorRepository.findByLicenseNumber(licenseNumber)
                .orElseGet(() -> doctorRepository.save(new Doctor(user, specialty, licenseNumber, true)));
    }
}
