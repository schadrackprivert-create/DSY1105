package gamezone

sealed class EstadoPuesto {

    data object Libre : EstadoPuesto()

    data class EnJuego(
        val consola: Consola
    ) : EstadoPuesto()

    data class EnProceso(
        val motivo: String
    ) : EstadoPuesto()

    data class EnReparacion(
        val motivo: String
    ) : EstadoPuesto()
}
