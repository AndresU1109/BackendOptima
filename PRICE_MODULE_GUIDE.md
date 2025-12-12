# Guía del Módulo de Cotizaciones (Price)

El módulo de cotizaciones permite crear, gestionar y generar PDFs de cotizaciones usando MongoDB.

---

## 🗄️ Base de Datos Híbrida

OptimaStock ahora usa:
- **MySQL**: Productos, inventario, usuarios, ventas (JPA)
- **MongoDB**: Cotizaciones (MongoDB)

**MongoDB local**: `mongodb://localhost:27017/optimastock_quotes`

---

## 📋 Modelo de Datos MongoDB

### Quote (Cotización)

```json
{
  "_id": "657f1a2b3c4d5e6f7a8b9c0d",
  "quoteNumber": "COT-2025-0001",
  "customerInfo": {
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "phone": "+57 300 123 4567",
    "company": "Constructora ABC",
    "address": "Calle 123 #45-67"
  },
  "items": [
    {
      "productId": 1,
      "productCode": "PROD001",
      "productName": "Martillo Carpintero",
      "quantity": 10,
      "unitPrice": 25000,
      "subtotal": 250000,
      "iva": 19,
      "ivaAmount": 47500,
      "total": 297500
    }
  ],
  "summary": {
    "subtotal": 250000,
    "totalIva": 47500,
    "total": 297500,
    "itemCount": 1
  },
  "status": "PENDING",
  "validUntil": "2025-12-21T23:59:59",
  "notes": "Entrega en Bogotá",
  "createdAt": "2025-12-06T14:30:00",
  "createdBy": "sistema",
  "updatedAt": "2025-12-06T14:30:00"
}
```

---

## 🚀 Endpoints Disponibles

### 1. Crear Cotización (PÚBLICO)

```http
POST http://localhost:8080/api/quotes
Content-Type: application/json

{
  "customerInfo": {
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "phone": "+57 300 123 4567",
    "company": "Constructora ABC",
    "address": "Calle 123 #45-67"
  },
  "items": [
    {
      "productId": 1,
      "quantity": 10
    },
    {
      "productId": 5,
      "quantity": 2
    }
  ],
  "notes": "Entrega en Bogotá"
}
```

**Respuesta** (HTTP 201):
```json
{
  "id": "657f1a2b3c4d5e6f7a8b9c0d",
  "quoteNumber": "COT-2025-0001",
  "customerInfo": {...},
  "items": [...],
  "summary": {
    "subtotal": 610000,
    "totalIva": 115900,
    "total": 725900,
    "itemCount": 2
  },
  "status": "PENDING",
  "validUntil": "2025-12-21T14:30:00",
  ...
}
```

---

### 2. Obtener Cotización por ID (PÚBLICO)

```http
GET http://localhost:8080/api/quotes/{id}
```

---

### 3. Descargar PDF (PÚBLICO)

```http
GET http://localhost:8080/api/quotes/{id}/pdf
```

**Genera y descarga**: `cotizacion-COT-2025-0001.pdf`

---

### 4. Listar Cotizaciones (ADMIN/EMPLEADO)

```http
GET http://localhost:8080/api/quotes
Authorization: Bearer {TOKEN}
```

---

### 5. Buscar por Email

```http
GET http://localhost:8080/api/quotes/customer/juan@example.com
```

---

### 6. Buscar por Estado (ADMIN/EMPLEADO)

```http
GET http://localhost:8080/api/quotes/status/PENDING
Authorization: Bearer {TOKEN}
```

---

### 7. Actualizar Estado (ADMIN/EMPLEADO)

```http
PATCH http://localhost:8080/api/quotes/{id}/status?status=APPROVED
Authorization: Bearer {TOKEN}
```

Estados disponibles: `PENDING`, `APPROVED`, `REJECTED`, `EXPIRED`

---

### 8. Eliminar Cotización (ADMIN)

```http
DELETE http://localhost:8080/api/quotes/{id}
Authorization: Bearer {TOKEN_ADMIN}
```

---

## 🔐 Permisos

| Endpoint | Público | Admin | Empleado |
|----------|---------|-------|----------|
| POST /api/quotes | ✅ | ✅ | ✅ |
| GET /api/quotes/{id} | ✅ | ✅ | ✅ |
| GET /api/quotes/{id}/pdf | ✅ | ✅ | ✅ |
| GET /api/quotes | ❌ | ✅ | ✅ |
| PATCH /api/quotes/{id}/status | ❌ | ✅ | ✅ |
| DELETE /api/quotes/{id} | ❌ | ✅ | ❌ |

---

## 📝 Contenido del PDF

El PDF generado incluye:

1. **Header**: Logo (OptimaStock), número de cotización, fecha, validez
2. **Info Cliente**: Nombre, empresa, email, teléfono, dirección
3. **Tabla Productos**: Código, nombre, cantidad, precio unitario, IVA, total
4. **Resumen**: Subtotal, IVA total, Total general
5. **Notas**: Observaciones adicionales
6. **Footer**: Agradecimiento y firma

---

## 🧪 Pruebas con Postman

### Collection: OptimaStock - Quotes

**1. Crear Cotización desde Sitio Web**
```
POST http://localhost:8080/api/quotes
Sin autenticación
Body: {customerInfo, items, notes}
```

**2. Descargar PDF**
```
GET http://localhost:8080/api/quotes/{{quoteId}}/pdf
Sin autenticación
```

**3. Admin: Ver todas las cotizaciones**
```
GET http://localhost:8080/api/quotes
Authorization: Bearer {{admin_token}}
```

**4. Aprobar Cotización**
```
PATCH http://localhost:8080/api/quotes/{{quoteId}}/status?status=APPROVED
Authorization: Bearer {{admin_token}}
```

---

## 💡 Flujo de Uso

### Desde el Sitio Web (Cliente)

1. Cliente selecciona productos en el catálogo web
2. Frontend envía POST a `/api/quotes` (sin auth)
3. Sistema:
   - Genera número único `COT-2025-XXXX`
   - Obtiene precios e IVA desde MySQL
   - Calcula totales automáticamente
   - Guarda en MongoDB
   - Retorna cotización completa
4. Cliente recibe email con enlace al PDF
5. Cliente puede descargar PDF desde `/api/quotes/{id}/pdf`

### Desde el Backend (Admin/Empleado)

1. Login para obtener token
2. Ver todas las cotizaciones
3. Filtrar por estado / cliente
4. Aprobar/Rechazar cotizaciones
5. Generar reportes

---

## 🔍 Verificar en MongoDB

Usa **MongoDB Compass** para visualizar:

```
mongodb://localhost:27017
Database: optimastock_quotes
Collection: quotes
```

---

## ⚙️ Configuración

### application.properties

```properties
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/optimastock_quotes
spring.data.mongodb.database=optimastock_quotes

# MySQL (se mantiene para productos, inventario, etc.)
spring.datasource.url=jdbc:mysql://localhost:3306/inventorybasefinal
```

---

## ✨ Características

✅ **Integración MySQL + MongoDB**: Productos desde MySQL, cotizaciones en MongoDB  
✅ **Cálculo Automático**: Precios, IVA, totales se calculan automáticamente  
✅ **PDF Profesional**: iText 7 con formato limpio y legible  
✅ **Público/Protegido**: Endpoints públicos para clientes, protegidos para gestión  
✅ **Validaciones**: Jakarta Bean Validation en todos los DTOs  
✅ **Numeración Automática**: COT-YYYY-XXXX incremental  
✅ **Búsquedas**: Por email, estado, fecha  

---

¡El módulo de cotizaciones está listo para usar! 🎉
