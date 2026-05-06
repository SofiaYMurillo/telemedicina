package com.healthtech.meditriage.triage;

import java.time.Instant;
import java.util.List;

public record TriageResponse(
        Long id,
        List<String> symptoms,
        Double temperature,
        Integer age,
        Integer durationDays,
        RiskLevel riskLevel,
        Integer priority,
        String recommendation,
        String suggestedSpecialty,
        String disclaimer,
        Instant createdAt
) {
    public static TriageResponse from(TriageRecord record) {
        return new TriageResponse(
                record.getId(),
                record.getSymptoms(),
                record.getTemperature(),
                record.getAge(),
                record.getDurationDays(),
                record.getRiskLevel(),
                record.getPriority(),
                record.getRecommendation(),
                record.getSuggestedSpecialty(),
                "Este triaje es una orientación educativa y no reemplaza una valoración médica profesional.",
                record.getCreatedAt()
        );
    }
}
