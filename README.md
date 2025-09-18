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
