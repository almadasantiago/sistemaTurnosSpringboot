import { Component, signal, computed, inject, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { AppointmentService } from '../../../core/services/appointment.service';
import { BarberServiceService } from '../../../core/services/barber-service.service';
import { UserService } from '../../../core/services/user.service';
import { AppointmentResponse } from '../../../core/models/appointment.model';
import { BarberServiceResponse } from '../../../core/models/barber-service.model';
import { UserResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './client-dashboard.html',
  styleUrl: './client-dashboard.scss'
})
export class ClientDashboardComponent implements OnInit {

  private readonly auth = inject(AuthService);
  private readonly appointmentService = inject(AppointmentService);
  private readonly barberServiceService = inject(BarberServiceService);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  readonly currentUser = this.auth.currentUser;

  readonly services = signal<BarberServiceResponse[]>([]);
  readonly barbers = signal<UserResponse[]>([]);
  readonly pendingAppointments = signal<AppointmentResponse[]>([]);

  readonly isLoadingServices = signal(false);
  readonly isLoadingBarbers = signal(false);
  readonly isLoadingAppointments = signal(false);
  readonly dropdownOpen = signal(false);

  readonly activeServices = computed(() =>
    this.services().filter(s => !s.finished)
  );

  ngOnInit(): void {
    this.loadServices();
    this.loadBarbers();
    this.loadPendingAppointments();
  }

  toggleDropdown(): void {
    this.dropdownOpen.update(v => !v);
  }

  closeDropdown(): void {
    this.dropdownOpen.set(false);
  }

  openProfile(): void {
    this.closeDropdown();
    this.router.navigate(['/dashboard/client/profile']);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.navbar-right')) {
      this.dropdownOpen.set(false);
    }
  }

  private loadServices(): void {
    this.isLoadingServices.set(true);
    this.barberServiceService.getAll().subscribe({
      next: (data: BarberServiceResponse[]) => {
        this.services.set(data);
        this.isLoadingServices.set(false);
      },
      error: () => this.isLoadingServices.set(false)
    });
  }

  private loadBarbers(): void {
    this.isLoadingBarbers.set(true);
    this.userService.getBarbers().subscribe({
      next: (data: UserResponse[]) => {
        this.barbers.set(data);
        this.isLoadingBarbers.set(false);
      },
      error: () => this.isLoadingBarbers.set(false)
    });
  }

  private loadPendingAppointments(): void {
    this.isLoadingAppointments.set(true);
    this.appointmentService.getMyPendingAppointments().subscribe({
      next: (data: AppointmentResponse[]) => {
        this.pendingAppointments.set(data);
        this.isLoadingAppointments.set(false);
      },
      error: () => this.isLoadingAppointments.set(false)
    });
  }

  formatDateTime(dateTime: string): string {
    const date = new Date(dateTime);
    return date.toLocaleDateString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    }) + ' ' + date.toLocaleTimeString('es-AR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  logout(): void {
    this.closeDropdown();
    this.auth.logout();
  }
}