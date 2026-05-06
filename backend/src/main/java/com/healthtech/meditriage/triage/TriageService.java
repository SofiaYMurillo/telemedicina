package com.healthtech.meditriage.triage;

import com.healthtech.meditriage.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class TriageService {
    private final TriageRepository triageRepository;

    public TriageService(TriageRepository triageRepository) {
        this.triageRepository = triageRepository;
    }

    @Transactional
    public TriageResponse evaluate(User patient, TriageRequest request) {
        List<String> normalizedSymptoms = request.symptoms().stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .distinct()
                .toList();

        if (normalizedSymptoms.isEmpty()) {
            throw new IllegalArgumentException("Debes enviar al menos un síntoma válido");
        }

        RiskAssessment assessment = assessRisk(normalizedSymptoms, request.temperature(), request.age(), request.durationDays());

        TriageRecord record = new TriageRecord();
        record.setPatient(patient);
        record.setSymptoms(normalizedSymptoms);
        record.setTemperature(request.temperature());
        record.setAge(request.age());
        record.setDurationDays(request.durationDays());
        record.setRiskLevel(assessment.riskLevel());
        record.setPriority(assessment.priority());
        record.setRecommendation(assessment.recommendation());
        record.setSuggestedSpecialty(assessment.suggestedSpecialty());

        return TriageResponse.from(triageRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<TriageResponse> history(User patient) {
        return triageRepository.findByPatientOrderByCreatedAtDesc(patient)
                .stream()
                .map(TriageResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TriageResponse findByIdForPatient(User patient, Long id) {
        TriageRecord record = triageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro de triaje no encontrado"));
        if (!record.getPatient().getId().equals(patient.getId())) {
            throw new IllegalArgumentException("No puedes consultar un triaje de otro paciente");
        }
        return TriageResponse.from(record);
    }

    private RiskAssessment assessRisk(List<String> symptoms, Double temperature, Integer age, Integer durationDays) {
        boolean respiratoryEmergency = containsAny(symptoms,
                "dificultad para respirar", "falta de aire", "ahogo", "respirar");
        boolean chestPain = containsAny(symptoms, "dolor de pecho", "opresion en el pecho", "pecho");
        boolean neurological = containsAny(symptoms, "confusion", "desmayo", "convulsiones", "paralisis");
        boolean severeBleeding = containsAny(symptoms, "sangrado abundante", "hemorragia");
        boolean highFever = temperature != null && temperature >= 39.0;
        boolean vulnerable = age != null && (age < 5 || age >= 65);
        boolean prolonged = durationDays != null && durationDays >= 3;

        if (respiratoryEmergency || chestPain || neurological || severeBleeding || (highFever && vulnerable)) {
            return new RiskAssessment(
                    RiskLevel.HIGH,
                    1,
                    "Prioridad alta: se recomienda atención médica urgente o servicio de emergencias.",
                    chestPain ? "Urgencias / Cardiología" : "Urgencias / Medicina General"
            );
        }

        if (highFever || prolonged || containsAny(symptoms, "dolor intenso", "vomito persistente", "deshidratacion")) {
            return new RiskAssessment(
                    RiskLevel.MEDIUM,
                    2,
                    "Prioridad media: agenda una teleconsulta lo antes posible y monitorea los síntomas.",
                    "Medicina General"
            );
        }

        return new RiskAssessment(
                RiskLevel.LOW,
                3,
                "Prioridad baja: puedes iniciar autocuidado básico y agendar una consulta si los síntomas persisten.",
                "Medicina General"
        );
    }

    private boolean containsAny(List<String> symptoms, String... keywords) {
        for (String symptom : symptoms) {
            for (String keyword : keywords) {
                if (symptom.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    private record RiskAssessment(
            RiskLevel riskLevel,
            Integer priority,
            String recommendation,
            String suggestedSpecialty
    ) {
    }
}
