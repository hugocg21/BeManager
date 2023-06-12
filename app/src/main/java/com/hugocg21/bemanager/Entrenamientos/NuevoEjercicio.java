package com.hugocg21.bemanager.Entrenamientos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.hugocg21.bemanager.Entrenamientos.DashboardEntrenamientos;
import com.hugocg21.bemanager.Equipo.DashboardEquipo;
import com.hugocg21.bemanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NuevoEjercicio extends AppCompatActivity {
    Button button_nuevoEjercicio;
    EditText editText_tituloEjercicio, editText_descripcionEjercicio, editText_duracionEjercicio;
    FirebaseAuth auth;
    FirebaseFirestore database;
    FirebaseUser usuarioLogueado;
    CollectionReference collectionReference_ejercicios, collectionReference_entrenamientos, collectionReference_equipos, collectionReference_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_ejercicio);

        editText_tituloEjercicio = findViewById(R.id.editTextTituloNuevoEjercicio);
        editText_descripcionEjercicio = findViewById(R.id.editTextDescripcionNuevoEjercicio);
        editText_duracionEjercicio = findViewById(R.id.editTextDuracionNuevoEjercicio);

        button_nuevoEjercicio = findViewById(R.id.buttonAnadirEjercicioNuevoEjercicio);

        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        usuarioLogueado = auth.getCurrentUser();
        String correo = Objects.requireNonNull(usuarioLogueado).getEmail();

        SharedPreferences sharedPreferencesEquipo = getSharedPreferences("datos", MODE_PRIVATE);
        String nombreEquipo = sharedPreferencesEquipo.getString("nombreEquipo", null);

        SharedPreferences sharedPreferencesEntrenamiento = getSharedPreferences("entrenamiento", MODE_PRIVATE);
        String fechaEntrenamiento = sharedPreferencesEntrenamiento.getString("fechaEntrenamiento", null);

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");
        collectionReference_entrenamientos = collectionReference_equipos.document(nombreEquipo).collection("Entrenamientos");
        collectionReference_ejercicios = collectionReference_entrenamientos.document(fechaEntrenamiento).collection("Ejercicios");

        button_nuevoEjercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tituloEjercicio = editText_tituloEjercicio.getText().toString();
                String descripcionEjercicio = editText_descripcionEjercicio.getText().toString();
                int duracionEjercicio = Integer.parseInt(editText_duracionEjercicio.getText().toString().trim());

                Query query = collectionReference_ejercicios;
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (tituloEjercicio.isEmpty() || descripcionEjercicio.isEmpty() || duracionEjercicio == 0) {
                                Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                            } else {
                                crearEjercicio(tituloEjercicio, descripcionEjercicio, duracionEjercicio);
                                startActivity(new Intent(getApplicationContext(), DashboardEntrenamientos.class));
                                finish();
                            }
                        }
                    }
                });
            }
        });
    }

    private void crearEjercicio(String tituloEjercicio, String descripcionEjercicio, int duracionEjercicio) {
        Map<String, Object> ejercicio = new HashMap<>();
        ejercicio.put("tituloEjercicio", tituloEjercicio);
        ejercicio.put("descripcionEjercicio", descripcionEjercicio);
        ejercicio.put("duracionEjercicio", duracionEjercicio);

        collectionReference_ejercicios.document(tituloEjercicio).set(ejercicio).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Ejercicio creado exitosamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al ingresar el ejercicio", Toast.LENGTH_SHORT).show();
            }
        });
    }
}