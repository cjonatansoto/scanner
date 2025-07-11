package cl.eng.market

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.eng.market.ui.screen.ProductoScreen
import cl.eng.market.ui.theme.ConsultarPrecioTheme
import cl.eng.market.ui.viewmodel.ProductoViewModel

import cl.eng.market.ui.viewmodel.ProductoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemUI()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        setContent {
            ConsultarPrecioTheme {
                val viewModel: ProductoViewModel = viewModel(
                    factory = ProductoViewModelFactory(application)
                )

                val productos by viewModel.productos
                val error by viewModel.error
                val busquedaRealizada by viewModel.busquedaRealizada // 👈 AÑADIDO

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProductoScreen(
                        productos = productos,
                        error = error,
                        busquedaRealizada = busquedaRealizada, // 👈 AÑADIDO
                        onBuscar = { codigo -> viewModel.onCodigoEscaneado(codigo) },
                        onLimpiarProducto = { viewModel.limpiarProducto() },
                        modifier = Modifier.padding(innerPadding),
                        productoEncontrado = viewModel.productoEncontrado.value, // ✅ Valor real
                        codigoActual = viewModel.codigoActual.value,
                        cargando = viewModel.cargando.value
                    )
                }
            }
        }
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(
                    android.view.WindowInsets.Type.statusBars() or
                            android.view.WindowInsets.Type.navigationBars()
                )
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
}
