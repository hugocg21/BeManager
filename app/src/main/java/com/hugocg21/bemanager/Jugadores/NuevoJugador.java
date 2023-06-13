package com.hugocg21.bemanager.Jugadores;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.hugocg21.bemanager.Equipo.DashboardEquipo;
import com.hugocg21.bemanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NuevoJugador extends AppCompatActivity {
    private Button button_nuevoJugador; //Creamos el Button de añadir un nuevo jugador
    private EditText editText_nombreJugador, editText_apellidosJugador, editText_dorsalJugador; //Creamos los EditTexts del nombre, apellidos y dorsal del jugador
    private Spinner spinner_equipoJugador, spinner_posicionJugador; //Creamos los Spinners para el equipo y la posición del jugador
    private FirebaseFirestore database; //Creamos el objeto de la base de datos
    private FirebaseAuth auth; //Creamos el objeto de la autenticación de usuario
    private FirebaseUser usuarioLogueado; //Creamos el objeto de usuario

    //Creamos las referencias a las colecciones de jugadores, equipos y usuario
    private CollectionReference collectionReference_jugadores, collectionReference_equipos, collectionReference_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_jugador);

        //Inicializamos los EditTexts
        editText_nombreJugador = findViewById(R.id.editTextNombreNuevoJugador);
        editText_apellidosJugador = findViewById(R.id.editTextApellidosNuevoJugador);
        editText_dorsalJugador = findViewById(R.id.editTextDorsalNuevoJugador);

        //Inicializamos los Spinners
        spinner_equipoJugador = findViewById(R.id.spinnerEquipoNuevoJugador);
        spinner_posicionJugador = findViewById(R.id.spinnerPosicionNuevoJugador);

        //Inicializamos el Button
        button_nuevoJugador = findViewById(R.id.buttonAnadirJugadorNuevoJugador);

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Creamos un String y le asignamos el correo del usuario logueado actual obtenido
        usuarioLogueado = auth.getCurrentUser();
        String correo = Objects.requireNonNull(usuarioLogueado).getEmail();

        //Creamos el objeto SharedPreferences y un String para obtener y almacenar el nombre del equipo al que queremos añadir el jugador
        SharedPreferences sharedPreferences = getSharedPreferences("datos", MODE_PRIVATE);
        String equipo = sharedPreferences.getString("nombreEquipo", null);

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");
        collectionReference_jugadores = collectionReference_equipos.document(equipo).collection("Jugadores");

        //Rellenamos los Spinners de posición y equipo
        rellenarSpinnerPosicion();
        rellenarSpinnerEquipos(equipo);

        //Método al hacer click en el Button de añadir un nuevo jugador
        button_nuevoJugador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos el adaptador personalizado para el equipo del jugador y añadimos el equipo del jugador
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item);
                adapter.add(equipo);

                //Asignamos el adaptador al Spinner del equipo
                spinner_equipoJugador.setAdapter(adapter);
                //Creamos los String de los datos del equipo(nombre, apellidos, equipo, posición y dorsal) y los guardamos
                String nombreJugador = editText_nombreJugador.getText().toString().trim();
                String apellidosJugador = editText_apellidosJugador.getText().toString().trim();
                String equipoJugador = spinner_equipoJugador.getSelectedItem().toString().trim();
                String posicionJugador = spinner_posicionJugador.getSelectedItem().toString().trim();
                int dorsalJugador = Integer.parseInt(editText_dorsalJugador.getText().toString().trim());

                //Creamos una query para comprobar si existe ya el dorsal en la base de datos de ese equipo
                Query query = collectionReference_jugadores.whereEqualTo("dorsalJugador", dorsalJugador);

                //Creamos e inicializamos una variable de tipo int para asignar a cada posición un valor entero y así poder ordenarlos por posición posteriormente
                int posicionJugadorNumero = 0;

                //Para cada String de la posición asignamos un valor entero
                switch (posicionJugador) {

                    //A la posición de base le asginamos el valor 1
                    case "Base":
                        posicionJugadorNumero = 1;
                        break;

                    //A la posición de base le asginamos el valor 2
                    case "Escolta":
                        posicionJugadorNumero = 2;
                        break;

                    //A la posición de base le asginamos el valor 3
                    case "Alero":
                        posicionJugadorNumero = 3;
                        break;

                    //A la posición de base le asginamos el valor 4
                    case "Ala-pivot":
                        posicionJugadorNumero = 4;
                        break;

                    //A la posición de base le asginamos el valor 5
                    case "Pivot":
                        posicionJugadorNumero = 5;
                        break;
                }

                //Creamos una variable para guardar el valor entero de la posición
                int finalPosicionJugadorNumero = posicionJugadorNumero;

                //Llamamos a la query para crear al jugador
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    //Si la query da un resultado positivo creamos el jugador
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                //Creamos y mostramos un mensaje emergente informando que el dorsal del jugador ya está cogido
                                Toast.makeText(getApplicationContext(), "El dorsal ya está en uso por otro jugador", Toast.LENGTH_SHORT).show();
                            } else {
                                //Creamos y mostramos un mensaje emergente informando que hay que rellenar todos los campos
                                if (nombreJugador.isEmpty() || apellidosJugador.isEmpty() || equipoJugador.isEmpty() || posicionJugador.isEmpty() || dorsalJugador == 0) {
                                    Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                                } else {
                                    //Si todos los campos están rellenados correctamente, llamamos el método para crear un equipo
                                    crearJugador(nombreJugador, apellidosJugador, equipoJugador, posicionJugador, dorsalJugador, finalPosicionJugadorNumero);

                                    //Comenzamos la actividad del Dashboard del equipo para que se actualice el Recycler View de los jugadores y finalizamos esta actividad
                                    startActivity(new Intent(getApplicationContext(), DashboardEquipo.class));
                                    finish();
                                }
                            }
                        } else {
                            //Creamos y mostramos un mensaje emergente informando que ha ocurrido un error al verificar si el dorsal está disponible
                            Toast.makeText(getApplicationContext(), "Error al verificar el dorsal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //Método para crear un jugador y añadirlo a la base de datos
    private void crearJugador(String nombreJugador, String apellidosJugador, String equipoJugador, String posicionJugador, int dorsalJugador,
                              int posicionJugadorNumero) {
        //Creamos un HashMap para guardar los datos del equipo
        Map<String, Object> equipo = new HashMap<>();
        equipo.put("nombreJugador", nombreJugador);
        equipo.put("apellidosJugador", apellidosJugador);
        equipo.put("equipoJugador", equipoJugador);
        equipo.put("posicionJugador", posicionJugador);
        equipo.put("dorsalJugador", dorsalJugador);
        equipo.put("posicionJugadorNumero", posicionJugadorNumero);

        //Recogemos la referencia a la colección de los jugadores y le asignamos como ID del documento el nombre completo del jugador
        collectionReference_jugadores.document(nombreJugador + " " + apellidosJugador).set(equipo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Creamos y mostramos un mensaje emergente informando que se ha creado y añadido el jugador correctamente
                Toast.makeText(getApplicationContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Creamos y mostramos un mensaje emergente indicando que ha habido un error al crear el jugador
                Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Método para rellenar el Spinner de las posiciones del jugador
    private void rellenarSpinnerPosicion() {
        //Creamos un ArrayLista par almacenar todas las posiciones posibles de un jugador y añadimos dichas posiciones
        ArrayList<String> posiciones = new ArrayList<String>();
        posiciones.add("Posición del jugador");
        posiciones.add("Base");
        posiciones.add("Escolta");
        posiciones.add("Alero");
        posiciones.add("Ala-pivot");
        posiciones.add("Pivot");

        //Creamos el adaptador, modificamos como se muestra al desplegarse y se lo asignamos al Spinner
        ArrayAdapter<String> adapterPosiciones = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, posiciones);
        adapterPosiciones.setDropDownViewResource(R.layout.custom_spinner_lista);
        spinner_posicionJugador.setAdapter(adapterPosiciones);
    }

    //Método para rellenar el Spinner del equipo del jugador
    private void rellenarSpinnerEquipos(String equipo) {
        //Creamos un ArrayList para almacenar el equipo del jugador jugador y añadimos el equipo
        ArrayList<String> equipos = new ArrayList<>();
        equipos.add(equipo);

        //Creamos el adaptador, modificamos como se muestra al desplegarse y se lo asignamos al Spinner
        ArrayAdapter<String> adapterEquipos = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, equipos);
        adapterEquipos.setDropDownViewResource(R.layout.custom_spinner_lista);
        spinner_equipoJugador.setAdapter(adapterEquipos);
    }
}