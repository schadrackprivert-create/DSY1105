package gamezone

import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

class GameZone(
    val nombre: String = "GameZone",
    capacidad: Int = 10
) {

    private val puestos = MutableList(capacidad) { indice ->
        Puesto(numero = indice + 1)
    }

    private val historial = mutableListOf<RegistroSesion>()

    private val recaudacionPorTipo = mutableMapOf(
        "ConsolaClasica" to 0.0,
        "ConsolaModerna" to 0.0,
        "ConsolaVR" to 0.0
    )

    private var recaudacionTotal = 0.0
    private var siguienteTicket = 1

    suspend fun registrarEntrada(consola: Consola) {
        val puesto = buscarPrimerPuestoLibre()
            ?: throw SistemaSinCapacidadException()

        puesto.estado = EstadoPuesto.EnProceso(
            "Registrando entrada de ${consola.codigo}"
        )

        println(
            "Puesto ${puesto.numero}: EnProceso. " +
                "Esperando confirmación del sensor de entrada..."
        )

        delay(3_000)

        puesto.estado = EstadoPuesto.EnJuego(consola)

        println(
            "Entrada confirmada. ${consola.codigo} quedó en el puesto ${puesto.numero}."
        )
    }

    suspend fun registrarSalida(
        codigoConsola: String,
        minutosUso: Long
    ): Ticket {

        val puesto = buscarPuestoConConsola(codigoConsola)
            ?: throw ConsolaNoEncontradaException(codigoConsola)

        val estadoAnterior = puesto.estado

        val consola = when (estadoAnterior) {
            EstadoPuesto.Libre ->
                throw ConsolaNoEncontradaException(codigoConsola)

            is EstadoPuesto.EnJuego ->
                estadoAnterior.consola

            is EstadoPuesto.EnProceso ->
                throw PuestoNoDisponibleException(
                    "El puesto ${puesto.numero} está procesando otra operación."
                )

            is EstadoPuesto.EnReparacion ->
                throw PuestoNoDisponibleException(
                    "El puesto ${puesto.numero} está en reparación."
                )
        }

        puesto.estado = EstadoPuesto.EnProceso(
            "Calculando tarifa de ${consola.codigo}"
        )

        println(
            "Puesto ${puesto.numero}: EnProceso. Calculando tarifa..."
        )

        delay(6_500)

        try {
            val monto = consola.calcularMontoFinal(minutosUso)

            val ticket = Ticket(
                numero = siguienteTicket++,
                tipoConsola = consola::class.simpleName ?: "Consola",
                codigoConsola = consola.codigo,
                tipoUsuario = consola.tipoUsuario,
                minutosUso = minutosUso,
                montoPagado = monto
            )

            historial.add(
                RegistroSesion(
                    consola = consola,
                    ticket = ticket
                )
            )

            recaudacionTotal += monto

            val tipo = ticket.tipoConsola
            recaudacionPorTipo[tipo] =
                recaudacionPorTipo.getOrDefault(tipo, 0.0) + monto

            puesto.estado = EstadoPuesto.Libre

            println("Salida completada. Puesto ${puesto.numero} liberado.")
            imprimirTicket(ticket)

            return ticket

        } catch (error: GameZoneException) {
            // Si falla el cálculo, la consola vuelve a quedar en juego.
            // Así el sistema continúa operativo y no pierde su estado.
            puesto.estado = EstadoPuesto.EnJuego(consola)
            throw error
        }
    }

    fun ponerPuestoEnReparacion(
        numeroPuesto: Int,
        motivo: String
    ) {
        val puesto = obtenerPuesto(numeroPuesto)

        when (puesto.estado) {
            EstadoPuesto.Libre -> {
                puesto.estado = EstadoPuesto.EnReparacion(motivo)
            }

            is EstadoPuesto.EnJuego -> {
                throw PuestoNoDisponibleException(
                    "No se puede enviar el puesto $numeroPuesto a reparación porque está en juego."
                )
            }

            is EstadoPuesto.EnProceso -> {
                throw PuestoNoDisponibleException(
                    "No se puede enviar el puesto $numeroPuesto a reparación porque está en proceso."
                )
            }

            is EstadoPuesto.EnReparacion -> {
                throw PuestoNoDisponibleException(
                    "El puesto $numeroPuesto ya está en reparación."
                )
            }
        }
    }

    fun liberarPuestoDeReparacion(numeroPuesto: Int) {
        val puesto = obtenerPuesto(numeroPuesto)

        when (puesto.estado) {
            EstadoPuesto.Libre -> {
                throw PuestoNoDisponibleException(
                    "El puesto $numeroPuesto ya está libre."
                )
            }

            is EstadoPuesto.EnJuego -> {
                throw PuestoNoDisponibleException(
                    "El puesto $numeroPuesto está en juego."
                )
            }

            is EstadoPuesto.EnProceso -> {
                throw PuestoNoDisponibleException(
                    "El puesto $numeroPuesto está en proceso."
                )
            }

            is EstadoPuesto.EnReparacion -> {
                puesto.estado = EstadoPuesto.Libre
            }
        }
    }

    fun cantidadPuestosDisponibles(): Int {
        return puestos.count { puesto ->
            when (puesto.estado) {
                EstadoPuesto.Libre -> true
                is EstadoPuesto.EnJuego -> false
                is EstadoPuesto.EnProceso -> false
                is EstadoPuesto.EnReparacion -> false
            }
        }
    }

    fun consolasDeSocios(): List<Consola> {
        return historial
            .map { it.consola }
            .filter { it.tipoUsuario == TipoUsuario.SOCIO }
    }

    fun ingresoPromedioPorConsola(): Double {
        if (historial.isEmpty()) return 0.0
        return recaudacionTotal / historial.size
    }

    fun codigosConsolasFinalizadas(): List<String> {
        return historial.map { it.consola.codigo }
    }

    fun consolaConMayorTiempoUso(): RegistroSesion? {
        return historial.maxByOrNull { it.ticket.minutosUso }
    }

    fun mostrarEstados() {
        println("\nESTADO DE LOS PUESTOS")

        puestos.forEach { puesto ->
            println("Puesto ${puesto.numero}: ${puesto.descripcionEstado()}")
        }
    }

    fun mostrarConsultasDeNegocio() {
        println("\nCONSULTAS DE NEGOCIO")

        println(
            "Puestos disponibles: ${cantidadPuestosDisponibles()}"
        )

        val socios = consolasDeSocios()

        println(
            "Consolas de clientes socio: " +
                if (socios.isEmpty()) {
                    "ninguna"
                } else {
                    socios.joinToString { it.codigo }
                }
        )

        println(
            "Ingreso promedio por consola: " +
                formatoDinero(ingresoPromedioPorConsola())
        )

        val finalizadas = codigosConsolasFinalizadas()

        println(
            "Códigos finalizados: " +
                if (finalizadas.isEmpty()) {
                    "ninguno"
                } else {
                    finalizadas.joinToString()
                }
        )

        val mayorTiempo = consolaConMayorTiempoUso()

        if (mayorTiempo == null) {
            println("Consola con mayor tiempo de uso: sin datos")
        } else {
            println(
                "Consola con mayor tiempo de uso: " +
                    "${mayorTiempo.consola.codigo}, " +
                    "${mayorTiempo.ticket.minutosUso} minutos"
            )
        }
    }

    fun reporteCierreTurno() {
        println("\n======================================")
        println("REPORTE DE CIERRE DE TURNO - $nombre")
        println("======================================")

        if (historial.isEmpty()) {
            println("No se atendieron consolas durante el turno.")
        } else {
            historial.forEach { registro ->
                val ticket = registro.ticket

                println(
                    "Ticket ${ticket.numero} | " +
                        "${ticket.tipoConsola} | " +
                        "${ticket.codigoConsola} | " +
                        "${ticket.minutosUso} min | " +
                        formatoDinero(ticket.montoPagado)
                )
            }
        }

        val tipoMayorIngreso = recaudacionPorTipo
            .maxByOrNull { it.value }
            ?.takeIf { it.value > 0.0 }
            ?.key
            ?: "Sin datos"

        println("--------------------------------------")
        println("Total recaudado: ${formatoDinero(recaudacionTotal)}")
        println("Consolas atendidas: ${historial.size}")
        println(
            "Ingreso promedio: " +
                formatoDinero(ingresoPromedioPorConsola())
        )
        println("Tipo con más ingresos: $tipoMayorIngreso")
        println(
            "Puestos disponibles al cierre: " +
                cantidadPuestosDisponibles()
        )
        println("======================================")
    }

    private fun buscarPrimerPuestoLibre(): Puesto? {
        return puestos.firstOrNull { puesto ->
            when (puesto.estado) {
                EstadoPuesto.Libre -> true
                is EstadoPuesto.EnJuego -> false
                is EstadoPuesto.EnProceso -> false
                is EstadoPuesto.EnReparacion -> false
            }
        }
    }

    private fun buscarPuestoConConsola(
        codigoConsola: String
    ): Puesto? {
        return puestos.firstOrNull { puesto ->
            when (val estado = puesto.estado) {
                EstadoPuesto.Libre ->
                    false

                is EstadoPuesto.EnJuego ->
                    estado.consola.codigo.equals(
                        codigoConsola,
                        ignoreCase = true
                    )

                is EstadoPuesto.EnProceso ->
                    false

                is EstadoPuesto.EnReparacion ->
                    false
            }
        }
    }

    private fun obtenerPuesto(numeroPuesto: Int): Puesto {
        return puestos.firstOrNull {
            it.numero == numeroPuesto
        } ?: throw PuestoNoDisponibleException(
            "El puesto $numeroPuesto no existe."
        )
    }

    private fun imprimirTicket(ticket: Ticket) {
        println("\nTICKET N° ${ticket.numero}")
        println("Tipo: ${ticket.tipoConsola}")
        println("Código: ${ticket.codigoConsola}")
        println("Tiempo: ${ticket.minutosUso} minutos")
        println("Monto pagado: ${formatoDinero(ticket.montoPagado)}")
    }

    private fun formatoDinero(valor: Double): String {
        val formato = NumberFormat.getCurrencyInstance(
            Locale("es", "CL")
        )
        formato.maximumFractionDigits = 0
        formato.minimumFractionDigits = 0
        return formato.format(valor)
    }
}
