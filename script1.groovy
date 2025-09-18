import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonSlurper

Message processData(Message message) {
    // ✅ Leemos el cuerpo como Reader para procesar en streaming (mejor práctica en CPI)
    //    Evita cargar todo el payload en memoria como String.
    def reader = message.getBody(java.io.Reader)

    // ✅ Parseamos el JSON del body; si está vacío o nulo, usamos un mapa vacío
    //    JsonSlurper.parse(Reader) soporta streaming y es eficiente.
    def j = reader ? new JsonSlurper().parse(reader) : [:]

    // ✅ Tomamos los campos esperados aplicando defaults y normalizando:
    //    - from: moneda base (por defecto USD), en mayúsculas
    //    - to:   moneda destino (por defecto BRL), en mayúsculas
    //    - amount: cantidad a convertir; guardamos como String para decidir luego el tipo
    def from = (j.from ?: "USD").toUpperCase()
    def to   = (j.to   ?: "BRL").toUpperCase()
    def amountStr = (j.amount ?: "1").toString()

    // ✅ Guardamos en propiedades del intercambio (Exchange Properties)
    //    para que estén disponibles en pasos siguientes del iFlow.
    message.setProperty("from", from)
    message.setProperty("to", to)
    message.setProperty("amount", amountStr)

    // ✅ Retornamos el mensaje para continuar el flujo.
    return message
}
