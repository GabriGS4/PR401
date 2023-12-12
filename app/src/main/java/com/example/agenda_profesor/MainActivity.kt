package com.example.agenda_profesor

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.agenda_profesor.ui.theme.Agenda_ProfesorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            Agenda_ProfesorTheme {
                // Llamada a la función AgendaProfesor
                agendaProfesor()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun agendaProfesor() {
    // Creamos un array de alumnos
    val alumnos = mutableListOf<Alumno>()
    val topAppBarHeight = 56.dp // Puedes ajustar este valor según tus necesidades
    val showListado = mutableStateOf(true)
    val showNuevoAlumno = mutableStateOf(false)
    val showBorrarDatos = mutableStateOf(false)
    val showEditarAlumno = mutableStateOf(false)
    val alumnoEditar = mutableStateOf(Alumno("", "", intArrayOf()))
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Agenda Profesor",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        },
    ) {
        Box(Modifier.fillMaxSize()) {
            // Contenido de la pantalla
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topAppBarHeight + 16.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.Center)
            ) {
                if (showListado.value) {
                    // Mostrar el listado de alumnos
                    listaAlumnos(
                        alumnos,
                        showListado,
                        showNuevoAlumno,
                        showBorrarDatos,
                        showEditarAlumno,
                        alumnoEditar
                    )
                } else if (showNuevoAlumno.value) {
                    // Mostrar el formulario para añadir un nuevo alumno
                    nuevoAlumno(showNuevoAlumno, showListado, alumnos)
                } else if (showBorrarDatos.value) {
                    // Mostrar el formulario para añadir un nuevo alumno
                    deleteDatos(alumnos, showListado, showBorrarDatos)
                } else if (showEditarAlumno.value) {
                    // Mostrar el formulario para editar un alumno
                    editarAlumno(showListado, showEditarAlumno, alumnoEditar, alumnos)
                }

                // Espaciador
                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }
}

// Modelo de datos para un alumno
data class Alumno(var nombre: String, var apellido: String, var notas: IntArray)

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun listaAlumnos(
    alumnos: MutableList<Alumno>,
    showListado: MutableState<Boolean>,
    showNuevoAlumno: MutableState<Boolean>,
    showBorrarDatos: MutableState<Boolean>,
    showEditarAlumno: MutableState<Boolean>,
    alumnoEditar: MutableState<Alumno>
) {
    var showCalcularNotaMedia by remember { mutableStateOf(false) }
    var showMostrarNotaMasAlta by remember { mutableStateOf(false) }
    var busquedaText by remember { mutableStateOf("") }

    if (showCalcularNotaMedia) {
        AlertDialog(
            title = {
                Text(text = "Nota Media Clase")
            },
            text = {
                if (alumnos.isNotEmpty() && alumnos.any { it.notas.isNotEmpty() }) {
                    // Filtra los alumnos que tienen notas
                    val alumnosConNotas = alumnos.filter { it.notas.isNotEmpty() }

                    // Filtra los alumnos que no tienen una media de 0 o 10
                    val alumnosFiltrados = alumnosConNotas.filter { alumno ->
                        val media = alumno.notas.average()
                        media != 0.0 && media != 10.0
                    }

                    // Calcula la media de las notas de los alumnos filtrados
                    val notaMediaClase = alumnosFiltrados.map { it.notas.average() }.average()

                    Text(
                        text = "La nota media de la clase es: $notaMediaClase",
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "La nota media de la clase es: 0.0",
                        modifier = Modifier.fillMaxWidth()
                    )
                }


            },
            onDismissRequest = {
                showCalcularNotaMedia = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCalcularNotaMedia = false
                    }
                ) {
                    Text("Aceptar")
                }
            },

            )

    }

    if (showMostrarNotaMasAlta) {
        AlertDialog(
            title = {
                Text(text = "Nota media más alta")
            },
            text = {
                // Mostramos de quien es la nota más alta
                val notaMasAlta = alumnos.maxByOrNull { it.notas.maxOrNull() ?: 0 }
                if (notaMasAlta != null && notaMasAlta.notas.isNotEmpty()) {
                    Text(
                        text = "La nota media más alta es: ${notaMasAlta.notas.maxOrNull()} de ${notaMasAlta.nombre} ${notaMasAlta.apellido}",
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "No hay notas",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            onDismissRequest = {
                showMostrarNotaMasAlta = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMostrarNotaMasAlta = false
                    }
                ) {
                    Text("Aceptar")
                }
            },

            )

    }

    // Función para realizar la búsqueda
    fun buscarAlumnos(): List<Alumno> {
        val busquedaLowerCase = busquedaText.lowercase()

        return if (busquedaLowerCase.isBlank()) {
            // Si la búsqueda está en blanco, mostrar todos los alumnos
            alumnos
        } else {
            // Filtrar los alumnos por nombre o apellido que contenga la cadena de búsqueda
            alumnos.filter {
                it.nombre.lowercase().contains(busquedaLowerCase) || it.apellido.lowercase()
                    .contains(busquedaLowerCase)
            }
        }
    }

    // Título de la lista
    Text(
        text = "LISTADO DE ALUMNOS",
        modifier = Modifier
            .padding(8.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ElevatedButton(
            onClick = {
                showListado.value = false
                showNuevoAlumno.value = true
            },
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Nuevo Alumno")
        }

        Spacer(modifier = Modifier.width(8.dp))


        ElevatedButton(
            onClick = {
                showMostrarNotaMasAlta = true;
            },
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Sacar Nota más alta")
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ElevatedButton(
            onClick = {
                showCalcularNotaMedia = true;
            },
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Calcular Nota Media")
        }

        Spacer(modifier = Modifier.width(8.dp))


        ElevatedButton(
            onClick = {
                showListado.value = false
                showBorrarDatos.value = true
            },
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Borrar Datos")
        }
    }

    // Campo de búsqueda
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = busquedaText,
            onValueChange = { busquedaText = it },
            label = { Text("Buscar alumno") },
            maxLines = 1,
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Icono de búsqueda"
                )
            },
            modifier = Modifier
                .weight(1f)
        )
    }

    // Lista de alumnos
    val resultadosBusqueda by remember(busquedaText) {
        derivedStateOf {
            buscarAlumnos()
        }
    }

    // Lista de alumnos
    LazyColumn {
        if (resultadosBusqueda.isEmpty()) {
            // Mostrar un mensaje si no hay resultados
            item {
                Text(
                    text = "No hay resultados",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        } else {
            items(resultadosBusqueda) { alumno ->
                // Elemento de la lista para cada alumno
                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    onClick = {
                        // Mostrar el formulario para editar el alumno
                        showListado.value = false
                        showEditarAlumno.value = true
                        alumnoEditar.value = alumno
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(5.dp))
                            // Mostramos la posicion del alumno en la lista, su nombre y apellido
                            if (alumno.notas.isNotEmpty()) {
                                Text(
                                    text = "${alumnos.indexOf(alumno) + 1}. ${alumno.nombre} ${alumno.apellido}. Nota Media: ${alumno.notas.average()}",
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                )
                            } else {
                                Text(
                                    text = "${alumnos.indexOf(alumno) + 1}. ${alumno.nombre} ${alumno.apellido}. Nota Media: 0.0",
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun nuevoAlumno(
    showNuevoAlumno: MutableState<Boolean>,
    showListado: MutableState<Boolean>,
    alumnos: MutableList<Alumno>
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }

    // Título de la lista
    Text(
        text = "NUEVO ALUMNO",
        modifier = Modifier
            .padding(8.dp)
    )

    // Campo de nombre
    OutlinedTextField(
        value = nombre,
        onValueChange = { nombre = it },
        label = { Text("Nombre") },
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )

    // Campo de apellido
    OutlinedTextField(
        value = apellido,
        onValueChange = { apellido = it },
        label = { Text("Apellido") },
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        OutlinedButton(
            onClick = {
                showNuevoAlumno.value = false
                showListado.value = true
            },
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Cancelar")
        }

        Spacer(modifier = Modifier.width(8.dp))


        // Botón para añadir el alumno
        Button(
            onClick = {
                // Añadir el alumno a la lista
                alumnos.add(Alumno(nombre, apellido, intArrayOf()))
                showNuevoAlumno.value = false
                showListado.value = true
            },
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Añadir Alumno")
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editarAlumno(
    showListado: MutableState<Boolean>,
    showEditarAlumno: MutableState<Boolean>,
    alumno: MutableState<Alumno>,
    alumnos: MutableList<Alumno>
) {
    var nombre by remember { mutableStateOf(alumno.value.nombre) }
    var apellido by remember { mutableStateOf(alumno.value.apellido) }

    var showNuevaNota by remember { mutableStateOf(false) }
    var showBorrarNotas by remember { mutableStateOf(false) }
    var notaText by remember { mutableStateOf("") }

    var notas by remember { mutableStateOf(alumno.value.notas) }
    val context = LocalContext.current

    if (showNuevaNota) {
        AlertDialog(
            title = {
                Text(text = "Añadir Nota")
            },
            text = {
                // Agregar un OutlinedTextField para ingresar números enteros

                OutlinedTextField(
                    value = notaText,
                    onValueChange = {
                        // Asegurarse de que solo sean caracteres numéricos
                        if (it.isDigitsOnly()) {
                            notaText = it
                        }
                    },
                    label = { Text("Nota") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            },
            onDismissRequest = {
                showNuevaNota = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Aquí puedes manejar la lógica para procesar el valor ingresado
                        showNuevaNota = false
                        // Comprobación de rango (1 a 10)
                        val nuevaNota = notaText.toIntOrNull()
                        if (nuevaNota in 1..10) {
                            // Añadimos la nota al array de notas del alumno
                            alumno.value.notas += nuevaNota!!
                            notas = alumno.value.notas
                        } else {
                            // Mostramos un toast de error
                            Toast.makeText(context, "Nota no válida", Toast.LENGTH_SHORT).show()
                        }
                        notaText = ""
                    }
                ) {
                    Text("Añadir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showNuevaNota = false
                    }
                ) {
                    Text("Cancelar")
                }
            },

            )

    }

    if (showBorrarNotas) {
        AlertDialog(
            title = {
                Text(text = "¿Estás seguro de eliminar todas las notas?")
            },
            onDismissRequest = {
                showBorrarNotas = false
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        showBorrarNotas = false
                        // Eliminar todas las notas del array de notas del alumno
                        alumno.value.notas = intArrayOf()
                        notas = alumno.value.notas
                    }
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showBorrarNotas = false
                    }
                ) {
                    Text("Cancelar")
                }
            },

            )

    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "EDITAR ALUMNO ${alumnos.indexOf(alumno.value) + 1}",
            modifier = Modifier
                .padding(8.dp)
                .weight(2f),
            textAlign = TextAlign.Center
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // Botón para cancelar la edición del alumno
        OutlinedButton(
            onClick = {
                // Ocultar el formulario de edición y mostrar el listado
                showEditarAlumno.value = false
                showListado.value = true
            },
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text("Cancelar")
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Botón para editar el alumno
        Button(
            onClick = {
                // Encontrar el índice del alumno en la lista
                val index = alumnos.indexOf(alumno.value)

                // Actualizar los valores del alumno en la lista con los nuevos valores
                if (index != -1) {
                    alumnos[index].nombre = nombre
                    alumnos[index].apellido = apellido
                }

                // Ocultar el formulario de edición y mostrar el listado
                showEditarAlumno.value = false
                showListado.value = true
            },
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text("Editar Alumno")
        }


    }


    // Campo de nombre
    OutlinedTextField(
        value = nombre,
        onValueChange = { nombre = it },
        label = { Text("Nombre") },
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )

    // Campo de apellido
    OutlinedTextField(
        value = apellido,
        onValueChange = { apellido = it },
        label = { Text("Apellido") },
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NOTAS",
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    showBorrarNotas = true
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Borrar Notas")
            }
            Spacer(modifier = Modifier.width(8.dp))

            ElevatedButton(
                onClick = {
                    showNuevaNota = true
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Añadir Nota")
            }

        }
        // Mostrar la nota media del alumno si no está vacío el array de notas
        if (notas.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Nota Media: ${notas.average()}",
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )

            }
        }

        LazyColumn {
            if (notas.isEmpty()) {
                item {
                    Text(
                        text = "No hay resultados",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {

                items(notas.size) { index ->
                    var nota = alumno.value.notas[index]
                    // Elemento de la lista para cada alumno
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(5.dp))
                        // Mostramos la posición del alumno en la lista, su nombre y apellido
                        Text(
                            // Mostramos la posición de la nota en el array de notas y su valor
                            text = "Nota ${index + 1}: $nota",
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        )

                        // Icono de papelera cliclable
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar nota",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    // Eliminar la nota del array de notas
                                    alumno.value.notas = alumno.value.notas
                                        .filterIndexed { i, _ ->
                                            i != index
                                        }
                                        .toIntArray()
                                    notas = alumno.value.notas
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun deleteDatos(
    alumnos: MutableList<Alumno>,
    showListado: MutableState<Boolean>,
    showBorrarDatos: MutableState<Boolean>
) {
    // Elimina los datos de la lista de alumnos
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 150.dp)
    ) {
        Text(
            text = "¿Estás seguro de que quieres borrar los alumnos y sus notas?",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ElevatedButton(
                onClick = {
                    alumnos.clear()
                    showBorrarDatos.value = false
                    showListado.value = true
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Borrar Datos")
            }

            Spacer(modifier = Modifier.width(8.dp))


            ElevatedButton(
                onClick = {
                    showBorrarDatos.value = false
                    showListado.value = true
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Cancelar")
            }
        }
    }
}
