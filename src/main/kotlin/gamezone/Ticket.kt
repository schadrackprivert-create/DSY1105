package gamezone

import java.time.LocalDateTime

data class Ticket(
    val numero: Int,
    val tipoConsola: String,
    val codigoConsola: String,
    val tipoUsuario: TipoUsuario,
    val minutosUso: Long,
    val montoPagado: Double,
    val fechaEmision: LocalDateTime = LocalDateTime.now()
)
