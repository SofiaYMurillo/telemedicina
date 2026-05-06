package com.healthtech.meditriage.appointment;

import java.time.Instant;
import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        String doctorSpecialty,
        LocalDateTime scheduledAt,
        String reason,
        AppointmentStatus status,
        Instant createdAt
) {
    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getUser().getName(),
                appointment.getDoctor().getSpecialty(),
                appointment.getScheduledAt(),
                appointment.getReason(),
                appointment.getStatus(),
                appointment.getCreatedAt()
        );
    }
}
