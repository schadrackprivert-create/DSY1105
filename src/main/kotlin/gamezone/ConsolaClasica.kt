package gamezone

class ConsolaClasica(
    codigo: String,
    marca: String,
    modelo: String,
    tipoUsuario: TipoUsuario
) : Consola(
    codigo = codigo,
    marca = marca,
    modelo = modelo,
    tipoUsuario = tipoUsuario,
    tarifaBase = 800.0
) {

    override fun calcularCostoAntesDeIva(minutosUso: Long): Double {
        val horas = minutosUso / 60.0
        var costo = horas * tarifaBase

        if (tipoUsuario == TipoUsuario.SOCIO) {
            costo *= 0.80
        }

        return costo
    }
}
