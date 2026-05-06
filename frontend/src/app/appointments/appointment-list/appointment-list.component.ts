import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AppointmentsService } from '../../core/appointments.service';
import { AuthService } from '../../core/auth.service';
import { AppointmentResponse, AppointmentStatus } from '../../core/models';

@Component({
  selector: 'app-appointment-list',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './appointment-list.component.html'
})
export class AppointmentListComponent implements OnInit {
  auth = inject(AuthService);
  private appointmentsService = inject(AppointmentsService);

  appointments = signal<AppointmentResponse[]>([]);
  error = signal('');
  loading = signal(false);

  isPatient = computed(() => this.auth.user()?.role === 'PATIENT');
  isDoctor = computed(() => this.auth.user()?.role === 'DOCTOR');
  isAdmin = computed(() => this.auth.user()?.role === 'ADMIN');

  pageTitle = computed(() => {
    if (this.isDoctor()) {
      return 'Pacientes asignados';
    }
    if (this.isAdmin()) {
      return 'Citas del sistema';
    }
    return 'Mis citas';
  });

  subtitle = computed(() => {
    if (this.isDoctor()) {
      return 'Aqui ves a los pacientes que esperan aprobacion, las citas ya aprobadas y el historial ya atendido.';
    }
    if (this.isAdmin()) {
      return 'Aqui puedes revisar citas pendientes de cualquier doctor y cancelar las que aun no han sido aprobadas.';
    }
    return 'Elige doctor, consulta tus citas y cancelalas si aun no han sido atendidas.';
  });

  pendingAppointments = computed(() =>
    this.appointments().filter((item) => item.status === 'PENDING')
  );

  confirmedAppointments = computed(() =>
    this.appointments().filter((item) => item.status === 'CONFIRMED')
  );

  completedAppointments = computed(() =>
    this.appointments().filter((item) => item.status === 'COMPLETED')
  );

  otherAppointments = computed(() =>
    this.appointments().filter((item) => item.status === 'CANCELLED')
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
        this.error.set(err.error?.details?.[0] ?? 'No se pudieron cargar las citas');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false)
    });
  }

  confirm(id: number): void {
    this.appointmentsService.updateStatus(id, 'CONFIRMED').subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(err.error?.details?.[0] ?? 'No se pudo confirmar la cita')
    });
  }

  complete(id: number): void {
    this.appointmentsService.updateStatus(id, 'COMPLETED').subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(err.error?.details?.[0] ?? 'No se pudo completar la cita')
    });
  }

  cancel(id: number): void {
    this.appointmentsService.cancel(id).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(err.error?.details?.[0] ?? 'No se pudo cancelar la cita')
    });
  }

  canPatientCancel(item: AppointmentResponse): boolean {
    return this.isPatient() && (item.status === 'PENDING' || item.status === 'CONFIRMED');
  }

  canAdminCancel(item: AppointmentResponse): boolean {
    return this.isAdmin() && item.status === 'PENDING';
  }
}
