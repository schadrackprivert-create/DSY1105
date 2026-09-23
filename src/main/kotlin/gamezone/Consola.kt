package gamezone

import java.time.LocalDateTime

abstract class Consola(
    val codigo: String,
    val marca: String,
    val modelo: String,
    val fechaHoraIngreso: LocalDateTime = LocalDateTime.now(),
    val tipoUsuario: TipoUsuario,
    val tarifaBase: Double
) {

    init {
        validarCodigo(codigo)

        if (tarifaBase <= 0) {
            throw TarifaInvalidaException(
                "La tarifa base debe ser mayor que cero. Valor recibido: $tarifaBase"
            )
        }
    }

    abstract fun calcularCostoAntesDeIva(minutosUso: Long): Double

    open fun detalle(): String {
        return "${this::class.simpleName} | código=$codigo | $marca $modelo | usuario=$tipoUsuario"
    }

    fun calcularMontoFinal(minutosUso: Long): Double {
        val costoAntesDeIva = calcularCostoAntesDeIva(minutosUso)

        val esSesionGratisValida =
            this is ConsolaModerna && minutosUso in 0..19 && costoAntesDeIva == 0.0

        if (costoAntesDeIva < 0 || (costoAntesDeIva == 0.0 && !esSesionGratisValida)) {
            throw TarifaInvalidaException(
                "El cálculo produjo un monto negativo o igual a cero cuando no corresponde."
            )
        }

        val montoConIva = costoAntesDeIva * 1.19

        return if (tipoUsuario == TipoUsuario.EDUCACIONAL) {
            montoConIva * 0.50
        } else {
            montoConIva
        }
    }

    private fun validarCodigo(codigo: String) {
        val expresion = Regex("^[A-Za-z]{2}[0-9]{2}[A-Za-z]{2}$")

        if (!expresion.matches(codigo)) {
            throw CodigoConsolaInvalidoException(codigo)
        }
    }
}
