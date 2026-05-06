package com.healthtech.meditriage.appointment;

import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateRequest(
        @NotNull(message = "El estado es obligatorio")
        AppointmentStatus status
) {
}
