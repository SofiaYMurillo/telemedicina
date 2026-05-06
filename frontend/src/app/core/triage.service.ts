import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from './environment';
import { TriageRequest, TriageResponse } from './models';

@Injectable({ providedIn: 'root' })
export class TriageService {
  private http = inject(HttpClient);

  evaluate(payload: TriageRequest) {
    return this.http.post<TriageResponse>(`${environment.apiUrl}/triage/evaluate`, payload);
  }

  history() {
    return this.http.get<TriageResponse[]>(`${environment.apiUrl}/triage/history`);
  }
}
