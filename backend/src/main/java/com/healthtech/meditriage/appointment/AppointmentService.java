package com.healthtech.meditriage.appointment;

import com.healthtech.meditriage.doctor.Doctor;
import com.healthtech.meditriage.doctor.DoctorRepository;
import com.healthtech.meditriage.user.Role;
import com.healthtech.meditriage.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public AppointmentResponse create(User patient, AppointmentRequest request) {
        if (patient.getRole() != Role.PATIENT) {
            throw new IllegalArgumentException("Solo los pacientes pueden agendar citas");
        }

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor no encontrado"));
        if (!doctor.isAvailable()) {
            throw new IllegalArgumentException("El doctor seleccionado no esta disponible");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setScheduledAt(request.scheduledAt());
        appointment.setReason(request.reason().trim());
        appointment.setStatus(AppointmentStatus.PENDING);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findMine(User user) {
        if (user.getRole() == Role.ADMIN) {
            return appointmentRepository.findAll().stream()
                    .map(AppointmentResponse::from)
                    .toList();
        }
        if (user.getRole() == Role.DOCTOR) {
            return appointmentRepository.findByDoctor_UserOrderByScheduledAtDesc(user).stream()
                    .map(AppointmentResponse::from)
                    .toList();
        }
        return appointmentRepository.findByPatientOrderByScheduledAtDesc(user).stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse findOne(User user, Long id) {
        Appointment appointment = getAppointmentForUser(user, id);
        return AppointmentResponse.from(appointment);
    }

    @Transactional
    public AppointmentResponse updateStatus(User user, Long id, AppointmentStatus status) {
        Appointment appointment = getAppointmentForUser(user, id);

        if (user.getRole() != Role.DOCTOR) {
            throw new IllegalArgumentException("Solo el doctor asignado puede aprobar o completar citas");
        }
        if (!appointment.getDoctor().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Solo el doctor asignado puede cambiar el estado de esta cita");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("La cita fue cancelada y ya no puede aprobarse");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("La cita ya fue completada");
        }

        if (status == AppointmentStatus.CONFIRMED) {
            if (appointment.getStatus() != AppointmentStatus.PENDING) {
                throw new IllegalArgumentException("Solo las citas pendientes pueden confirmarse");
            }
        } else if (status == AppointmentStatus.COMPLETED) {
            if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
                throw new IllegalArgumentException("Solo las citas confirmadas pueden marcarse como completadas");
            }
        } else {
            throw new IllegalArgumentException("Estado no permitido para actualizacion manual");
        }

        appointment.setStatus(status);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional
    public void cancel(User user, Long id) {
        Appointment appointment = getAppointmentForUser(user, id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("La cita ya esta cancelada");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("No se puede cancelar una cita ya atendida");
        }

        if (user.getRole() == Role.PATIENT) {
            if (!appointment.getPatient().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Solo el paciente propietario puede cancelar esta cita");
            }
        } else if (user.getRole() == Role.ADMIN) {
            if (appointment.getStatus() != AppointmentStatus.PENDING) {
                throw new IllegalArgumentException("El administrador solo puede cancelar citas pendientes");
            }
        } else {
            throw new IllegalArgumentException("Solo el paciente o el administrador pueden cancelar citas");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private Appointment getAppointmentForUser(User user, Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        boolean isPatientOwner = appointment.getPatient().getId().equals(user.getId());
        boolean isAssignedDoctor = appointment.getDoctor().getUser().getId().equals(user.getId());

        if (user.getRole() == Role.ADMIN || isPatientOwner || isAssignedDoctor) {
            return appointment;
        }
        throw new IllegalArgumentException("No puedes consultar o modificar una cita ajena");
    }
}
