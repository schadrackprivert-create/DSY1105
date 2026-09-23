package gamezone

data class Puesto(
    val numero: Int,
    var estado: EstadoPuesto = EstadoPuesto.Libre
) {

    fun descripcionEstado(): String {
        return when (val actual = estado) {
            EstadoPuesto.Libre ->
                "Libre"

            is EstadoPuesto.EnJuego ->
                "EnJuego con ${actual.consola.codigo}"

            is EstadoPuesto.EnProceso ->
                "EnProceso: ${actual.motivo}"

            is EstadoPuesto.EnReparacion ->
                "EnReparación: ${actual.motivo}"
        }
    }

    fun permiteEntrada(): Boolean {
        return when (estado) {
            EstadoPuesto.Libre -> true
            is EstadoPuesto.EnJuego -> false
            is EstadoPuesto.EnProceso -> false
            is EstadoPuesto.EnReparacion -> false
        }
    }
}
