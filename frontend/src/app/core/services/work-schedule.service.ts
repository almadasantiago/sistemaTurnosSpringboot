import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  WorkScheduleResponse,
  CreateWorkScheduleRequest,
  UpdateWorkScheduleRequest
} from '../models/work-schedule.model';

@Injectable({ providedIn: 'root' })
export class WorkScheduleService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/work-schedules`;

  private readonly _schedules = signal<WorkScheduleResponse[]>([]);
  readonly schedules = this._schedules.asReadonly();

  getByBarber(barberId: number) {
    return this.http.get<WorkScheduleResponse[]>(`${this.baseUrl}/barber/${barberId}`).pipe(
      tap(schedules => this._schedules.set(schedules))
    );
  }

  create(request: CreateWorkScheduleRequest) {
    return this.http.post<WorkScheduleResponse>(this.baseUrl, request).pipe(
      tap(created => {
        this._schedules.update(schedules => [...schedules, created]);
      })
    );
  }

  update(id: number, request: UpdateWorkScheduleRequest) {
    return this.http.put<WorkScheduleResponse>(`${this.baseUrl}/${id}`, request).pipe(
      tap(updated => {
        this._schedules.update(schedules =>
          schedules.map(s => s.id === id ? updated : s)
        );
      })
    );
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        this._schedules.update(schedules =>
          schedules.filter(s => s.id !== id)
        );
      })
    );
  }
}