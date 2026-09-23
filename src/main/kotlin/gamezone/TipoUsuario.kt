package gamezone

enum class TipoUsuario {
    INFANTIL,
    SOCIO,
    EDUCACIONAL;

    companion object {
        fun desdeTexto(valor: String): TipoUsuario {
            return when (valor.trim().lowercase()) {
                "infantil" -> INFANTIL
                "socio" -> SOCIO
                "educacional" -> EDUCACIONAL
                else -> throw TipoUsuarioInvalidoException(
                    "Tipo de usuario inválido: $valor. Use infantil, socio o educacional."
                )
            }
        }
    }
}
