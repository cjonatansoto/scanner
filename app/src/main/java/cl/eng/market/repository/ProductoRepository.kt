package cl.eng.market.repository

import android.content.Context
import cl.eng.market.data.model.Producto
import cl.eng.market.data.remote.RetrofitClient

class ProductoRepository(private val context: Context) {
    private val api = RetrofitClient.getInstance(context)

    suspend fun buscarProducto(codigo: String): List<Producto> {
        return api.buscarProducto(codigo)
    }
}