# Guía: Crear Usuarios desde el Módulo /users

Ahora puedes gestionar usuarios a través del endpoint `/users` (protegido por ADMIN) con encriptación automática de contraseñas.

## Endpoints Disponibles

### 1. Crear Usuario (POST)

```http
POST http://localhost:8080/users
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "username": "empleado1",
  "password": "password123",
  "role": "EMPLEADO",
  "person": {"id": 2}
}
```

**El sistema automáticamente**:
- ✅ Encripta el password con BCrypt
- ✅ Agrega el prefijo `ROLE_` al rol si no existe
- ✅ Verifica que el username no exista
- ✅ No devuelve el password en la respuesta (muestra `[PROTECTED]`)

**Respuesta** (HTTP 201):
```json
{
  "id": 3,
  "username": "empleado1",
  "password": "[PROTECTED]",
  "role": "ROLE_EMPLEADO",
  "person": {"id": 2, ...}
}
```

---

### 2. Actualizar Usuario (PUT)

```http
PUT http://localhost:8080/users/3
Authorization: Bearer {ADMIN_TOKEN}
Content-Type: application/json

{
  "username": "empleado1",
  "password": "newpassword456",
  "role": "EMPLEADO",
  "person": {"id": 2}
}
```

**Solo encripta el password si cambió** (detecta si ya está encriptado con BCrypt)

---

### 3. Listar Todos los Usuarios (GET)

```http
GET http://localhost:8080/users
Authorization: Bearer {ADMIN_TOKEN}
```

---

### 4. Obtener Usuario por ID (GET)

```http
GET http://localhost:8080/users/1
Authorization: Bearer {ADMIN_TOKEN}
```

---

### 5. Obtener Usuario por Username (GET)

```http
GET http://localhost:8080/users/username/admin
Authorization: Bearer {ADMIN_TOKEN}
```

---

### 6. Eliminar Usuario (DELETE)

```http
DELETE http://localhost:8080/users/3
Authorization: Bearer {ADMIN_TOKEN}
```

---

## Protección por Roles

Todos los endpoints `/users/**` requieren rol `ROLE_ADMIN`. Si un empleado intenta acceder:

```
HTTP 403 Forbidden
```

---

## Ejemplo Completo en Postman

### Paso 1: Login como ADMIN
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Guardar el token** de la respuesta.

---

### Paso 2: Crear Usuario
```http
POST http://localhost:8080/users
Authorization: Bearer {TU_TOKEN_ADMIN}
Content-Type: application/json

{
  "username": "vendedor1",
  "password": "vend123",
  "role": "EMPLEADO",
  "person": {"id": 5}
}
```

---

### Paso 3: Verificar que el usuario puede hacer login
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "vendedor1",
  "password": "vend123"
}
```

✅ Debe devolver un token JWT válido.

---

## Diferencias entre /auth/register y /users

| Característica | `/auth/register` | `/users` (ADMIN) |
|----------------|------------------|------------------|
| **Protección** | Público* | Requiere ADMIN |
| **Propósito** | Auto-registro | Gestión de usuarios |
| **Validaciones** | Básicas | Completas con @Valid |
| **Recomendación** | Deshabilitar en producción | Usar para administración |

\* Puede deshabilitarse o protegerse según necesidad

---

## Seguridad Implementada

✅ **Passwords encriptados**: BCrypt automático  
✅ **Solo ADMIN**: Protegido con `@PreAuthorize`  
✅ **Username único**: Validación antes de crear  
✅ **Password oculto**: No se devuelve en respuestas  
✅ **Roles normalizados**: Prefijo `ROLE_` automático

---

¡Ahora puedes gestionar usuarios de forma segura desde `/users`! 🎉
