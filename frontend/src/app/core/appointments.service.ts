import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from './environment';
import { AppointmentRequest, AppointmentResponse, AppointmentStatus } from './models';

@Injectable({ providedIn: 'root' })
export class AppointmentsService {
  private http = inject(HttpClient);

  create(payload: AppointmentRequest) {
    return this.http.post<AppointmentResponse>(`${environment.apiUrl}/appointments`, payload);
  }

  findMine() {
    return this.http.get<AppointmentResponse[]>(`${environment.apiUrl}/appointments/my`);
  }

  updateStatus(id: number, status: AppointmentStatus) {
    return this.http.patch<AppointmentResponse>(`${environment.apiUrl}/appointments/${id}/status`, { status });
  }

  cancel(id: number) {
    return this.http.delete<void>(`${environment.apiUrl}/appointments/${id}`);
  }
}
