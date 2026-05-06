package com.healthtech.meditriage.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull(message = "El doctor es obligatorio")
        Long doctorId,

        @NotNull(message = "La fecha y hora son obligatorias")
        @Future(message = "La cita debe programarse para una fecha futura")
        LocalDateTime scheduledAt,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(max = 500, message = "El motivo no puede superar 500 caracteres")
        String reason
) {
}
