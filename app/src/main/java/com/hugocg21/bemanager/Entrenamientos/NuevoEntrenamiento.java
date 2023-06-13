package com.hugocg21.bemanager.Entrenamientos;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
    private Button button_nuevoEntrenamiento; //Creamos el Button de añadir un nuevo entrenamiento
    private EditText editText_fechaEntrenamiento, editText_horaEntrenamiento; //Creamos los EditTexts de la fecha y hora del entrenamiento
    private FirebaseFirestore database; //Creamos el objeto de la base de datos
    private FirebaseAuth auth; //Creamos el objeto de la autenticación de usuario
    private FirebaseUser usuarioLogueado; //Creamos el objeto de usuario

    //Creamos las referencias a las colecciones de entrenamientos, equipos y usuario
    private CollectionReference collectionReference_entrenamientos, collectionReference_equipos, collectionReference_usuario;

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

        //Creamos el objeto SharedPreferences y un String para obtener y almacenar el nombre del equipo al que queremos añadir el entrenamiento
        SharedPreferences sharedPreferences = getSharedPreferences("datos", MODE_PRIVATE);
        String equipo = sharedPreferences.getString("nombreEquipo", null);

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");
        collectionReference_entrenamientos = collectionReference_equipos.document(equipo).collection("Entrenamientos");

        //Método al hacer click en el EditText de la fecha del entrenamiento
        editText_fechaEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un objeto Calendar y tres números enteros para almacenar el dia, mes y año de la fecha
                Calendar calendarioFecha = Calendar.getInstance();
                int anio = calendarioFecha.get(Calendar.YEAR);
                int mes = calendarioFecha.get(Calendar.MONTH);
                int dia = calendarioFecha.get(Calendar.DAY_OF_MONTH);

                //Creamos un objeto DatePickerDialog para mostrar un Dialog que muestra un calendario
                DatePickerDialog datePickerDialog = new DatePickerDialog(NuevoEntrenamiento.this,
                        new DatePickerDialog.OnDateSetListener() {

                            //Sobre escribimos el método que controla la selección de la fecha
                            @Override
                            public void onDateSet(DatePicker view, int anioSeleccionado, int mesSeleccionado, int diaSeleccionado) {
                                //Sumamos 1 al mes seleccionado, ya que el primer mes por defecto es 0 (Enero = 0 y Diciembre = 11)
                                mesSeleccionado += 1;

                                //Formateamos la fecha con ceros a la izquierda (07-06-2023 en vez de 7-6-2023) y se lo asignamos al EditText de la fecha
                                String fechaSeleccionada = String.format("%02d-%02d-%d", diaSeleccionado, mesSeleccionado, anioSeleccionado);
                                editText_fechaEntrenamiento.setText(fechaSeleccionada);
                            }
                        }, anio, mes, dia);

                //Mostramos el Dialog del calendario
                datePickerDialog.show();
            }
        });

        //Método al hacer click en el EditText de la hora del entrenamiento
        editText_horaEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un objeto Calendar y dos números enteros para almacenar la hora y los minutos de la hora
                Calendar calendarioHora = Calendar.getInstance();
                int hora = calendarioHora.get(Calendar.HOUR_OF_DAY);
                int min = calendarioHora.get(Calendar.MINUTE);

                //Creamos un objeto TimePickerDialog para mostrar un Dialog que muestra un reloj
                TimePickerDialog timePickerDialog = new TimePickerDialog(NuevoEntrenamiento.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            //Sobre escribimos el método que controla la selección de la hora
                            @Override
                            public void onTimeSet(TimePicker view, int horaSeleccionada, int minutoSeleccionado) {
                                //Formateamos la fecha con ceros a la izquierda (05:09 en vez de 5:9) y se lo asignamos al EditText de la hora
                                String horaFechaSeleccionada = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado);
                                editText_horaEntrenamiento.setText(horaFechaSeleccionada);
                            }
                        }, hora, min, true);

                //Mostramos el Dialog del reloj
                timePickerDialog.show();
            }
        });


        //Método al hacer click en el Button de añadir un nuevo equipo
        button_nuevoEntrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cremos dos String para guardar la fecha y la hora de los entrenamientos
                String fechaEntrenamiento = editText_fechaEntrenamiento.getText().toString();
                String horaEntrenamiento = editText_horaEntrenamiento.getText().toString();

                //Si la fecha o la hora del entrenamiento están vacios entramos aquí
                if (fechaEntrenamiento.isEmpty() || horaEntrenamiento.isEmpty()){
                    //Creamos y mostramos un mensaje emergente informando que hay que rellenar todos los campos
                    Toast.makeText(getApplicationContext(), "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show();
                } else {
                    //Si todos los campos están rellenados correctamente, llamamos al método para crear el entrenamiento y finalizamos esta actividad
                    crearEntrenamiento(fechaEntrenamiento, horaEntrenamiento);
                    finish();
                }
            }
        });
    }

    //Método para crear un entrenamiento y añadirlo a la base de datos
    private void crearEntrenamiento(String fechaEntrenamiento, String horaEntrenamiento) {
        //Creamos un HashMap para guardar los datos del equipo
        Map<String, Object> entrenamiento = new HashMap<>();
        entrenamiento.put("fechaEntrenamiento", fechaEntrenamiento);
        entrenamiento.put("horaEntrenamiento", horaEntrenamiento);

        //Recogemos la referencia a la colección de los entrenamientos y le asignamos como ID del documento la fecha del entrenamiento
        collectionReference_entrenamientos.document(fechaEntrenamiento).set(entrenamiento).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Creamos y mostramos un mensaje emergente informando que se ha creado y añadido el entrenamiento correctamente
                Toast.makeText(getApplicationContext(), "Entrenamiento añadido correctamente", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Creamos y mostramos un mensaje emergente indicando que ha habido un error al crear el entrenamiento
                Toast.makeText(getApplicationContext(), "Error al ingresar el entrenamiento", Toast.LENGTH_SHORT).show();
            }
        });
    }
}