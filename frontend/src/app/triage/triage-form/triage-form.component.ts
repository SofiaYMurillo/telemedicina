import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TriageResponse } from '../../core/models';
import { TriageService } from '../../core/triage.service';

@Component({
  selector: 'app-triage-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './triage-form.component.html'
})
export class TriageFormComponent {
  private triageService = inject(TriageService);

  symptomsText = 'fiebre, dificultad para respirar';
  temperature: number | null = 39.1;
  age: number | null = 62;
  durationDays: number | null = 2;

  result = signal<TriageResponse | null>(null);
  error = signal('');
  loading = signal(false);

  submit(): void {
    const symptoms = this.symptomsText.split(',').map((s) => s.trim()).filter(Boolean);
    this.loading.set(true);
    this.error.set('');
    this.result.set(null);

    this.triageService.evaluate({
      symptoms,
      temperature: this.temperature,
      age: this.age,
      durationDays: this.durationDays
    }).subscribe({
      next: (res) => this.result.set(res),
      error: (err) => {
        this.error.set(err.error?.details?.[0] ?? 'No se pudo evaluar el triaje');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false)
    });
  }
}
