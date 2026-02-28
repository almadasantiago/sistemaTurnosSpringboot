export interface BarberServiceResponse {
  id: number;
  name: string;
  price: number;
  durationMinutes: number;
  finished: boolean;
}

export interface CreateBarberServiceRequest {
  name: string;
  price: number;
  durationMinutes: number;
}

export interface UpdateBarberServiceRequest {
  name: string;
  price: number;
  durationMinutes: number;
}