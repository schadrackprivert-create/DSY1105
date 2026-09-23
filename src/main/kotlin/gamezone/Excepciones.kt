package gamezone

open class GameZoneException(message: String) : Exception(message)

class CodigoConsolaInvalidoException(codigo: String) :
    GameZoneException(
        "Código de consola inválido: $codigo. El formato válido es dos letras, dos dígitos y dos letras. Ejemplo: CC12CD."
    )

class TarifaInvalidaException(detalle: String) :
    GameZoneException("Resultado de tarifa inválido. $detalle")

class ConsolaNoEncontradaException(codigo: String) :
    GameZoneException("Consola no encontrada: $codigo.")

class SistemaSinCapacidadException :
    GameZoneException("Sistema sin capacidad. No hay puestos libres disponibles.")

class TipoUsuarioInvalidoException(detalle: String) :
    GameZoneException(detalle)

class PuestoNoDisponibleException(detalle: String) :
    GameZoneException(detalle)
