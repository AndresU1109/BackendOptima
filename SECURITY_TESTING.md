# Guía de Pruebas - Spring Security + JWT

## 🔐 Sistema de Autenticación Implementado

Se ha integrado Spring Security con JWT en OptimaStock:
- ✅ Autenticación con tokens JWT (24h)
- ✅ Roles: `ROLE_ADMIN` y `ROLE_EMPLEADO` 
- ✅ Endpoints protegidos por rol
- ✅ Password encryption con BCrypt

---

## 📋 Requisitos Previos

### 1. Crear Usuarios en la Base de Datos

Primero necesitas crear usuarios de prueba. Tienes 2 opciones:

#### Opción A: Usar el endpoint `/auth/register`

```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "role": "ADMIN",
  "person": {"id": 1}
}
```

**Nota**: El endpoint automáticamente:
- Encripta la contraseña con BCrypt
- Agrega el prefijo `ROLE_` si no existe

#### Opción B: Insertar directamente en MySQL

```sql
-- Primero genera el hash BCrypt de la contraseña
-- admin123 → $2a$10$N9qo8uLOickgx2ZMRZoMy.K3MkS8nQJsJ.SoP0o0IBXT6Xv1f2Cma

INSERT INTO user (username, password, role, person_id) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.K3MkS8nQJsJ.SoP0o0IBXT6Xv1f2Cma', 'ROLE_ADMIN', 1),
('empleado', '$2a$10$N9qo8uLOickgx2ZMRZoMy.K3MkS8nQJsJ.SoP0o0IBXT6Xv1f2Cma', 'ROLE_EMPLEADO', 2);
``

---

## 🧪 Pruebas de Autenticación

### Test 1: Login Exitoso (ADMIN)

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Respuesta Esperada** (HTTP 200):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9XSwic3ViIjoiYWRtaW4iLCJpYXQiOjE3MzM0MzE1MDAsImV4cCI6MTczMzUxNzkwMH0.xyz123...",
  "type": "Bearer",
  "username": "admin",
  "role": "ROLE_ADMIN",
  "expiresIn": 86400000
}
```

**Guardar el token** para usarlo en las siguientes pruebas.

---

### Test 2: Login con Credenciales Inválidas

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "wrongpassword"
}
```

**Respuesta Esperada** (HTTP 401):
```json
{
  "error": "Credenciales inválidas",
  "message": "Usuario o contraseña incorrectos"
}
```

---

## 🔒 Pruebas de Autorización

### Test 3: Acceso SIN Token

```http
GET http://localhost:8080/product
```

**Respuesta Esperada**: HTTP 401 Unauthorized o 403 Forbidden

---

### Test 4: Acceso CON Token Válido (ADMIN)

```http
GET http://localhost:8080/product
Authorization: Bearer {TU_TOKEN_AQUI}
```

**Reemplaza** `{TU_TOKEN_AQUI}` con el token obtenido en Test 1.

**Respuesta Esperada**: HTTP 200 con lista de productos

---

### Test 5: Operación Permitida para ADMIN

```http
DELETE http://localhost:8080/product/1
Authorization: Bearer {ADMIN_TOKEN}
```

**Respuesta Esperada**: HTTP 204 No Content (producto eliminado)

---

### Test 6: Operación NO Permitida para EMPLEADO

Primero haz login como empleado:

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "empleado",
  "password": "admin123"
}
```

Luego intenta eliminar un producto:

```http
DELETE http://localhost:8080/product/2
Authorization: Bearer {EMPLEADO_TOKEN}
```

**Respuesta Esperada**: HTTP 403 Forbidden

---

## 📊 Matriz de Permisos

| Operación | Endpoint | ADMIN | EMPLEADO |
|-----------|----------|-------|----------|
| Ver productos | GET /product | ✅ | ✅ |
| Crear producto | POST /product | ✅ | ❌ |
| Actualizar producto | PUT /product/{id} | ✅ | ❌ |
| Eliminar producto | DELETE /product/{id} | ✅ | ❌ |
| Ver inventario | GET /inventory | ✅ | ✅ |
| Modificar inventario | POST/PUT /inventory | ✅ | ✅ |
| Movimientos inventario | /api/inventory-movements/** | ✅ | ❌ |
| Ventas | /sale/** | ✅ | ✅ |
| Usuarios | /user/** | ✅ | ❌ |

---

## 🛠️ Pruebas con Postman

### Configuración

1. **Crear Colección**: "OptimaStock Auth"
2. **Variables de Entorno**:
   - `base_url`: `http://localhost:8080`
   - `admin_token`: (se llenará automáticamente)
   - `empleado_token`: (se llenará automáticamente)

### Script de Login Automático

En el request de login, agrega este script en "Tests":

```javascript
// Guardar el token automáticamente
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("admin_token", jsonData.token);
}
```

### Usar el Token Automáticamente

En otros requests, usa:
```
Authorization: Bearer {{admin_token}}
```

---

## 🔍 Verificación de Token

Puedes decodificar el token JWT en [jwt.io](https://jwt.io) para ver su contenido:

**Payload del token**:
```json
{
  "authorities": [
    {
      "authority": "ROLE_ADMIN"
    }
  ],
  "sub": "admin",
  "iat": 1733431500,
  "exp": 1733517900
}
```

- `sub`: username
- `iat`: fecha de emisión
- `exp`: fecha de expiración
- `authorities`: roles del usuario

---

## ❗ Troubleshooting

### Error: "Unauthorized" (401)
- ✅ Verifica que el token esté en el header `Authorization: Bearer {token}`
- ✅ Verifica que el token no haya expirado (24h)
- ✅ Verifica que no haya espacios extras en el header

### Error: "Forbidden" (403)
- ✅ El usuario no tiene el rol adecuado
- ✅ Verifica que el rol en BD tenga el prefijo `ROLE_`

### Error: "Bad Credentials"
- ✅ Username o password incorrectos
- ✅ Verifica que la contraseña en BD esté encriptada con BCrypt

### Error al iniciar aplicación
- ✅ Verifica que `jwt.secret` tenga al menos 32 caracteres
- ✅ Revisa los logs para errores de dependencias

---

## 📝 Próximos Pasos

1. **Crear usuarios iniciales** en tu base de datos
2. **Probar el login** con Postman
3. **Verificar permisos** intentando acceder con diferentes roles
4. **Integrar frontend** para usar JWT en todas las peticiones

---

## 🔐 Seguridad en Producción

> [!IMPORTANT]
> Antes de ir a producción:
> - Cambiar `jwt.secret` a una variable de entorno
> - Usar HTTPS siempre
> - Implementar refresh tokens
> - Agregar rate limiting en /auth/login
> - Deshabilitar /auth/register o protegerlo

---

¡Ya tienes un sistema de autenticación robusto funcionando! 🎉
