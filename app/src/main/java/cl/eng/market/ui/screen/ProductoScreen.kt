package cl.eng.market.ui.screen

import android.app.Activity
import android.content.Context
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
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun ProductoScreen(
    productos: List<Producto>,
    error: String?,
    busquedaRealizada: Boolean,
    onBuscar: (String) -> Unit,
    onLimpiarProducto: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scanValue by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.arrow_down))
    val lottieAnimatable = rememberLottieAnimatable()
    val ttsManager = remember { TTSManager(context) }

    var productoMostrado by remember { mutableStateOf<Producto?>(null) }
    var triggerRepetido by remember { mutableStateOf(UUID.randomUUID().toString()) }

    val producto = productos.firstOrNull()

    // 🔁 Siempre reinicia cuando llega un producto nuevo o igual
    LaunchedEffect(producto?.Codigo, producto?.PrecioVenta) {
        if (producto != null) {
            triggerRepetido = UUID.randomUUID().toString()
        }
    }

    // 🗣️ Mostrar producto, hablar y limpiar después de 10s
    LaunchedEffect(triggerRepetido) {
        producto?.let {
            productoMostrado = it
            ttsManager.speak("${it.PrecioVenta} pesos")

            delay(10_000)

            productoMostrado = null
            onLimpiarProducto()
        }
    }

    // ❌ Producto no encontrado: hablar + limpiar pantalla
    LaunchedEffect(busquedaRealizada, producto) {
        if (busquedaRealizada && producto == null) {
            productoMostrado = null // 👈 LIMPIAR
            ttsManager.speak("El producto no existe. Validar en caja")
        }
    }

    // 🎯 Enfocar campo oculto y ocultar teclado
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        (context as? Activity)?.currentFocus?.windowToken?.let {
            imm.hideSoftInputFromWindow(it, 0)
        }
    }

    // ▶️ Animación loop
    LaunchedEffect(composition) {
        lottieAnimatable.animate(composition, iterations = LottieConstants.IterateForever)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
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

        // Pantalla resultado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(Color(0xFF01579B)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // TextField oculto para escáner
                TextField(
                    value = scanValue,
                    onValueChange = {
                        scanValue = it
                        if (it.isNotBlank()) {
                            onBuscar(it)
                            scanValue = ""
                        }
                    },
                    modifier = modifier
                        .focusRequester(focusRequester)
                        .size(1.dp)
                        .alpha(0f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions.Default,
                    readOnly = false
                )

                // Código del producto
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

                // Producto no encontrado
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

                // Nombre del producto
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

                // Precio
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

                // Instrucción
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

                // Animación
                LottieAnimation(
                    composition = composition,
                    progress = lottieAnimatable.progress,
                    modifier = Modifier.size(170.dp)
                )

                // Error general
                error?.let {
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

    // Cleanup TTS
    DisposableEffect(context) {
        onDispose {
            ttsManager.shutdown()
        }
    }
}
