package cl.eng.market.data.remote

import cl.eng.market.data.model.Producto
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductoApi {
    @GET("AdminDTE/includes/BuscarProductoCP.php")
    suspend fun buscarProducto(
        @Query("Codigo") codigo: String,
        @Query("Rut_Empresa") rutEmpresa: String = "77912473-8"
    ): List<Producto>
}