import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from './environment';
import { DoctorResponse } from './models';

@Injectable({ providedIn: 'root' })
export class DoctorsService {
  private http = inject(HttpClient);

  findAll() {
    return this.http.get<DoctorResponse[]>(`${environment.apiUrl}/doctors`);
  }
}
