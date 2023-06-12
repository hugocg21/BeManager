package com.hugocg21.bemanager.Entrenamientos;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hugocg21.bemanager.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NuevoEntrenamiento extends AppCompatActivity {
    Button button_nuevoEntrenamiento;
    EditText editText_fechaEntrenamiento, editText_horaEntrenamiento;
    FirebaseAuth auth;
    FirebaseFirestore database;
    FirebaseUser usuarioLogueado;
    CollectionReference collectionReference_entrenamientos, collectionReference_equipos, collectionReference_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_entrenamiento);

        //Inicializamos los EditTexts
        editText_fechaEntrenamiento = findViewById(R.id.editTextFechaEntrenamiento);
        editText_horaEntrenamiento = findViewById(R.id.editTextHoraEntrenamiento);

        //Inicializamos el Button
        button_nuevoEntrenamiento = findViewById(R.id.buttonAnadirEntrenamiento);

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Creamos un String y le asignamos el correo del usuario logueado actual obtenido
        usuarioLogueado = auth.getCurrentUser();
        String correo = Objects.requireNonNull(usuarioLogueado).getEmail();

        SharedPreferences sharedPreferences = getSharedPreferences("datos", MODE_PRIVATE);
        String equipo = sharedPreferences.getString("nombreEquipo", null);

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");
        collectionReference_entrenamientos = collectionReference_equipos.document(equipo).collection("Entrenamientos");

        editText_fechaEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarioFecha = Calendar.getInstance();
                int anio = calendarioFecha.get(Calendar.YEAR);
                int mes = calendarioFecha.get(Calendar.MONTH);
                int dia = calendarioFecha.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(NuevoEntrenamiento.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int anioSeleccionado, int mesSeleccionado, int diaSeleccionado) {
                                // Sumar 1 al mes seleccionado
                                mesSeleccionado += 1;

                                // Formatear la fecha con ceros a la izquierda
                                String fechaSeleccionada = String.format("%02d-%02d-%d", diaSeleccionado, mesSeleccionado, anioSeleccionado);
                                editText_fechaEntrenamiento.setText(fechaSeleccionada);
                            }
                        }, anio, mes, dia);

                datePickerDialog.show();
            }
        });

        editText_horaEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarioHora = Calendar.getInstance();
                int hora = calendarioHora.get(Calendar.HOUR_OF_DAY);
                int min = calendarioHora.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(NuevoEntrenamiento.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int horaSeleccionada, int minutoSeleccionado) {
                                // Formatear la hora con ceros a la izquierda
                                String horaFechaSeleccionada = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado);
                                editText_horaEntrenamiento.setText(horaFechaSeleccionada);
                            }
                        }, hora, min, true);

                timePickerDialog.show();
            }
        });


        //Método al hacer click en el Button de añadir un nuevo equipo
        button_nuevoEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaEntrenamiento = editText_fechaEntrenamiento.getText().toString();
                String horaEntrenamiento = editText_horaEntrenamiento.getText().toString();

                if (fechaEntrenamiento.isEmpty() || horaEntrenamiento.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show();
                } else {
                    crearEntrenamiento(fechaEntrenamiento, horaEntrenamiento);
                    startActivity(new Intent(getApplicationContext(), DashboardEntrenamientos.class));
                    finish();
                }
            }
        });
    }

    private void crearEntrenamiento(String fechaEntrenamiento, String horaEntrenamiento) {
        Map<String, Object> entrenamiento = new HashMap<>();
        entrenamiento.put("fechaEntrenamiento", fechaEntrenamiento);
        entrenamiento.put("horaEntrenamiento", horaEntrenamiento);

        collectionReference_entrenamientos.document(fechaEntrenamiento).set(entrenamiento).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Entrenamiento añadido correctamente", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al ingresar el entrenamiento", Toast.LENGTH_SHORT).show();
            }
        });
    }
}