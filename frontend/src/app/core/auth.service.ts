import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { tap } from 'rxjs';
import { environment } from './environment';
import { AuthResponse, Role, UserResponse } from './models';
import { TokenService } from './token.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private tokenService = inject(TokenService);
  private tokenSignal = signal<string | null>(this.tokenService.getToken());
  private userSignal = signal<UserResponse | null>(this.tokenService.getUser<UserResponse>());

  user = computed(() => this.userSignal());
  isAuthenticated = computed(() => !!this.tokenSignal());

  login(email: string, password: string) {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, { email, password })
      .pipe(tap((res) => this.setSession(res)));
  }

  register(name: string, email: string, password: string, role: Role = 'PATIENT') {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, { name, email, password, role })
      .pipe(tap((res) => this.setSession(res)));
  }

  me() {
    return this.http.get<UserResponse>(`${environment.apiUrl}/auth/me`)
      .pipe(tap((user) => this.userSignal.set(user)));
  }

  logout(): void {
    this.tokenService.clear();
    this.tokenSignal.set(null);
    this.userSignal.set(null);
  }

  private setSession(response: AuthResponse): void {
    this.tokenService.setSession(response.token, response.user);
    this.tokenSignal.set(response.token);
    this.userSignal.set(response.user);
  }
}
