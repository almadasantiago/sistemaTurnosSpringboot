export type RolUsuario = 'ADMIN' | 'BARBERO' | 'CLIENTE';

export interface Usuario {
  readonly id: number;
  nombre: string;
  email: string;
  rol: RolUsuario;
}