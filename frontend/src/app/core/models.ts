export type Role = 'PATIENT' | 'DOCTOR' | 'ADMIN';
export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH';
export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  role: Role;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresInMs: number;
  user: UserResponse;
}

export interface DoctorResponse {
  id: number;
  name: string;
  email: string;
  specialty: string;
  licenseNumber: string;
  available: boolean;
}

export interface TriageRequest {
  symptoms: string[];
  temperature?: number | null;
  age?: number | null;
  durationDays?: number | null;
}

export interface TriageResponse {
  id: number;
  symptoms: string[];
  temperature: number | null;
  age: number | null;
  durationDays: number | null;
  riskLevel: RiskLevel;
  priority: number;
  recommendation: string;
  suggestedSpecialty: string;
  disclaimer: string;
  createdAt: string;
}

export interface AppointmentRequest {
  doctorId: number;
  scheduledAt: string;
  reason: string;
}

export interface AppointmentResponse {
  id: number;
  patientId: number;
  patientName: string;
  doctorId: number;
  doctorName: string;
  doctorSpecialty: string;
  scheduledAt: string;
  reason: string;
  status: AppointmentStatus;
  createdAt: string;
}
