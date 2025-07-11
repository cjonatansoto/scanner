package cl.eng.market.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.eng.market.data.model.Producto
import cl.eng.market.repository.ProductoRepository
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ProductoViewModel"
    }

    private val repository = ProductoRepository(application)

    // Lista de productos obtenidos por búsqueda
    private val _productos = mutableStateOf<List<Producto>>(emptyList())
    val productos: State<List<Producto>> = _productos

    // Último error ocurrido
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // Indica si se ha hecho una búsqueda
    private val _busquedaRealizada = mutableStateOf(false)
    val busquedaRealizada: State<Boolean> = _busquedaRealizada

    // Indica si el producto fue encontrado
    private val _productoEncontrado = mutableStateOf(false)
    val productoEncontrado: State<Boolean> = _productoEncontrado

    // Último código escaneado
    private val _codigoActual = mutableStateOf("")
    val codigoActual: State<String> = _codigoActual

    // Indica si está esperando la respuesta del servidor
    private val _cargando = mutableStateOf(false)
    val cargando: State<Boolean> = _cargando

    /**
     * Acción que se ejecuta cuando se escanea un código.
     */
    fun onCodigoEscaneado(codigo: String) {
        Log.d(TAG, "Código escaneado: $codigo")
        _codigoActual.value = codigo
        _busquedaRealizada.value = false
        _cargando.value = true
        // Limpiar productos antes de buscar nuevo para evitar estado stale
        _productos.value = emptyList()
        buscarProducto(codigo)
    }

    /**
     * Realiza la búsqueda del producto según el código entregado.
     */
    private fun buscarProducto(codigo: String) {
        viewModelScope.launch {
            try {
                val resultado = repository.buscarProducto(codigo)
                // Copiar los productos para forzar nueva referencia y disparar recomposición
                _productos.value = resultado.map { it.copy() }
                _productoEncontrado.value = resultado.isNotEmpty()
                _busquedaRealizada.value = true
                _error.value = null
            } catch (e: Exception) {
                _productos.value = emptyList()
                _productoEncontrado.value = false
                _busquedaRealizada.value = true
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }

    /**
     * Limpia los datos de producto y estado actual de búsqueda.
     */
    fun limpiarProducto() {
        Log.d(TAG, "Limpiando producto")
        _productos.value = emptyList()
        _productoEncontrado.value = false
        _busquedaRealizada.value = false
        _error.value = null
    }
}
