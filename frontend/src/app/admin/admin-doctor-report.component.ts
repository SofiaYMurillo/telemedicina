import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AppointmentsService } from '../core/appointments.service';
import { AppointmentResponse } from '../core/models';

interface DoctorActivity {
  doctorId: number;
  doctorName: string;
  specialty: string;
  completedCount: number;
  pendingCount: number;
  completedAppointments: AppointmentResponse[];
  pendingAppointments: AppointmentResponse[];
}

@Component({
  selector: 'app-admin-doctor-report',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './admin-doctor-report.component.html'
})
export class AdminDoctorReportComponent implements OnInit {
  private appointmentsService = inject(AppointmentsService);

  appointments = signal<AppointmentResponse[]>([]);
  error = signal('');
  loading = signal(false);

  doctorActivity = computed<DoctorActivity[]>(() => {
    const groups = new Map<number, DoctorActivity>();

    for (const appointment of this.appointments()) {
      const current = groups.get(appointment.doctorId) ?? {
        doctorId: appointment.doctorId,
        doctorName: appointment.doctorName,
        specialty: appointment.doctorSpecialty,
        completedCount: 0,
        pendingCount: 0,
        completedAppointments: [],
        pendingAppointments: []
      };

      if (appointment.status === 'COMPLETED') {
        current.completedCount += 1;
        current.completedAppointments.push(appointment);
      }
      if (appointment.status === 'PENDING') {
        current.pendingCount += 1;
        current.pendingAppointments.push(appointment);
      }

      groups.set(appointment.doctorId, current);
    }

    return Array.from(groups.values()).sort((a, b) => a.doctorName.localeCompare(b.doctorName));
  });

  pendingAppointments = computed(() =>
    this.appointments().filter((item) => item.status === 'PENDING')
  );

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    this.appointmentsService.findMine().subscribe({
      next: (items) => this.appointments.set(items),
      error: (err) => {
        this.error.set(err.error?.details?.[0] ?? 'No se pudieron cargar los reportes');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false)
    });
  }

  cancelPending(id: number): void {
    this.appointmentsService.cancel(id).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(err.error?.details?.[0] ?? 'No se pudo cancelar la cita pendiente')
    });
  }
}
