export type DayOfWeek =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY';

export interface WorkScheduleResponse {
  id: number;
  barberId: number;
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
}

export interface CreateWorkScheduleRequest {
  barberId: number;
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
}

export interface UpdateWorkScheduleRequest {
  startTime: string;
  endTime: string;
}