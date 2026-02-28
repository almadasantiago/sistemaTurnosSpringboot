import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  BarberServiceResponse,
  CreateBarberServiceRequest,
  UpdateBarberServiceRequest
} from '../models/barber-service.model';

@Injectable({ providedIn: 'root' })
export class BarberServiceService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/barber-services`;

  private readonly _services = signal<BarberServiceResponse[]>([]);

  // Solo exponemos los servicios que no están finalizados
  readonly activeServices = this._services.asReadonly();

  getAll() {
    return this.http.get<BarberServiceResponse[]>(this.baseUrl).pipe(
      tap(services => this._services.set(services))
    );
  }

  create(request: CreateBarberServiceRequest) {
    return this.http.post<BarberServiceResponse>(this.baseUrl, request).pipe(
      tap(created => {
        this._services.update(services => [...services, created]);
      })
    );
  }

  update(id: number, request: UpdateBarberServiceRequest) {
    return this.http.put<BarberServiceResponse>(`${this.baseUrl}/${id}`, request).pipe(
      tap(updated => {
        this._services.update(services =>
          services.map(s => s.id === id ? updated : s)
        );
      })
    );
  }


  finish(id: number) {
    return this.http.patch<BarberServiceResponse>(`${this.baseUrl}/${id}/finish`, {}).pipe(
      tap(updated => {
        this._services.update(services =>
          services.map(s => s.id === id ? updated : s)
        );
      })
    );
  }
}