package com.healthtech.meditriage.triage;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TriageRequest(
        @NotEmpty(message = "Debes enviar al menos un síntoma")
        List<String> symptoms,

        @DecimalMin(value = "34.0", message = "La temperatura mínima permitida es 34.0")
        @DecimalMax(value = "43.0", message = "La temperatura máxima permitida es 43.0")
        Double temperature,

        @Min(value = 0, message = "La edad no puede ser negativa")
        @Max(value = 120, message = "La edad máxima permitida es 120")
        Integer age,

        @Min(value = 0, message = "La duración no puede ser negativa")
        @Max(value = 60, message = "La duración máxima permitida es 60 días")
        Integer durationDays
) {
}
