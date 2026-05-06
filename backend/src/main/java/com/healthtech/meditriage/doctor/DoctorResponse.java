package com.healthtech.meditriage.doctor;

public record DoctorResponse(
        Long id,
        String name,
        String email,
        String specialty,
        String licenseNumber,
        boolean available
) {
    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getUser().getName(),
                doctor.getUser().getEmail(),
                doctor.getSpecialty(),
                doctor.getLicenseNumber(),
                doctor.isAvailable()
        );
    }
}
