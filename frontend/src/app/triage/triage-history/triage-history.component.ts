import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TriageResponse } from '../../core/models';
import { TriageService } from '../../core/triage.service';

@Component({
  selector: 'app-triage-history',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './triage-history.component.html'
})
export class TriageHistoryComponent implements OnInit {
  private triageService = inject(TriageService);

  items = signal<TriageResponse[]>([]);
  error = signal('');
  loading = signal(false);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set('');
    this.triageService.history().subscribe({
      next: (items) => this.items.set(items),
      error: (err) => {
        this.error.set(err.error?.details?.[0] ?? 'No se pudo cargar el historial');
        this.loading.set(false);
      },
      complete: () => this.loading.set(false)
    });
  }
}
