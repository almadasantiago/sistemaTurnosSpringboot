import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AppointmentResponse,
  CreateAppointmentRequest,
  UpdateAppointmentRequest,
  CancelAppointmentRequest
} from '../models/appointment.model';

@Injectable({ providedIn: 'root' })
export class AppointmentService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/appointments`;

  private readonly _appointments = signal<AppointmentResponse[]>([]);
  readonly appointments = this._appointments.asReadonly();


  readonly pendingAppointments = computed(() =>
    this._appointments().filter(a => a.status === 'PENDING')
  );

  readonly completedAppointments = computed(() =>
    this._appointments().filter(a => a.status === 'COMPLETED')
  );

  getAll() {
    return this.http.get<AppointmentResponse[]>(this.baseUrl).pipe(
      tap(appointments => this._appointments.set(appointments))
    );
  }

  getById(id: number) {
    return this.http.get<AppointmentResponse>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateAppointmentRequest) {
    return this.http.post<AppointmentResponse>(this.baseUrl, request).pipe(
      tap(created => {
        this._appointments.update(appointments => [...appointments, created]);
      })
    );
  }

  update(id: number, request: UpdateAppointmentRequest) {
    return this.http.put<AppointmentResponse>(`${this.baseUrl}/${id}`, request).pipe(
      tap(updated => {
        this._appointments.update(appointments =>
          appointments.map(a => a.id === id ? updated : a)
        );
      })
    );
  }

  complete(id: number) {
    return this.http.patch<AppointmentResponse>(`${this.baseUrl}/${id}/complete`, {}).pipe(
      tap(updated => {
        this._appointments.update(appointments =>
          appointments.map(a => a.id === id ? updated : a)
        );
      })
    );
  }

  
  cancel(id: number, request: CancelAppointmentRequest) {
    return this.http.patch<AppointmentResponse>(`${this.baseUrl}/${id}/cancel`, request).pipe(
      tap(updated => {
        this._appointments.update(appointments =>
          appointments.map(a => a.id === id ? updated : a)
        );
      })
    );
  }
}