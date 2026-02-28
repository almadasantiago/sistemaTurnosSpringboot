# Sistema de Turnos - Documentación del Proyecto

## ¿De qué se trata este sistema?

Es una aplicación para gestionar turnos de una barbería. Permite que:
- Los **clientes** reserven turnos con un barbero.
- Los **barberos** vean su agenda y horarios.
- El **admin** gestione usuarios, servicios y comisiones.

---

## Arquitectura: Monolito Modular

El proyecto usa una arquitectura llamada **monolito modular**. Esto significa que todo está en una sola aplicación, pero organizado en módulos separados según su responsabilidad. Es más simple que microservicios pero más ordenado que un monolito sin estructura.

```
sistemaTurnosSpringboot/
├── backend/   → Java + Spring Boot + JPA (la lógica y la base de datos)
└── frontend/  → Angular 17 (la interfaz visual)
```

El frontend le habla al backend a través de una **API REST** (peticiones HTTP como GET, POST, PUT, DELETE).

---

## BACKEND (Java + Spring Boot + JPA)

### ¿Cómo está organizado?

Cada funcionalidad tiene su propia carpeta (módulo):

```
com/barberia/
├── users/           → Todo lo relacionado a usuarios
├── appointments/    → Todo lo relacionado a turnos
├── barberservices/  → Servicios que ofrece la barbería (corte, barba, etc.)
├── workschedules/   → Horarios de trabajo de los barberos
└── shared/          → Cosas compartidas (manejo de errores, etc.)
```

Dentro de cada módulo hay capas:

| Capa | Archivo | ¿Qué hace? |
|---|---|---|
| **Entity** | `Users.java`, `Appointment.java`, etc. | Representa una tabla en la base de datos |
| **Repository** | `UserRepository.java`, etc. | Se comunica con la base de datos |
| **Service** | `AppointmentService.java`, etc. | Contiene la lógica del negocio |
| **Controller** | `UserController.java`, etc. | Recibe y responde las peticiones HTTP |
| **DTO** | `UserResponse.java`, etc. | Define qué datos se envían/reciben |

---

### Entidades (tablas en la base de datos)

#### Users (Usuarios)
```java
// Representa a cualquier persona del sistema: admin, barbero o cliente
public class Users {
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private Double porcentajeComision; // Solo aplica a barberos
}
```
> Nota: El campo `porcentajeComision` solo tiene valor si el usuario es barbero. Para clientes y admin queda en `null`.

---

#### Service (Servicios de la barbería)
```java
// Representa un servicio ofrecido: corte de pelo, barba, etc.
public class Service {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer duracionMinutos;
}
```

---

#### Appointment (Turno)
```java
// Representa un turno reservado
public class Appointment {
    private Long id;
    private Users cliente;        // Quién reservó
    private Users barbero;        // Quién lo atiende
    private Service service;      // Qué servicio se realiza
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String estado;        // PENDING, CONFIRMED, COMPLETED, CANCELLED
    private Double precioFinal;
    private Double comisionBarberia;
    private Double gananciaBarbero;
}
```
> Los campos `precioFinal`, `comisionBarberia` y `gananciaBarbero` se calculan cuando el turno se completa.

---

#### WorkSchedule (Horario de trabajo)
```java
// Define los días y horarios en que trabaja un barbero
public class WorkSchedule {
    private Long id;
    private Users barbero;
    private Integer diaSemana;    // 1=Lunes, 7=Domingo
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
}
```

---

## FRONTEND (Angular 17)

### ¿Cómo está organizado?

```
src/app/
├── core/                  → Núcleo de la app (servicios, modelos, interceptores)
│   ├── models/            → Interfaces TypeScript (la forma de los datos)
│   ├── services/          → Clases que hablan con el backend
│   ├── guards/            → Protegen rutas según el rol del usuario
│   └── interceptors/      → Modifican automáticamente cada petición HTTP
├── features/              → Módulos visuales por funcionalidad
│   ├── auth/              → Login y registro
│   │   ├── login/
│   │   └── register/
│   ├── dashboard/         → Pantallas principales por rol
│   │   ├── admin-dashboard/
│   │   └── barber-dashboard/
│   └── appointments/      → Gestión de turnos
│       ├── appointment-list/
│       └── appointment-form/
└── shared/                → Componentes reutilizables (botones, tarjetas, etc.)
```

---

### Models (Modelos / Interfaces)

Los modelos son como "moldes" que definen la forma exacta de los datos. Cuando el backend responde con un usuario, el frontend sabe exactamente qué campos esperar.

#### auth.model.ts
```typescript
// Lo que se manda al login
export interface LoginRequest {
  email: string;
  password: string;
}

// Lo que el backend responde tras hacer login
export interface AuthResponse {
  token: string;  // El "carnet de acceso" para futuras peticiones
  id: number;
  name: string;
  email: string;
  role: UserRole; // 'ADMIN' | 'BARBER' | 'CLIENT'
}
```

---

#### user.model.ts
```typescript
// Los tres roles posibles en el sistema
export type UserRole = 'ADMIN' | 'BARBER' | 'CLIENT';

// Versión simplificada de un usuario (para mostrar en listas)
export interface UserSummary {
  id: number;
  name: string;
}

// Versión completa (para el panel de admin)
export interface UserResponse {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  commissionRate: number | null; // null si no es barbero
}

// Para que el admin edite un usuario
export interface UpdateUserRequest {
  name: string;
  email: string;
  commissionRate: number | null;
}

// Para que el propio usuario edite su perfil
export interface UpdateProfileRequest {
  name: string;
  email: string;
  currentPassword: string;
  newPassword: string | null;
}
```

---

#### appointment.model.ts
```typescript
// Estados posibles de un turno
export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED';

// Para crear un turno nuevo
export interface CreateAppointmentRequest {
  clientId: number;
  barberId: number;
  barberServiceId: number;
  dateTime: string; // Formato: "2026-03-15T10:30:00"
}

// Lo que el backend devuelve al consultar un turno
export interface AppointmentResponse {
  id: number;
  client: UserSummary;
  barber: UserSummary;
  barberService: BarberServiceResponse;
  dateTime: string;
  status: AppointmentStatus;
  cancelReason: string | null;
  priceLocked: number | null;      // Precio al momento del turno
  shopCommission: number | null;   // Lo que gana la barbería
  barberEarnings: number | null;   // Lo que gana el barbero
}
```

---

#### barber-service.model.ts
```typescript
// Un servicio ofrecido (corte, barba, etc.)
export interface BarberServiceResponse {
  id: number;
  name: string;
  price: number;
  durationMinutes: number;
  finished: boolean; // true = servicio dado de baja
}
```

---

#### work-schedule.model.ts
```typescript
// Días de la semana en inglés (como los envía el backend)
export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

// Horario de un barbero en un día específico
export interface WorkScheduleResponse {
  id: number;
  barberId: number;
  dayOfWeek: DayOfWeek;
  startTime: string; // Formato: "09:00"
  endTime: string;   // Formato: "18:00"
}
```

---

### Services (Servicios Angular)

Los servicios son las clases que realizan las peticiones HTTP al backend. Cada servicio:
1. Tiene una URL base que apunta al endpoint correspondiente del backend.
2. Guarda los datos en un **signal** (estado reactivo de Angular 17).
3. Expone esos datos de forma de solo lectura al resto de la app.

---

#### AuthService
Se encarga del login, logout y de saber quién está logueado en todo momento.

```typescript
// URL base: http://localhost:8080/api/auth
@Injectable({ providedIn: 'root' })
export class AuthService {

  // Signal: guarda el usuario actual en memoria
  private readonly _currentUser = signal<AuthResponse | null>(null);

  // Solo lectura para los componentes
  readonly currentUser = this._currentUser.asReadonly();

  // Computed: se recalcula automáticamente cuando cambia _currentUser
  readonly isAuthenticated = computed(() => this._currentUser() !== null);
  readonly currentRole = computed(() => this._currentUser()?.role ?? null);
}
```

**Flujo de login:**
1. El usuario escribe email y contraseña.
2. Se llama a `login()` → hace POST a `/api/auth/login`.
3. El backend responde con un `token` y los datos del usuario.
4. Se guarda en `localStorage` (para no perder la sesión al recargar).
5. Se actualiza el signal `_currentUser`.

---

#### UserService
Gestión de usuarios (solo accesible por el admin).

```typescript
// URL base: http://localhost:8080/api/users
export class UserService {
  getAll()               // Lista todos los usuarios
  getById(id)            // Obtiene uno por ID
  update(id, request)    // Admin edita un usuario
  updateProfile(request) // El usuario edita su propio perfil
}
```

---

#### AppointmentService
Gestión de turnos.

```typescript
// URL base: http://localhost:8080/api/appointments
export class AppointmentService {
  getAll()              // Lista todos los turnos
  getById(id)           // Obtiene uno
  create(request)       // Crea un nuevo turno
  update(id, request)   // Modifica fecha/servicio
  cancel(id, request)   // Cancela con motivo
  complete(id)          // Marca como completado

  // Computed lists (se actualizan solos)
  pendingAppointments   // Solo los PENDING
  completedAppointments // Solo los COMPLETED
}
```

---

#### BarberServiceService
Gestión de servicios de la barbería.

```typescript
// URL base: http://localhost:8080/api/barber-services
export class BarberServiceService {
  getAll()              // Lista todos (activos y finalizados)
  create(request)       // Crea un servicio nuevo
  update(id, request)   // Edita nombre, precio o duración
  finish(id)            // Da de baja un servicio (no se elimina, se marca finished=true)
}
```

---

#### WorkScheduleService
Gestión de horarios de los barberos.

```typescript
// URL base: http://localhost:8080/api/work-schedules
export class WorkScheduleService {
  getByBarber(barberId)  // Obtiene los horarios de un barbero específico
  create(request)        // Agrega un día/horario
  update(id, request)    // Cambia hora de entrada/salida
  delete(id)             // Elimina un horario
}
```

---

### Environment (Configuración de entornos)

```typescript
// environment.ts (desarrollo local)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};

// environment.prod.ts (producción)
export const environment = {
  production: true,
  apiUrl: 'https://tu-dominio.com/api'
};
```

Todos los servicios importan `environment.apiUrl` para construir sus URLs. Así, con un solo cambio en este archivo, toda la app apunta a otro servidor.

---

## Flujo completo de una petición

Ejemplo: **el cliente reserva un turno**

```
[Componente AppointmentForm]
        ↓ llama a
[AppointmentService.create(request)]
        ↓ hace POST HTTP a
[Backend: POST /api/appointments]
        ↓ pasa por
[AppointmentController → AppointmentService → AppointmentRepository]
        ↓ guarda en
[Base de datos MySQL]
        ↓ responde con
[AppointmentResponse (JSON)]
        ↓ el servicio actualiza
[Signal _appointments]
        ↓ los componentes que usan
[appointments signal] se actualizan automáticamente
```

---

## Resumen rápido

| Concepto | ¿Qué es? | Ejemplo |
|---|---|---|
| **Entity (Java)** | Tabla en la base de datos | `Appointment.java` |
| **DTO** | Datos que viajan entre backend y frontend | `AppointmentResponse.java` |
| **Interface (TS)** | Molde de datos en el frontend | `AppointmentResponse` en `appointment.model.ts` |
| **Service (Angular)** | Clase que hace peticiones HTTP | `AppointmentService` |
| **Signal** | Estado reactivo (reemplaza a `BehaviorSubject`) | `_appointments = signal([])` |
| **Computed** | Valor derivado de un signal | `pendingAppointments` |
| **Environment** | Configuración por entorno | `apiUrl` en `environment.ts` |
