# 💱 iFlow – Conversión de Divisas con SAP Integration Suite + Frankfurter API

Este repositorio contiene ejemplos de **scripts Groovy** utilizados en un iFlow del **SAP Integration Suite (CPI)** para integrar con la [Frankfurter API](https://www.frankfurter.app/), permitiendo conversión de divisas en tiempo real.

---

## 🚀 Funcionalidad
- Recibe solicitudes **POST** con JSON en el cuerpo (`from`, `to`, `amount`).
- Consulta la **API Frankfurter** para obtener la tasa de cambio.
- Calcula el monto convertido usando `BigDecimal` (precisión financiera).
- Devuelve un **JSON estructurado** con los detalles de la conversión.
- Incluye soporte opcional para solicitudes **GET** vía Query String.

---

## 📂 Flujo del iFlow
1. **HTTPS Sender** – expone el endpoint `/forex`.
2. **Groovy Script 1 – Extraer Parámetros**  
   - Lee `body` (POST) o `query string` (GET).  
   - Define propiedades (`from`, `to`, `amount`).
3. **Request Reply – HTTP Receiver**  
   - Llama a Frankfurter API:  
     ```
     GET https://api.frankfurter.app/latest?from={from}&to={to}
     ```
4. **Groovy Script 2 – Cálculo**  
   - Procesa la respuesta.  
   - Multiplica `amount * rate`.  
   - Construye el JSON de salida.
5. **Response** – devuelve el resultado al cliente.

---

## 📥 Ejemplo de Solicitud (POST)
```http
POST /forex
Content-Type: application/json

{
  "from": "USD",
  "to": "BRL",
  "amount": 1000
}
```

---

## 📤 Ejemplo de Respuesta
```json
{
  "provider": "Frankfurter",
  "date": "2025-09-17",
  "base": "USD",
  "to": "BRL",
  "rate": 5.2981,
  "amount": 1000,
  "converted": 5298.10
}
```

---

## ⚙️ Tecnologías
- **SAP Integration Suite (CPI)**
- **Groovy Scripts**
- **Frankfurter API**

---

## 🔒 Notas
- Uso de `message.getBody(java.io.Reader)` para parsing en streaming (mejor práctica en CPI).
- Manejo básico de errores: si la moneda no existe en la respuesta, el flujo lanza excepción.
- Puede ampliarse con logging, métricas o integración con bases de datos.

---

## 📜 Licencia
Este proyecto se publica bajo la licencia MIT. Eres libre de usarlo, modificarlo y adaptarlo según tus necesidades.
