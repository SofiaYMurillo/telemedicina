import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth.guard';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TriageFormComponent } from './triage/triage-form/triage-form.component';
import { TriageHistoryComponent } from './triage/triage-history/triage-history.component';
import { AppointmentCreateComponent } from './appointments/appointment-create/appointment-create.component';
import { AppointmentListComponent } from './appointments/appointment-list/appointment-list.component';
import { AdminDoctorReportComponent } from './admin/admin-doctor-report.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'triage', component: TriageFormComponent, canActivate: [authGuard, roleGuard(['PATIENT'])] },
  { path: 'triage/history', component: TriageHistoryComponent, canActivate: [authGuard, roleGuard(['PATIENT'])] },
  { path: 'appointments/new', component: AppointmentCreateComponent, canActivate: [authGuard, roleGuard(['PATIENT'])] },
  { path: 'appointments', component: AppointmentListComponent, canActivate: [authGuard] },
  { path: 'admin/report', component: AdminDoctorReportComponent, canActivate: [authGuard, roleGuard(['ADMIN'])] },
  { path: '**', redirectTo: 'dashboard' }
];
