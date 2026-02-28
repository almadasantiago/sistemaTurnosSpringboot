import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserResponse, UpdateUserRequest, UpdateProfileRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/users`;

  private readonly _users = signal<UserResponse[]>([]);
  readonly users = this._users.asReadonly();

  // GET solo de Admin
  getAll() {
    return this.http.get<UserResponse[]>(this.baseUrl).pipe(
      tap(users => this._users.set(users))
    );
  }

  getById(id: number) {
    return this.http.get<UserResponse>(`${this.baseUrl}/${id}`);
  }

  update(id: number, request: UpdateUserRequest) {
    return this.http.put<UserResponse>(`${this.baseUrl}/${id}`, request).pipe(
      tap(updated => {
        this._users.update(users =>
          users.map(u => u.id === id ? updated : u)
        );
      })
    );
  }
  
  updateProfile(request: UpdateProfileRequest) {
    return this.http.put<UserResponse>(`${this.baseUrl}/me`, request);
  }
}