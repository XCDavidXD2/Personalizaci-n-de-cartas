package com.example.businesscard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.drawable.toBitmap
import com.example.businesscard.ui.theme.BusinessCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BusinessCardTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PersonalizarCarta()
                }
            }
        }
    }
}

@Preview
@Composable
fun PersonalizarCarta() {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf<ImageBitmap?>(null) }
    var colorFondo by remember { mutableStateOf(Color.Green) }

    var nombreHabilitado by remember { mutableStateOf(false) }
    var descripcionHabilitada by remember { mutableStateOf(false) }
    var imagenHabilitada by remember { mutableStateOf(false) }
    var modoOscuroHabilitado by remember { mutableStateOf(false) } // Estado del modo oscuro

    // Estado para las estrellas
    var cantidadEstrellas by remember { mutableStateOf(1) }

    val contexto = LocalContext.current


    // Cambiar colores según el estado del modo oscuro
    val colorFondoPantalla = if (modoOscuroHabilitado) Color.Black else Color.White
    val colorTexto = if (modoOscuroHabilitado) Color.White else Color.Black


    val lanzadorSelectorImagen = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = contexto.contentResolver.openInputStream(uri)?.use { inputStream ->
                android.graphics.BitmapFactory.decodeStream(inputStream)
            }?.asImageBitmap()
            imagen = bitmap
        }
    }


    // Lógica para cambiar la cantidad de estrellas
    val manejarClickEstrella = {
        cantidadEstrellas = when (cantidadEstrellas) {
            1 -> 2
            2 -> 3
            else -> 1
        }
    }

    // Cantidad de campos habilitados

    val camposHabilitados = listOf(nombreHabilitado, descripcionHabilitada, imagenHabilitada).count { it }
    val camposCompletados = listOf(
        nombreHabilitado && nombre.isNotBlank(),
        descripcionHabilitada && descripcion.isNotBlank(),
        imagenHabilitada && imagen != null
    ).count { it }
    val progreso = if (camposHabilitados == 0) 0f else camposCompletados / camposHabilitados.toFloat()
    val botonHabilitado = progreso == 1f

    val estadoDesplazamiento = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondoPantalla) // Fondo cambia según el modo oscuro
            .verticalScroll(estadoDesplazamiento)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Customiza tu carta", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorTexto)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    BasicTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                        },
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorTexto // Cambia el color del texto según el modo oscuro
                        ),
                        modifier = Modifier
                            .width(320.dp)
                            .padding(top = 16.dp)
                            .background(colorFondo, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .then(if (!nombreHabilitado) Modifier.background(Color.Gray) else Modifier),
                        enabled = nombreHabilitado
                    )

                    Spacer(modifier = Modifier.height(8.dp))


                    // Estrellas clickeables
                    Row {
                        repeat(cantidadEstrellas) {
                            Icon(
                                painter = painterResource(id = R.drawable._76020),
                                contentDescription = "Estrella",
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { manejarClickEstrella() }
                                    .padding(2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // IMAGEN POKEMON

                    Box(
                        modifier = Modifier
                            .width(320.dp)
                            .height(200.dp)
                            .padding(3.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .clickable {
                                if (imagenHabilitada) {
                                    lanzadorSelectorImagen.launch("image/*")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagen != null) {
                            Image(
                                bitmap = imagen!!,
                                contentDescription = "Imagen del Pokémon",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(text = "Haz clic para elegir una imagen", color = colorTexto)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    BasicTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        textStyle = TextStyle(fontSize = 16.sp, color = colorTexto),
                        modifier = Modifier
                            .width(320.dp)
                            .height(220.dp)
                            .padding(top = 16.dp)
                            .background(colorFondo, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .then(if (!descripcionHabilitada) Modifier.background(Color.Gray) else Modifier),
                        enabled = descripcionHabilitada
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Row {

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Habilitar opciones", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = colorTexto)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = nombreHabilitado, onCheckedChange = { nombreHabilitado = it })
                    Text(text = "Nombre", color = colorTexto)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = descripcionHabilitada,
                        onCheckedChange = { descripcionHabilitada = it })
                    Text(text = "Descripción", color = colorTexto)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = imagenHabilitada, onCheckedChange = { imagenHabilitada = it })
                    Text(text = "Imagen", color = colorTexto)
                }
            }


            // Columna de radio buttons

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Color de la carta", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = colorTexto)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = colorFondo == Color.Black,
                        onClick = { colorFondo = Color.Black }
                    )
                    Text(text = "Negro", color = colorTexto)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = colorFondo == Color.Green,
                        onClick = { colorFondo = Color.Green }
                    )
                    Text(text = "Verde", color = colorTexto)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = colorFondo == Color.Cyan,
                        onClick = { colorFondo = Color.Cyan }
                    )
                    Text(text = "Cian", color = colorTexto)
                }
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        // Switch para el modo oscuro
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Modo oscuro", color = colorTexto)
            Switch(
                checked = modoOscuroHabilitado,
                onCheckedChange = { modoOscuroHabilitado = it }
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progreso },
            color = if (modoOscuroHabilitado) Color.Green else Color.Red,
        )

    }
}

