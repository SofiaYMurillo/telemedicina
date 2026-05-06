package com.healthtech.meditriage.appointment;

import com.healthtech.meditriage.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(
            @AuthenticationPrincipal User patient,
            @Valid @RequestBody AppointmentRequest request
    ) {
        return appointmentService.create(patient, request);
    }

    @GetMapping("/my")
    public List<AppointmentResponse> findMine(@AuthenticationPrincipal User user) {
        return appointmentService.findMine(user);
    }

    @GetMapping("/{id}")
    public AppointmentResponse findOne(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return appointmentService.findOne(user, id);
    }

    @PatchMapping("/{id}/status")
    public AppointmentResponse updateStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusUpdateRequest request
    ) {
        return appointmentService.updateStatus(user, id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@AuthenticationPrincipal User user, @PathVariable Long id) {
        appointmentService.cancel(user, id);
    }
}
