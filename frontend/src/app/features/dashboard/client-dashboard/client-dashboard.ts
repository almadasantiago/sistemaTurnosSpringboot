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

  private readonly SERVICE_IMAGES: Record<string, string> = {
    'Corte de pelo': 'https://images.unsplash.com/photo-1585747860715-2ba37e788b70?q=80&w=400',
    'Afeitado de barba': 'https://images.unsplash.com/photo-1621605815971-fbc98d665033?q=80&w=400',
    'Barba': 'https://images.unsplash.com/photo-1599351431247-f10b21817021?q=80&w=400',
    'Corte y tintura': 'https://images.unsplash.com/photo-1605497788044-5a32c7078486?q=80&w=400',
    'Corte y barba': 'https://images.unsplash.com/photo-1503951914875-452162b0f3f1?q=80&w=400'
  };

  private loadServices(): void {
    this.isLoadingServices.set(true);
    this.barberServiceService.getAll().subscribe({
      next: (data: BarberServiceResponse[]) => {
        const modifiedBackendServices = data.map(s => ({
          ...s,
          durationMinutes: s.name.toLowerCase().includes('barba') ? 90 : 60
        }));

        const extraServices: BarberServiceResponse[] = [
          { id: 998, name: 'Barba', price: 8000, durationMinutes: 30, finished: false },
          { id: 999, name: 'Corte y tintura', price: 20000, durationMinutes: 90, finished: false }
        ];
        
        const existingNames = modifiedBackendServices.map(s => s.name.toLowerCase());
        const filteredExtras = extraServices.filter(s => !existingNames.includes(s.name.toLowerCase()));
        
        this.services.set([...modifiedBackendServices, ...filteredExtras]);
        this.isLoadingServices.set(false);
      },
      error: () => this.isLoadingServices.set(false)
    });
  }

  getServiceImage(name: string): string {
    return this.SERVICE_IMAGES[name] || 'https://images.unsplash.com/photo-1503951914875-452162b0f3f1?q=80&w=400';
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