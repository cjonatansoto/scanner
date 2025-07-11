package cl.eng.market.ui.screen

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import cl.eng.market.R
import cl.eng.market.data.model.Producto
import cl.eng.market.util.TTSManager
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "ProductoScreen"

@Composable
fun ProductoScreen(
    productos: List<Producto>,
    error: String?,
    busquedaRealizada: Boolean,
    onBuscar: (String) -> Unit,
    onLimpiarProducto: () -> Unit,
    modifier: Modifier = Modifier,
    productoEncontrado: Boolean,
    cargando: Boolean,
    codigoActual: String,
) {
    var scanValue by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.arrow_down))
    val lottieAnimatable = rememberLottieAnimatable()
    val ttsManager = remember { TTSManager(context) }

    var productoMostrado by remember { mutableStateOf<Producto?>(null) }
    var mostrarJob by remember { mutableStateOf<Job?>(null) }
    var trigger by remember { mutableStateOf(UUID.randomUUID()) }
    val producto = productos.firstOrNull()

    // Actualizar trigger y log cuando cambia producto
    LaunchedEffect(producto) {
        if (producto != null) {
            trigger = UUID.randomUUID()
            Log.d(TAG, "Producto recibido: $producto")
            Log.d(TAG, "Trigger actualizado: $trigger")
        }
    }

    // Mostrar producto y usar TTS cuando cambia trigger
    LaunchedEffect(trigger) {
        producto?.let {
            Log.d(TAG, "Nuevo producto recibido: ${it.Nombre}")
            mostrarJob?.cancel()

            productoMostrado = it
            ttsManager.speak("${it.PrecioVenta} pesos")

            mostrarJob = scope.launch {
                delay(10_000)
                Log.d(TAG, "Ocultando producto después de 10s")
                productoMostrado = null
                onLimpiarProducto()
            }
        }
    }

    // Manejar producto no encontrado y notificar TTS
    LaunchedEffect(busquedaRealizada, productoEncontrado, cargando) {
        if (busquedaRealizada && !productoEncontrado && !cargando) {
            productoMostrado = null
            Log.d(TAG, "Producto no encontrado para código: $codigoActual")
            ttsManager.speak("El producto no existe. Validar en caja")
        }
    }

    // Ocultar teclado y solicitar foco al iniciar la pantalla
    LaunchedEffect(Unit) {
        Log.d(TAG, "Solicitando foco y ocultando teclado")
        focusRequester.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        (context as? Activity)?.currentFocus?.windowToken?.let {
            imm.hideSoftInputFromWindow(it, 0)
        }
    }

    // Animación continua flecha
    LaunchedEffect(composition) {
        lottieAnimatable.animate(composition, iterations = LottieConstants.IterateForever)
    }

    // Detectar cambios en scanValue para ejecutar búsqueda y limpiar input
    LaunchedEffect(scanValue) {
        if (scanValue.isNotBlank()) {
            Log.d(TAG, "Código ingresado: $scanValue")
            onBuscar(scanValue)
            scanValue = "" // Limpia para permitir mismo código repetido
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo del Supermercado",
                modifier = Modifier.height(200.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(Color(0xFF01579B)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    value = scanValue,
                    onValueChange = { scanValue = it },
                    modifier = modifier
                        .focusRequester(focusRequester)
                        .size(1.dp)
                        .alpha(0f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions.Default,
                    readOnly = false
                )

                Text(
                    text = productoMostrado?.Codigo ?: "",
                    fontSize = 28.sp,
                    modifier = Modifier.padding(top = 16.dp),
                    style = TextStyle(
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = Offset(3f, 3f),
                            blurRadius = 6f
                        )
                    )
                )

                if (busquedaRealizada && producto == null) {
                    Text(
                        text = "EL PRODUCTO NO EXISTE\nValidar en caja",
                        fontSize = 38.sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            color = Color.Red,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 6f
                            )
                        )
                    )
                }

                if (cargando) {
                    Text(
                        text = "Buscando...",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Yellow,
                        modifier = Modifier.padding(vertical = 12.dp),
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }

                Text(
                    text = productoMostrado?.Nombre ?: "",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.4f),
                            offset = Offset(2f, 2f),
                            blurRadius = 6f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = productoMostrado?.PrecioVenta ?: "",
                    fontSize = 95.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = Offset(3f, 3f),
                            blurRadius = 12f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Acerca el producto al lector",
                    fontSize = 34.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = Offset(2f, 2f),
                            blurRadius = 7f
                        )
                    )
                )

                LottieAnimation(
                    composition = composition,
                    progress = lottieAnimatable.progress,
                    modifier = Modifier.size(170.dp)
                )

                error?.let {
                    Log.e(TAG, "Error visualizado en pantalla: $it")
                    Text(
                        text = it,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }

    DisposableEffect(context) {
        onDispose {
            Log.d(TAG, "Liberando recursos del TTS")
            ttsManager.shutdown()
        }
    }
}

