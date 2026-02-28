export type UserRole = 'ADMIN' | 'BARBER' | 'CLIENT';

export interface UserSummary {
  id: number;
  name: string;
}

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  commissionRate: number | null;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role: Exclude<UserRole, 'ADMIN'>;
}
// admin edita acá 
export interface UpdateUserRequest {
  name: string;
  email: string;
  commissionRate: number | null;
}

// usuario edita acá 
export interface UpdateProfileRequest {
  name: string;
  email: string;
  currentPassword: string;  
  newPassword: string | null; 
} 