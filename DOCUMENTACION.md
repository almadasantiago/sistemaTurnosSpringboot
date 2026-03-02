# Sistema de Turnos — Documentación Técnica

## Descripción del sistema

Aplicación fullstack para la gestión de turnos de una barbería. Contempla tres roles de usuario (ADMIN, BARBER, CLIENT), autenticación stateless con JWT, control de horarios de barberos, servicios con precio y duración configurable, y cálculo de comisiones automático al completar un turno.

---

## Stack tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Backend | Java + Spring Boot | 3.x |
| Persistencia | Spring Data JPA + Hibernate | — |
| Base de datos | MySQL | — |
| Seguridad | Spring Security + JWT (jjwt) | — |
| Frontend | Angular | 17 |
| Estado reactivo | Angular Signals | (nativo Angular 17) |
| HTTP | Angular HttpClient | — |
| Estilos | SCSS | — |

---

## Arquitectura: Monolito Modular

Se eligió **monolito modular** (en lugar de microservicios) porque:
- El dominio del problema no justifica la complejidad operativa de múltiples servicios desplegados por separado.
- Permite mantener cohesión de código sin sacrificar organización interna.
- Es fácil de migrar a microservicios en el futuro si el sistema escala: los módulos ya están aislados.

```
sistemaTurnosSpringboot/
├── backend/
│   └── src/main/java/com/barberia/
│       ├── users/
│       ├── appointments/
│       ├── barberservices/
│       ├── workschedules/
│       └── shared/
│           ├── config/       → Seguridad, JWT, CORS
│           └── exception/    → Manejo global de errores
└── frontend/
    └── src/app/
        ├── core/             → Modelos, servicios, guards, interceptores
        ├── features/         → Componentes por dominio (auth, dashboard, appointments)
        └── shared/           → Componentes reutilizables
```

La comunicación entre ambas capas es exclusivamente a través de **REST API**. El frontend nunca accede directamente a la base de datos.

---

## BACKEND

### Estructura interna de cada módulo

Cada módulo sigue el patrón de capas estándar de Spring:

```
users/
├── User.java                   → @Entity (tabla en la BD)
├── Role.java                   → @Enum (ADMIN, BARBER, CLIENT)
├── UserRepository.java         → @Repository (JPA)
├── UserService.java            → @Service (lógica de negocio)
├── UserController.java         → @RestController (endpoints HTTP)
├── UserDetailsServiceImpl.java → integración con Spring Security
└── dto/
    ├── UserResponse.java
    ├── UpdateUserRequest.java
    └── RegisterRequest.java
```

> **¿Por qué DTOs separados de la Entity?**
> La entidad `User` contiene `password`. Exponer la entidad directamente en los endpoints significaría enviar el hash de la contraseña en cada respuesta. Los DTOs permiten controlar exactamente qué campos se exponen al cliente.

---

### Entidades (modelo de datos)

#### User

```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;         // Almacenado como BCrypt hash, nunca en texto plano

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;               // ADMIN | BARBER | CLIENT

    @Column
    private Double commissionRate;   // Solo relevante para BARBER; null para los demás
}
```

> Se usa `@Enumerated(EnumType.STRING)` en lugar de `ORDINAL` para que la base de datos almacene `"ADMIN"` y no `0`. Si en el futuro se reordena el enum, `ORDINAL` rompería datos históricos.

---

#### Appointment

```java
@Entity
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne private Users cliente;
    @ManyToOne private Users barbero;
    @ManyToOne private Service service;

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    private String estado;             // PENDING | CONFIRMED | COMPLETED | CANCELLED

    private Double precioFinal;        // Se bloquea al completar el turno
    private Double comisionBarberia;   // Calculada en base al commissionRate del barbero
    private Double gananciaBarbero;    // precioFinal - comisionBarberia
}
```

> Los campos financieros son `null` hasta que el turno pasa a `COMPLETED`. Se bloquean en ese momento para preservar el historial aunque el precio del servicio cambie después.

---

#### Service (servicio de la barbería)

```java
@Entity
public class Service {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double precio;
    private Integer duracionMinutos;
}
```

> **Decisión de diseño — soft delete:** Los servicios no se eliminan físicamente. Cuando se dan de baja se marcan con `finished = true`. Esto preserva el historial de turnos anteriores que referenciaban ese servicio.

---

#### WorkSchedule

```java
@Entity
public class WorkSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "barbero_id")
    private Users barbero;

    private Integer diaSemana;     // Pendiente: migrar a enum DayOfWeek para alinearse con el frontend
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
}
```

---

### Seguridad (Spring Security + JWT)

La autenticación es **stateless** (sin sesión en el servidor). Se eligió JWT porque:
- El frontend y el backend están desacoplados; no hay cookies de sesión compartidas.
- El token es autosuficiente: contiene el email y el rol del usuario firmados criptográficamente.
- Escala horizontalmente sin necesidad de sesión compartida entre instancias.

#### Flujo de autenticación

```
1. POST /api/auth/login  { email, password }
         ↓
2. Spring verifica credenciales contra la BD (BCrypt compare)
         ↓
3. Se genera un JWT firmado con HS256
   Payload: { sub: email, role: "ADMIN", iat: ..., exp: ... }
         ↓
4. El frontend recibe el token y lo guarda en localStorage
         ↓
5. Cada petición posterior incluye: Authorization: Bearer <token>
         ↓
6. JwtAuthFilter intercepta, valida firma y expiración,
   e inyecta el usuario en el SecurityContext de Spring
```

#### Clases involucradas en seguridad

| Clase | Responsabilidad |
|---|---|
| `JwtService` | Generar, parsear y validar tokens JWT |
| `JwtAuthFilter` | Filtro HTTP que extrae y valida el token en cada request |
| `UserDetailsServiceImpl` | Carga el usuario desde la BD por email para Spring Security |
| `SecurityConfig` | Define reglas de acceso por rol, CORS, y encadenamiento de filtros |

#### ¿Por qué `UserDetailsServiceImpl` está separado de `SecurityConfig`?

La implementación original definía `UserDetailsService` como un `@Bean` dentro de `SecurityConfig`. Esto generaba una **dependencia circular en el arranque de Spring**:

```
SecurityConfig → JwtAuthFilter → UserDetailsService → SecurityConfig
```

Spring no puede resolver este ciclo. La solución fue extraer `UserDetailsServiceImpl` como un `@Service` independiente en el paquete `users`, que Spring instancia de forma autónoma antes de configurar la seguridad.

#### CORS

El backend corre en `localhost:8080` y el frontend en `localhost:4200`. Sin configuración CORS explícita, el navegador rechaza todas las respuestas por la política Same-Origin.

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:4200"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    // ...
}
```

> En producción, `allowedOrigins` debe cambiarse al dominio real del frontend.

#### Reglas de autorización por endpoint

```
/api/auth/**             → público (login, registro)
GET  /api/users          → solo ADMIN
POST /api/users/barbers  → solo ADMIN
PUT  /api/users/{id}     → solo ADMIN
PUT  /api/users/me       → cualquier usuario autenticado
*                        → cualquier petición autenticada
```

#### application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistemaTurnos_DB?serverTimezone=UTC
spring.jpa.hibernate.ddl-auto=update   # Crea/modifica tablas automáticamente
spring.jpa.show-sql=true               # Imprime SQL generado (solo en desarrollo)

jwt.secret=8f3a2b9c...    # Clave HMAC-SHA256 de 64 chars (256 bits)
jwt.expiration=86400000   # 24 horas en milisegundos
```

> `ddl-auto=update` es útil en desarrollo pero **no debe usarse en producción**. En producción se usa `validate` o migraciones con Flyway/Liquibase.

---

## FRONTEND (Angular 17)

### Estructura de carpetas

```
src/app/
├── core/
│   ├── models/               → Interfaces TypeScript (contratos de datos)
│   │   ├── auth.model.ts
│   │   ├── user.model.ts
│   │   ├── appointment.model.ts
│   │   ├── barber-service.model.ts
│   │   └── work-schedule.model.ts
│   ├── services/             → Lógica HTTP + estado reactivo
│   │   ├── auth.service.ts
│   │   ├── user.service.ts
│   │   ├── appointment.service.ts
│   │   ├── barber-service.service.ts
│   │   └── work-schedule.service.ts
│   ├── guards/
│   │   └── auth.guard.ts     → Protege rutas según autenticación y rol
│   └── interceptors/
│       └── auth.interceptor.ts → Adjunta el JWT a cada petición HTTP
├── features/
│   ├── auth/
│   │   ├── login/
│   │   └── register/
│   ├── dashboard/
│   │   ├── admin-dashboard/
│   │   └── barber-dashboard/
│   └── appointments/
│       ├── appointment-list/
│       └── appointment-form/
├── shared/                   → Componentes reutilizables
└── environments/
    ├── environment.ts        → { production: false, apiUrl: 'http://localhost:8080/api' }
    └── environment.prod.ts   → { production: true, apiUrl: 'https://tu-dominio.com/api' }
```

### Standalone Components

Angular 17 usa **standalone components** sin NgModules. Cada componente declara sus dependencias directamente en el decorador `@Component`. Esto simplifica el árbol de dependencias, mejora el tree-shaking del bundle y hace el código más predecible.

```typescript
@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent {}
```

---

### Models (interfaces TypeScript)

Los modelos replican exactamente los DTOs del backend. Son interfaces (no clases) porque solo definen forma de datos, sin lógica ni instanciación.

#### auth.model.ts

```typescript
export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;   // JWT recibido tras login exitoso
  id: number;
  name: string;
  email: string;
  role: UserRole;
}
```

> `AuthResponse` es lo que se persiste en `localStorage` para restaurar la sesión al recargar la página sin necesidad de volver a hacer login.

---

#### user.model.ts

```typescript
export type UserRole = 'ADMIN' | 'BARBER' | 'CLIENT';

// Versión reducida: se usa dentro de AppointmentResponse para no exponer datos sensibles
export interface UserSummary {
  id: number;
  name: string;
}

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  commissionRate: number | null;  // null si el usuario no es BARBER
}

// Usado por el ADMIN para editar datos de otro usuario
export interface UpdateUserRequest {
  name: string;
  email: string;
  commissionRate: number | null;
}

// Requiere contraseña actual como verificación de identidad
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
export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED';

export interface AppointmentResponse {
  id: number;
  client: UserSummary;
  barber: UserSummary;
  barberService: BarberServiceResponse;
  dateTime: string;                    // ISO 8601: "2026-03-15T10:30:00"
  status: AppointmentStatus;
  cancelReason: string | null;
  priceLocked: number | null;          // Precio capturado al momento de completar
  shopCommission: number | null;
  barberEarnings: number | null;
}
```

---

#### barber-service.model.ts

```typescript
export interface BarberServiceResponse {
  id: number;
  name: string;
  price: number;
  durationMinutes: number;
  finished: boolean;    // Soft delete: oculto en UI pero no eliminado de BD
}
```

---

#### work-schedule.model.ts

```typescript
// String union en lugar de número para legibilidad y alineamiento con el enum Java
export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

export interface WorkScheduleResponse {
  id: number;
  barberId: number;
  dayOfWeek: DayOfWeek;
  startTime: string;   // "HH:mm"
  endTime: string;
}
```

---

### Services y Signals

#### ¿Por qué Signals en lugar de BehaviorSubject (RxJS)?

Angular 17 introduce Signals como mecanismo de estado reactivo nativo. Ventajas sobre `BehaviorSubject`:
- No requiere `subscribe`/`unsubscribe` manual → sin memory leaks.
- Detección de cambios granular (no por componente completo).
- `computed()` reemplaza `combineLatest` + `map` para estados derivados.
- `asReadonly()` expone el estado sin permitir mutación externa, forzando que los cambios pasen siempre por el servicio.

---

#### AuthService

```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly _currentUser = signal<AuthResponse | null>(null);

  readonly currentUser     = this._currentUser.asReadonly();
  readonly isAuthenticated = computed(() => this._currentUser() !== null);
  readonly currentRole     = computed(() => this._currentUser()?.role ?? null);

  constructor() {
    this.loadSessionFromStorage(); // Restaura sesión al recargar la página
  }

  login(request: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, request).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('currentUser', JSON.stringify(response));
        this._currentUser.set(response);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this._currentUser.set(null);
    this.router.navigate(['/auth/login']);
  }
}
```

---

#### AppointmentService (ejemplo de computed sobre signal)

```typescript
@Injectable({ providedIn: 'root' })
export class AppointmentService {

  private readonly _appointments = signal<AppointmentResponse[]>([]);
  readonly appointments = this._appointments.asReadonly();

  // Se recalculan automáticamente cuando cambia _appointments
  readonly pendingAppointments   = computed(() => this._appointments().filter(a => a.status === 'PENDING'));
  readonly completedAppointments = computed(() => this._appointments().filter(a => a.status === 'COMPLETED'));

  create(request: CreateAppointmentRequest) {
    return this.http.post<AppointmentResponse>(this.baseUrl, request).pipe(
      // Patrón inmutable: crea nuevo array en lugar de mutar el existente
      // Necesario para que Angular detecte el cambio en el signal
      tap(created => this._appointments.update(list => [...list, created]))
    );
  }
}
```

---

#### Interceptor de autenticación (pendiente)

`auth.interceptor.ts` está creado pero vacío. Su función: adjuntar automáticamente el JWT a cada petición saliente para no tener que hacerlo en cada servicio individualmente.

```typescript
// Implementación pendiente
export function authInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {
  const token = inject(AuthService).getToken();
  if (token) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(req);
}
```

---

#### Guard de rutas (pendiente)

`auth.guard.ts` está creado pero vacío. Protegerá rutas privadas redirigiendo al login si el usuario no está autenticado o no tiene el rol requerido.

```typescript
// Implementación pendiente
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.isAuthenticated() ? true : inject(Router).createUrlTree(['/auth/login']);
};
```

---

## Flujo completo: login + petición autenticada

```
[LoginComponent]
  → llama AuthService.login({ email, password })
  → POST http://localhost:8080/api/auth/login
  → Backend valida BCrypt, genera JWT
  → Frontend guarda token en localStorage + signal
  → Redirect al dashboard según rol

[Cualquier componente posterior]
  → llama, p.ej: AppointmentService.getAll()
  → HttpClient hace GET /api/appointments
  → [auth.interceptor] inyecta Authorization: Bearer <token>
  → [JwtAuthFilter] valida token, carga usuario en SecurityContext
  → Controller responde con lista de turnos (JSON)
  → Servicio guarda en signal _appointments
  → Componentes que consumen el signal se actualizan automáticamente
```

---

## Estado actual del proyecto

### Completado
- Entidades JPA: `User`, `Appointment`, `Service`, `WorkSchedule`
- Seguridad: JWT stateless, BCrypt, roles por endpoint, CORS configurado
- Separación de `UserDetailsServiceImpl` para eliminar dependencia circular
- Todos los modelos TypeScript del frontend (5 archivos)
- Todos los servicios Angular con signals y computed (5 servicios)
- Archivos `environment.ts` y `environment.prod.ts`
- Todos los componentes creados con nombre de clase correcto

### Pendiente
- Implementar `auth.interceptor.ts`
- Implementar `auth.guard.ts`
- Implementar templates HTML de cada componente
- Implementar lógica en los componentes (inyectar servicios, manejar formularios)
- Definir rutas en `app.routes.ts`
- Completar `DataSeeder.java` (datos iniciales en la BD)
- Alinear `WorkSchedule.java`: agregar `@Entity` y cambiar `Integer diaSemana` por enum `DayOfWeek`

---

## Tabla de referencia rápida

| Concepto | Capa | Ejemplo |
|---|---|---|
| `@Entity` | Backend — BD | `User.java`, `Appointment.java` |
| `@Repository` | Backend — persistencia | `UserRepository extends JpaRepository` |
| `@Service` | Backend — lógica | `AppointmentService.java` |
| `@RestController` | Backend — API | `UserController.java` |
| DTO | Backend — contrato API | `UserResponse.java`, `CreateAppointmentRequest.java` |
| `interface` (TS) | Frontend — tipado | `AppointmentResponse` en `appointment.model.ts` |
| `signal` | Frontend — estado | `_appointments = signal([])` |
| `computed` | Frontend — estado derivado | `pendingAppointments` |
| `interceptor` | Frontend — HTTP middleware | `auth.interceptor.ts` |
| `guard` | Frontend — protección de rutas | `auth.guard.ts` |
| `environment` | Frontend — configuración | `apiUrl` en `environment.ts` |
