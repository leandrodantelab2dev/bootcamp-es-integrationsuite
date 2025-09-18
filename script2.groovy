import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import java.math.RoundingMode

Message processData(Message message) {
    // ✅ Recuperamos las propiedades establecidas en el primer script
    def from   = (message.getProperty("from") ?: "USD").toString()
    def to     = (message.getProperty("to")   ?: "BRL").toString()
    def amount = new BigDecimal((message.getProperty("amount") ?: "1").toString())

    // ✅ Leemos la respuesta cruda del Frankfurter como Reader (streaming)
    def reader = message.getBody(java.io.Reader)

    // ✅ Parseamos el JSON devuelto por Frankfurter:
    //    Ejemplo de estructura:
    //    { "amount": 1, "base": "USD", "date": "2025-09-17", "rates": { "BRL": 5.2981 } }
    def json = new JsonSlurper().parse(reader)

    // ✅ Calculamos la tasa (rate):
    //    - Si 'to' coincide con 'base' en la respuesta, la tasa = 1
    //    - En caso contrario, buscamos 'rates[to]'
    BigDecimal rate
    if (to.equalsIgnoreCase(json.base?.toString())) {
        rate = BigDecimal.ONE
    } else {
        def r = json.rates?."$to"
        if (r == null) {
            // ❌ Si no existe la tasa para 'to', lanzamos error para que el iFlow lo capture
            throw new RuntimeException("No se encontró tasa para ${to}. Respuesta: ${JsonOutput.toJson(json)}")
        }
        // ⚠️ Convertimos a BigDecimal para precisión en cálculos monetarios
        rate = new BigDecimal(r.toString())
    }

    // ✅ Multiplicamos monto * tasa y redondeamos a 2 decimales (HALF_UP)
    def converted = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP)

    // ✅ Armamos el JSON de salida para el cliente del iFlow
    def outJson = [
        provider : "Frankfurter",
        date     : json.date,   // fecha de la tasa devuelta por el servicio
        base     : from,        // moneda recibida del cliente (lo que pidió convertir)
        to       : to,          // moneda destino
        rate     : rate,        // tasa usada
        amount   : amount,      // monto original solicitado
        converted: converted    // monto convertido (redondeado a 2 decimales)
    ]

    // ✅ Definimos Content-Type y el cuerpo en formato JSON
    message.setHeader("Content-Type", "application/json")
    message.setBody(JsonOutput.toJson(outJson))
    return message
}
