# MediTriage Connect

Portal academico para la tematica **HealthTech: Telemedicina y Triaje Automatizado**.

El proyecto cubre los entregables pedidos:

- Backend en Spring Boot con autenticacion JWT funcional.
- Dos servicios del dominio:
  - Triaje automatizado.
  - Telemedicina y gestion de citas.
- Frontend en Angular 21 consumiendo el backend.
- Dockerizacion del backend y stack local con PostgreSQL.
- Guia de despliegue cloud para backend dockerizado.

## Arquitectura

```text
meditriage-connect/
|-- backend/    Spring Boot 3 + JWT + JPA + PostgreSQL/H2
|-- frontend/   Angular 21 standalone
|-- docker-compose.yml
`-- .env.example
```

## Funcionalidades implementadas

### Autenticacion

- Registro de usuarios.
- Login JWT.
- Endpoint `/api/auth/me` para perfil autenticado.
- Persistencia del token en frontend.
- Validacion de sesion al recargar la aplicacion.

### Servicio 1: Triaje automatizado

- Evaluacion de sintomas con nivel de riesgo `LOW`, `MEDIUM` o `HIGH`.
- Priorizacion de atencion.
- Recomendacion medica orientativa.
- Historial de evaluaciones por paciente.

### Servicio 2: Telemedicina

- Consulta de doctores disponibles.
- Creacion de citas por pacientes.
- Consulta de citas por paciente, doctor asignado o administrador.
- Aprobacion de citas pendientes por doctor asignado.
- Registro separado de citas pendientes, confirmadas y completadas para doctores.
- Cancelacion de citas por paciente y cancelacion administrativa de citas pendientes.
- Reporte administrativo por doctor con cantidad de pacientes atendidos y detalle de citas.

## Usuarios de prueba

Se crean automaticamente al iniciar el backend:

| Rol | Email | Password |
|---|---|---|
| Paciente | `patient@meditriage.com` | `Patient123*` |
| Doctor | `cardio@meditriage.com` | `Doctor123*` |
| Doctor | `general@meditriage.com` | `Doctor123*` |
| Doctor | `pediatria@meditriage.com` | `Doctor123*` |
| Doctor | `neurologia@meditriage.com` | `Doctor123*` |
| Doctor | `dermatologia@meditriage.com` | `Doctor123*` |
| Admin | `admin@meditriage.com` | `Admin123*` |

## Ejecucion local con Docker

1. Copia `.env.example` a `.env` si quieres personalizar puertos o secretos.
2. Desde la raiz ejecuta:

```bash
docker compose up --build
```

Servicios locales:

- Frontend: [http://localhost:4200](http://localhost:4200)
- Backend: [http://localhost:8080](http://localhost:8080)
- Healthcheck backend: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- PostgreSQL: `localhost:5432`

## Ejecucion local sin Docker

### Backend

Requiere Java 21 y Maven 3.9+.

```bash
cd backend
mvn spring-boot:run
```

Por defecto usa H2 en memoria si no se definen variables de PostgreSQL.

### Frontend

Requiere Node.js 22+.

```bash
cd frontend
npm install
npm start
```

## Variables de entorno relevantes

| Variable | Uso |
|---|---|
| `JWT_SECRET` | Secreto JWT, minimo 32 caracteres |
| `JWT_EXPIRATION_MS` | Tiempo de expiracion del token |
| `SPRING_DATASOURCE_URL` | Conexion JDBC del backend |
| `SPRING_DATASOURCE_USERNAME` | Usuario PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | Password PostgreSQL |
| `CORS_ALLOWED_ORIGINS` | Origen permitido para Angular |
| `BACKEND_PORT` | Puerto local publicado del backend |
| `FRONTEND_PORT` | Puerto local publicado del frontend |

## Endpoints principales

### Auth

```http
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

### Triaje

```http
POST /api/triage/evaluate
GET  /api/triage/history
GET  /api/triage/{id}
```

### Telemedicina

```http
GET    /api/doctors
POST   /api/appointments
GET    /api/appointments/my
GET    /api/appointments/{id}
PATCH  /api/appointments/{id}/status
DELETE /api/appointments/{id}
```

## Despliegue cloud del backend

El requisito pide **solo backend dockerizado** en una capa gratuita de un proveedor permitido. La opcion mas directa para este proyecto es **Google Cloud Run**.

### Opcion recomendada: Google Cloud Run

1. Crea un proyecto en Google Cloud.
2. Habilita Cloud Run y Cloud Build.
3. Desde la carpeta `backend/` construye y publica la imagen:

```bash
gcloud auth login
gcloud config set project TU_PROJECT_ID
gcloud builds submit --tag gcr.io/TU_PROJECT_ID/meditriage-backend
```

4. Despliega el contenedor:

```bash
gcloud run deploy meditriage-backend \
  --image gcr.io/TU_PROJECT_ID/meditriage-backend \
  --region us-central1 \
  --platform managed \
  --allow-unauthenticated \
  --set-env-vars JWT_SECRET=TU_SECRETO_DE_32_O_MAS_CARACTERES,JWT_EXPIRATION_MS=86400000,CORS_ALLOWED_ORIGINS=http://localhost:4200
```

Notas:

- Para academica puedes usar H2 en memoria en la nube.
- Si quieres persistencia real, conecta PostgreSQL gestionado o una instancia propia dentro del proveedor permitido.
- Cloud Run detecta el puerto desde `SERVER_PORT`; en este proyecto el contenedor expone `8080`.

## Observaciones para la entrega

- El frontend no necesita desplegarse en la nube; se valida en local.
- El backend ya queda preparado para correr localmente con Docker y para empaquetarse como contenedor independiente.
- El endpoint `/actuator/health` ayuda a verificar el estado del backend en entornos cloud.
