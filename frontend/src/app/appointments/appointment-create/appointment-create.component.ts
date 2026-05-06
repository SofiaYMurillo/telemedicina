import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AppointmentsService } from '../../core/appointments.service';
import { AuthService } from '../../core/auth.service';
import { DoctorsService } from '../../core/doctors.service';
import { DoctorResponse } from '../../core/models';

@Component({
  selector: 'app-appointment-create',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './appointment-create.component.html'
})
export class AppointmentCreateComponent implements OnInit {
  private auth = inject(AuthService);
  private doctorsService = inject(DoctorsService);
  private appointmentsService = inject(AppointmentsService);
  private router = inject(Router);

  doctors = signal<DoctorResponse[]>([]);
  canCreate = computed(() => this.auth.user()?.role === 'PATIENT');
  doctorId: number | null = null;
  scheduledAt = '';
  minScheduledAt = new Date(Date.now() + 30 * 60 * 1000).toISOString().slice(0, 16);
  reason = 'Consulta por sintomas persistentes';
  loading = signal(false);
  error = signal('');

  ngOnInit(): void {
    if (!this.canCreate()) {
      this.error.set('Solo los pacientes pueden agendar teleconsultas desde este formulario');
      return;
    }

    this.doctorsService.findAll().subscribe({
      next: (doctors) => {
        this.doctors.set(doctors);
        this.doctorId = doctors[0]?.id ?? null;
      },
      error: (err) => this.error.set(err.error?.details?.[0] ?? 'No se pudieron cargar los doctores')
    });
  }

  submit(): void {
    if (!this.canCreate()) {
      this.error.set('No tienes permisos para agendar citas');
      return;
    }
    if (!this.doctorId) {
      this.error.set('Selecciona un doctor');
      return;
    }
    this.loading.set(true);
    this.error.set('');
    this.appointmentsService.create({
      doctorId: Number(this.doctorId),
      scheduledAt: this.scheduledAt,
      reason: this.reason
    }).subscribe({
      next: () => this.router.navigateByUrl('/appointments'),
      error: (err) => {
        this.error.set(err.error?.details?.[0] ?? 'No se pudo crear la cita');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false)
    });
  }
}
