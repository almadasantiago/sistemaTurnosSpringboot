import { UserSummary } from './user.model';
import { BarberServiceResponse } from './barber-service.model';

export type AppointmentStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'COMPLETED'
  | 'CANCELLED';

export interface CreateAppointmentRequest {
  clienteId: number;
  barberoId: number;
  serviceId: number;
  fechaHoraInicio: string;
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
  endDateTime: string;
  status: AppointmentStatus;
  priceLocked: number | null;
  shopCommission: number | null;
  barberEarnings: number | null;
}