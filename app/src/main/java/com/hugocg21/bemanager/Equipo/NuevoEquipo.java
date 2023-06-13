package com.hugocg21.bemanager.Equipo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hugocg21.bemanager.Dashboard;
import com.hugocg21.bemanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NuevoEquipo extends AppCompatActivity {
    private Button button_nuevoEquipo; //Creamos el Button de añadir un nuevo equipo
    private EditText editText_nombreEquipo, editText_sedeEquipo; //Creamos los EditTexts del nombre y sede del equipo
    private Spinner spinner_categoriaEquipo; //Creamos el Spinner para la categoría del equipo
    private FirebaseFirestore database; //Creamos el objeto de la base de datos
    private FirebaseAuth auth; //Creamos el objeto de la autenticación de usuario
    private FirebaseUser usuarioLogueado; //Creamos el objeto de usuario
    private CollectionReference collectionReference_equipos, collectionReference_usuario; //Creamos las referencias a las colecciones de equipos y usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_equipo);

        //Inicializamos los EditTexts
        editText_nombreEquipo = findViewById(R.id.editTextNombreNuevoEquipo);
        editText_sedeEquipo = findViewById(R.id.editTextSedeNuevoEquipo);

        //Inicializamos el Spinner
        spinner_categoriaEquipo = findViewById(R.id.spinnerCategoriaNuevoEquipo);

        //Llamamos al método para rellenar el Spinner
        rellenarSpinner();

        //Inicializamos el Button
        button_nuevoEquipo = findViewById(R.id.buttonAnadirNuevoEquipo);

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Creamos un String y le asignamos el correo del usuario logueado actual obtenido
        usuarioLogueado = auth.getCurrentUser();
        String correo = Objects.requireNonNull(usuarioLogueado).getEmail();

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");

        //Método al hacer click en el Button de añadir un nuevo equipo
        button_nuevoEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos los String de los datos del equipo (nombre, sede y categoría) y los guardamos
                String nombreEquipo = editText_nombreEquipo.getText().toString().trim();
                String sedeEquipo = editText_sedeEquipo.getText().toString().trim();
                String categoriaEquipo = spinner_categoriaEquipo.getSelectedItem().toString().trim();

                //Si algún campo está vacio, entramos el if
                if (nombreEquipo.isEmpty() || categoriaEquipo.isEmpty() || sedeEquipo.isEmpty()) {
                    //Creamos y mostramos un mensaje emergente informando que hay que rellenar todos los campos
                    Toast.makeText(getApplicationContext(), "Ingresar todos los datos", Toast.LENGTH_SHORT).show();
                } else {
                    //Si todos los campos están rellenados correctamente, llamamos el método para crear un equipo
                    crearEquipo(nombreEquipo, categoriaEquipo, sedeEquipo);

                    //Creamos un Intent y comenzamos la actividad del Dashboard
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                }
            }
        });
    }

    //Método para crear un equipo y añadirlo a la base de datos
    private void crearEquipo(String nombreEquipo, String categoriaEquipo, String sedeEquipo) {
        //Creamos un HashMap para guardar los datos del equipo y los guardamos
        Map<String, Object> equipo = new HashMap<>();
        equipo.put("nombreEquipo", nombreEquipo);
        equipo.put("categoriaEquipo", categoriaEquipo);
        equipo.put("sedeEquipo", sedeEquipo);

        //Recogemos la referencia a la colección de los equipos y no hay problemas, entra aquí
        collectionReference_equipos.document(nombreEquipo).set(equipo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Creamos y mostramos un mensaje emergente informando que se ha creado y añadido el equipo correctamente y finalizamos la actividad
                        Toast.makeText(getApplicationContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                //Si hay problemas, entramos aquí
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Creamos y mostramos un mensaje emergente indicando que ha habido un error al crear el equipo
                        Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //Método para rellenar el Spinner de las categorías del equipo
    private void rellenarSpinner() {
        //Creamos un ArrayList para almacenar todas las categorías y añadimos dichas categorías
        ArrayList<String> categorias = new ArrayList<String>();
        categorias.add("Categoría del equipo");
        categorias.add("Cto. España 1ª División Mas (Competiciones federadas)");
        categorias.add("Cto. España 1ª División Fem (Competiciones federadas)");
        categorias.add("1ª Autonómica Mas (Competiciones federadas)");
        categorias.add("1ª Autonómica Fem (Competiciones federadas)");
        categorias.add("2ª Autonómica Mas (Competiciones federadas)");
        categorias.add("2ª Autonómica Fem (Competiciones federadas)");
        categorias.add("3ª Autonómica Mas (Competiciones federadas)");
        categorias.add("Junior Mas 1ª (Competiciones federadas)");
        categorias.add("Junior Mas 2ª (Competiciones federadas)");
        categorias.add("Junior Mas 3ª (Competiciones federadas)");
        categorias.add("Junior Fem 1ª (Competiciones federadas)");
        categorias.add("Junior Fem 2ª (Competiciones federadas)");
        categorias.add("Alevín Mas Copa (Competiciones federadas)");
        categorias.add("Alevín Fem Copa (Competiciones federadas)");
        categorias.add("Benjamín Mas Copa (Competiciones federadas)");
        categorias.add("Benjamín Fem Copa (Competiciones federadas)");

        categorias.add("Cadete Mas 1ª (Juegos deportivos)");
        categorias.add("Cadete Mas 2ª (Juegos deportivos)");
        categorias.add("Cadete Fem 1ª (Juegos deportivos)");
        categorias.add("Cadete Fem 2ª (Juegos deportivos)");
        categorias.add("Infantil Mas 1ª (Juegos deportivos)");
        categorias.add("Infantil Fem 1ª (Juegos deportivos)");
        categorias.add("Infantil Mas 2ª (Juegos deportivos)");
        categorias.add("Infantil Fem 2ª (Juegos deportivos)");
        categorias.add("Alevín Mas (Juegos deportivos)");
        categorias.add("Alevín Fem (Juegos deportivos)");
        categorias.add("Benjamín Mas (Juegos deportivos)");
        categorias.add("Benjamín Fem (Juegos deportivos)");

        //Creamos el adaptador, modificamos como se muestra al desplegarse y se lo asignamos al Spinner
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categorias);
        adapterCategorias.setDropDownViewResource(R.layout.custom_spinner_lista);
        spinner_categoriaEquipo.setAdapter(adapterCategorias);
    }
}