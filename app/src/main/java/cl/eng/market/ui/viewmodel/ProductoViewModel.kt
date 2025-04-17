package cl.eng.market.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.eng.market.data.model.Producto
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import cl.eng.market.repository.ProductoRepository
import android.util.Log


class ProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductoRepository(application)

    private val _productos = mutableStateOf<List<Producto>>(emptyList())
    val productos: State<List<Producto>> = _productos

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _busquedaRealizada = mutableStateOf(false)
    val busquedaRealizada: State<Boolean> = _busquedaRealizada

    fun onCodigoEscaneado(codigo: String) {
        Log.d("ProductoViewModel", "onCodigoEscaneado called with: $codigo")
        _busquedaRealizada.value = true
        buscarProducto(codigo)
    }

    private fun buscarProducto(codigo: String) {
        viewModelScope.launch {
            Log.d("ProductoViewModel", "buscarProducto called with: $codigo")
            try {
                val resultado = repository.buscarProducto(codigo)
                _productos.value = resultado
                _error.value = null
                Log.d("ProductoViewModel", "Product found: $resultado")
            } catch (e: Exception) {
                _productos.value = emptyList()
                _error.value = "Error: ${e.localizedMessage}"
                Log.e("ProductoViewModel", "Error searching product: ${e.localizedMessage}")
            }
        }
    }

    // ✅ Nueva función para limpiar producto
    fun limpiarProducto() {
        Log.d("ProductoViewModel", "limpiarProducto called")
        _productos.value = emptyList()
        _busquedaRealizada.value = false
    }
}
