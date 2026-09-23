package gamezone

class ConsolaModerna(
    codigo: String,
    marca: String,
    modelo: String,
    tipoUsuario: TipoUsuario
) : Consola(
    codigo = codigo,
    marca = marca,
    modelo = modelo,
    tipoUsuario = tipoUsuario,
    tarifaBase = 1500.0
) {

    override fun calcularCostoAntesDeIva(minutosUso: Long): Double {
        if (minutosUso < 20) {
            return 0.0
        }

        val horas = minutosUso / 60.0
        return horas * tarifaBase
    }
}
