package com.healthtech.meditriage.triage;

import com.healthtech.meditriage.user.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/triage")
public class TriageController {
    private final TriageService triageService;

    public TriageController(TriageService triageService) {
        this.triageService = triageService;
    }

    @PostMapping("/evaluate")
    public TriageResponse evaluate(@AuthenticationPrincipal User patient, @Valid @RequestBody TriageRequest request) {
        return triageService.evaluate(patient, request);
    }

    @GetMapping("/history")
    public List<TriageResponse> history(@AuthenticationPrincipal User patient) {
        return triageService.history(patient);
    }

    @GetMapping("/{id}")
    public TriageResponse findOne(@AuthenticationPrincipal User patient, @PathVariable Long id) {
        return triageService.findByIdForPatient(patient, id);
    }
}
