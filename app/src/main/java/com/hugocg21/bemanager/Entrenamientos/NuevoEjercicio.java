package com.hugocg21.bemanager.Entrenamientos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hugocg21.bemanager.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NuevoEjercicio extends AppCompatActivity {
    private Button button_nuevoEjercicio; //Creamos el Button de añadir un nuevo ejercicio
    private EditText editText_tituloEjercicio, editText_descripcionEjercicio, editText_duracionEjercicio; //Creamos los EditTexts del título, descripción y duración del ejercicio
    private FirebaseFirestore database; //Creamos el objeto de la base de datos
    private FirebaseAuth auth; //Creamos el objeto de la autenticación de usuario
    private FirebaseUser usuarioLogueado; //Creamos el objeto de usuario

    //Creamos las referencias a las colecciones de ejercicios, entrenamientos, equipos y usuarios
    private CollectionReference collectionReference_ejercicios, collectionReference_entrenamientos, collectionReference_equipos, collectionReference_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_ejercicio);

        //Inicializamos los EditTexts
        editText_tituloEjercicio = findViewById(R.id.editTextTituloNuevoEjercicio);
        editText_descripcionEjercicio = findViewById(R.id.editTextDescripcionNuevoEjercicio);
        editText_duracionEjercicio = findViewById(R.id.editTextDuracionNuevoEjercicio);

        //Inicializamos el Button
        button_nuevoEjercicio = findViewById(R.id.buttonAnadirEjercicioNuevoEjercicio);

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Creamos un String y le asignamos el correo del usuario logueado actual obtenido
        usuarioLogueado = auth.getCurrentUser();
        String correo = Objects.requireNonNull(usuarioLogueado).getEmail();

        //Creamos el objeto SharedPreferences y un String para obtener y almacenar el nombre del equipo al que queremos añadir el ejercicio
        SharedPreferences sharedPreferencesEquipo = getSharedPreferences("datos", MODE_PRIVATE);
        String nombreEquipo = sharedPreferencesEquipo.getString("nombreEquipo", null);

        //Creamos el objeto SharedPreferences y un String para obtener y almacenar la fecha del entrenamiento al que queremos añadir el ejercicio
        SharedPreferences sharedPreferencesEntrenamiento = getSharedPreferences("entrenamiento", MODE_PRIVATE);
        String fechaEntrenamiento = sharedPreferencesEntrenamiento.getString("fechaEntrenamiento", null);

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");
        collectionReference_entrenamientos = collectionReference_equipos.document(nombreEquipo).collection("Entrenamientos");
        collectionReference_ejercicios = collectionReference_entrenamientos.document(fechaEntrenamiento).collection("Ejercicios");

        //Método al hacer click en el Button de añadir un ejercicio
        button_nuevoEjercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos los Strings y el int de los datos del ejercicio (título, descripción y duracion) y los guardamos
                String tituloEjercicio = editText_tituloEjercicio.getText().toString();
                String descripcionEjercicio = editText_descripcionEjercicio.getText().toString();
                int duracionEjercicio = Integer.parseInt(editText_duracionEjercicio.getText().toString().trim());

                //Creamos la query para consultar la base de datos
                Query query = collectionReference_ejercicios;

                //Llamamos a la query para crear el ejercicio
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    //Si la query da un resultado positivo creamos el ejercicio
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Si algún campo está en blanco, entramos en el if
                            if (tituloEjercicio.isEmpty() || descripcionEjercicio.isEmpty() || duracionEjercicio == 0) {
                                //Creamos y mostamos un mensaje emergente de que no puede haber campos en blanco
                                Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                            } else {
                                //Si todos los campos están rellenados correctamente, llamamos el método para crear un ejercicio y terminamos la actividad
                                crearEjercicio(tituloEjercicio, descripcionEjercicio, duracionEjercicio);
                                finish();
                            }
                        }
                    }
                });
            }
        });
    }

    //Método para crear un ejercicio y añadirlo a la base de datos
    private void crearEjercicio(String tituloEjercicio, String descripcionEjercicio, int duracionEjercicio) {
        //Creamos un HashMap para guardar los datos del ejercicio
        Map<String, Object> ejercicio = new HashMap<>();
        ejercicio.put("tituloEjercicio", tituloEjercicio);
        ejercicio.put("descripcionEjercicio", descripcionEjercicio);
        ejercicio.put("duracionEjercicio", duracionEjercicio);

        //Recogemos la referencia a la colección de los ejercicio y le asignamos como ID del documento el título del ejercicio
        collectionReference_ejercicios.document(tituloEjercicio).set(ejercicio).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Creamos y mostramos un mensaje emergente informando que se ha creado y añadido el ejercicio correctamente
                Toast.makeText(getApplicationContext(), "Ejercicio creado exitosamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Creamos y mostramos un mensaje emergente indicando que ha habido un error al crear el ejercicio
                Toast.makeText(getApplicationContext(), "Error al ingresar el ejercicio", Toast.LENGTH_SHORT).show();
            }
        });
    }
}