package com.example.agenda_profesor

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    listaAlumnos(alumnos, showListado, showNuevoAlumno)
                } else if (showNuevoAlumno.value) {
                    // Mostrar el formulario para añadir un nuevo alumno
                    nuevoAlumno(showNuevoAlumno, showListado, alumnos)
                }

                // Espaciador
                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }
}

// Modelo de datos para un alumno
data class Alumno(val nombre: String, val apellido: String)

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun listaAlumnos(alumnos: MutableList<Alumno>, showListado: MutableState<Boolean>, showNuevoAlumno: MutableState<Boolean>) {
    var busquedaText by remember { mutableStateOf("") }

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
            onClick = { },
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
            onClick = { },
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Calcular Nota Media")
        }

        Spacer(modifier = Modifier.width(8.dp))


        ElevatedButton(
            onClick = { },
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun nuevoAlumno(showNuevoAlumno: MutableState<Boolean>, showListado: MutableState<Boolean>, alumnos: MutableList<Alumno>) {
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

    // Botón para añadir el alumno
    Button(
        onClick = {
            // Añadir el alumno a la lista
            alumnos.add(Alumno(nombre, apellido))
            showNuevoAlumno.value = false
            showListado.value = true
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Añadir Alumno")
    }
}
