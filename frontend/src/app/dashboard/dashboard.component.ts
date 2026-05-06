import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent {
  auth = inject(AuthService);

  roleLabel = computed(() => {
    const role = this.auth.user()?.role;
    if (role === 'DOCTOR') {
      return 'Doctor';
    }
    if (role === 'ADMIN') {
      return 'Administrador';
    }
    return 'Paciente';
  });

  welcomeCopy = computed(() => {
    const role = this.auth.user()?.role;
    if (role === 'DOCTOR') {
      return 'Aqui puedes revisar pacientes pendientes, aprobar citas y mantener separado el historial de atenciones completadas.';
    }
    if (role === 'ADMIN') {
      return 'Aqui puedes auditar el trabajo de cada doctor, revisar citas pendientes y cancelar las que aun no han sido aprobadas.';
    }
    return 'Aqui puedes completar el triaje, elegir doctor y gestionar tus propias citas de telemedicina.';
  });
}
