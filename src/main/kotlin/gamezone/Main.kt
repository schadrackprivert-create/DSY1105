package gamezone

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    val sistema = GameZone()

    println("======================================")
    println("       SISTEMA GAMEZONE")
    println("======================================")

    val consola1 = ConsolaClasica(
        codigo = "CC12CD",
        marca = "Sony",
        modelo = "PlayStation 5",
        tipoUsuario = TipoUsuario.SOCIO
    )

    val consola2 = ConsolaClasica(
        codigo = "CC99ZA",
        marca = "Microsoft",
        modelo = "Xbox Series X",
        tipoUsuario = TipoUsuario.INFANTIL
    )

    val consola3 = ConsolaModerna(
        codigo = "CM22TO",
        marca = "Nintendo",
        modelo = "Switch",
        tipoUsuario = TipoUsuario.INFANTIL
    )

    val consola4 = ConsolaVR(
        codigo = "VR44RG",
        marca = "Meta",
        modelo = "Quest 3",
        tipoUsuario = TipoUsuario.EDUCACIONAL,
        accesoriosPremium = true
    )

    val consola5 = ConsolaVR(
        codigo = "VR77RG",
        marca = "HTC",
        modelo = "Vive Pro",
        tipoUsuario = TipoUsuario.INFANTIL,
        accesoriosPremium = false
    )

    // Entradas sugeridas por el enunciado.
    ejecutarSeguro {
        sistema.registrarEntrada(consola1)
    }

    ejecutarSeguro {
        sistema.registrarEntrada(consola2)
    }

    ejecutarSeguro {
        sistema.registrarEntrada(consola3)
    }

    ejecutarSeguro {
        sistema.registrarEntrada(consola4)
    }

    ejecutarSeguro {
        sistema.registrarEntrada(consola5)
    }

    sistema.mostrarEstados()

    // Prueba de error: código con formato inválido.
    ejecutarSeguro {
        val consolaInvalida = ConsolaClasica(
            codigo = "123ABC",
            marca = "Marca",
            modelo = "Modelo",
            tipoUsuario = TipoUsuario.INFANTIL
        )

        sistema.registrarEntrada(consolaInvalida)
    }

    // Salidas y tiempos sugeridos por el enunciado.
    ejecutarSeguro {
        sistema.registrarSalida("CC12CD", 75)
    }

    ejecutarSeguro {
        sistema.registrarSalida("CC99ZA", 180)
    }

    ejecutarSeguro {
        sistema.registrarSalida("CM22TO", 18)
    }

    ejecutarSeguro {
        sistema.registrarSalida("VR44RG", 120)
    }

    ejecutarSeguro {
        sistema.registrarSalida("VR77RG", 45)
    }

    // Prueba de error: consola no encontrada.
    ejecutarSeguro {
        sistema.registrarSalida("ZZ99ZZ", 60)
    }

    sistema.mostrarEstados()
    sistema.mostrarConsultasDeNegocio()
    sistema.reporteCierreTurno()
}

suspend fun ejecutarSeguro(
    operacion: suspend () -> Unit
) {
    try {
        operacion()
    } catch (error: GameZoneException) {
        println("\nERROR: ${error.message}")
        println("El sistema continúa funcionando.\n")
    }
}
