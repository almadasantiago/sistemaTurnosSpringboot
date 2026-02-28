import { UserSummary } from './user.model';
import { BarberServiceResponse } from './barber-service.model';

export type AppointmentStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'COMPLETED'
  | 'CANCELLED';

export interface CreateAppointmentRequest {
  clientId: number;
  barberId: number;
  barberServiceId: number;
  dateTime: string;
}

export interface UpdateAppointmentRequest {
  dateTime: string;
  barberServiceId: number;
}

export interface CancelAppointmentRequest {
  reason: string | null;
}

export interface AppointmentResponse {
  id: number;
  client: UserSummary;
  barber: UserSummary;
  barberService: BarberServiceResponse;
  dateTime: string;
  status: AppointmentStatus;
  cancelReason: string | null;
  priceLocked: number | null;
  shopCommission: number | null;
  barberEarnings: number | null;
}