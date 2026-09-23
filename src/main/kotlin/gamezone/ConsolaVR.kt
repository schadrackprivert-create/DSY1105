package gamezone

class ConsolaVR(
    codigo: String,
    marca: String,
    modelo: String,
    tipoUsuario: TipoUsuario,
    val accesoriosPremium: Boolean
) : Consola(
    codigo = codigo,
    marca = marca,
    modelo = modelo,
    tipoUsuario = tipoUsuario,
    tarifaBase = 3000.0
) {

    override fun calcularCostoAntesDeIva(minutosUso: Long): Double {
        val horas = minutosUso / 60.0
        var costo = horas * tarifaBase

        if (accesoriosPremium) {
            costo *= 1.30
        }

        return costo
    }

    override fun detalle(): String {
        val premium = if (accesoriosPremium) "sí" else "no"
        return "${super.detalle()} | accesorios premium=$premium"
    }
}
