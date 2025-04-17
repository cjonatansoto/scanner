package cl.eng.market.data.model

data class Producto(
    val Codigo: String,
    val Nombre: String,
    val PrecioVenta: String,
    val RutaFoto1: String,
    val RutaFoto2: String,
    val RutaFoto3: String,
    val PrecioXMayor: String,
    val CantidadXMayor: String
)