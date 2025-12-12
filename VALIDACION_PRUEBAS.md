# Guía de Prueba de Validaciones - OptimaStock

## URLs Correctas de los Endpoints

Basado en tu configuración actual:

### Product
- **URL Base**: `http://localhost:8080/product`
- **POST** (crear): `POST http://localhost:8080/product`
- **PUT** (actualizar): `PUT http://localhost:8080/product/{id}`

### Inventory  
- **URL Base**: `http://localhost:8080/inventory`
- **POST** (crear): `POST http://localhost:8080/inventory`
- **PUT** (actualizar): `PUT http://localhost:8080/inventory/{id}`

### Inventory Movement
- **URL Base**: `http://localhost:8080/api/inventory-movements`
- **POST** (crear): `POST http://localhost:8080/api/inventory-movements`
- **PUT** (actualizar): `PUT http://localhost:8080/api/inventory-movements/{id}`

---

## Prueba 1: Crear Producto con Datos Inválidos

```http
POST http://localhost:8080/product
Content-Type: application/json

{
  "code": "",
  "name": "",
  "minQuantity": -5,
  "cost": -100,
  "price": 50
}
```

**Respuesta Esperada**: HTTP 400 Bad Request
```json
{
  "timestamp": "2025-12-05T...",
  "status": 400,
  "error": "Error de Validación",
  "message": "Los datos enviados no cumplen con las validaciones requeridas",
  "validationErrors": {
    "code": "El código del producto es requerido",
    "name": "El nombre del producto es requerido",
    "minQuantity": "La cantidad mínima no puede ser negativa",
    "cost": "El costo no puede ser negativo",
    "category": "La categoría es requerida",
    "iva": "El IVA es requerido"
  }
}
```

---

## Prueba 2: Crear Producto con Datos Válidos

Primero asegúrate de tener una categoría e IVA existentes en tu base de datos.

```http
POST http://localhost:8080/product
Content-Type: application/json

{
  "code": "PROD001",
  "name": "Martillo Carpintero",
  "description": "Martillo de acero para carpintería",
  "category": {"id": 1},
  "minQuantity": 10,
  "cost": 5000,
  "price": 8000,
  "iva": {"idIVA": 1},
  "status": 1
}
```

**Respuesta Esperada**: HTTP 200 OK con el producto creado

---

## Verificación de laAplicación

### 1. Verifica que la aplicación esté corriendo
Abre: `http://localhost:8080/product` en tu navegador
- Si ves una lista JSON (aunque vacía `[]`), la app está corriendo
- Si ves error de conexión, la app no está corriendo

### 2. Revisa los logs de la aplicación
En la consola donde corre Spring Boot, busca:
```
Started OptimaStockApplication in X.XXX seconds
```

### 3. Si hay errores al iniciar
Busca en los logs líneas que contengan:
- `ERROR`
- `Exception`
- `Failed to`

---

## Comandos Útiles

### Rebuild completo
```bash
cd c:\Users\Victus\Desktop\backendOptimaStock\backendOptimaStock
mvn clean install -DskipTests
```

### Iniciar aplicación desde Maven (alternativa)
```bash
cd c:\Users\Victus\Desktop\backendOptimaStock\backendOptimaStock\app  
mvn spring-boot:run
``

---

## Checklist de Troubleshooting

- [ ] ¿La aplicación está corriendo? (verifica `http://localhost:8080/product`)
- [ ] ¿Usaste la URL correcta `/product` en lugar de `/api/products`?
- [ ] ¿Env iaste `Content-Type: application/json` en el header?
- [ ] ¿El JSON está bien formado? (sin comas extras, comillas correctas)
- [ ] ¿Hay errores en la consola de Spring Boot?

---

## Ejemplo Completo en Postman

1. **Abre Postman**
2. **Crea nueva petición**:
   - Method: `POST`
   - URL: `http://localhost:8080/product`
   - Headers: `Content-Type: application/json`
   - Body (raw, JSON):
   ```json
   {
     "code": "",
     "name": "",
     "minQuantity": -5
   }
   ```
3. **Click Send**
4. **Deberías ver HTTP 400** con mensajes de error de validación

---

## Si Aún No Funciona

Envíame:
1. La URL exacta que estás usando
2. El JSON completo que estás enviando
3. La respuesta HTTP completa que recibes (código y body)
4. Los últimos 20-30 líneas de la consola de Spring Boot
