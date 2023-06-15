package com.hugocg21.bemanager.Entrenamientos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hugocg21.bemanager.Adaptadores.AdaptadorEjercicios;
import com.hugocg21.bemanager.Clases.Ejercicio;
import com.hugocg21.bemanager.R;

import java.util.Objects;

public class DashboardEntrenamientos extends AppCompatActivity {
    private RecyclerView recyclerView_ejercicios; //Creamos el RecyclerView del Dashboard del entrenamiento
    private FloatingActionButton floatingActionButton_nuevoEjercicio; //Creamos el FloatingActionButton
    private FirebaseFirestore database; //Creamos la variable de la base de datos
    private FirebaseAuth auth; //Creamos la variable de autenticación
    private FirebaseUser usuarioLogueado; //Creamos una variable para almacenar el usuario que es logueo

    //Creamos referencias a las colecciones de los ejercicios, entrenaminetos, equipos y usuarios
    private CollectionReference collectionReference_ejercicios, collectionReference_entrenamientos, collectionReference_equipos, collectionReference_usuario;
    private DocumentReference documentReference_ejercicio; //Creamos la referencia al ejercicio
    private AdaptadorEjercicios adaptadorEjercicios; //Creamos el adaptador de los ejercicios

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_entrenamientos);

        //Inicializamos el RecyclerView
        recyclerView_ejercicios = findViewById(R.id.recyclerViewEjerciciosFragmentEjercicios);
        recyclerView_ejercicios.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //Inicializamos el FloatingActionButton
        floatingActionButton_nuevoEjercicio = findViewById(R.id.floatingActionButtonNuevoEjercicio);

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Creamos un String y le asignamos el correo del usuario logueado actual obtenido
        usuarioLogueado = auth.getCurrentUser();
        String correo = Objects.requireNonNull(usuarioLogueado).getEmail();

        //Creamos el objeto SharedPreferences y un String para obtener y almacenar el nombre del equipo al que queremos añadir el entrenamiento
        SharedPreferences sharedPreferencesEquipo = getSharedPreferences("datos", MODE_PRIVATE);
        String nombreEquipo = sharedPreferencesEquipo.getString("nombreEquipo", null);

        //Creamos el objeto SharedPreferences y un String para obtener y almacenar la fecha del entrenamiento al que queremos añadir el ejercicio
        SharedPreferences sharedPreferencesEntrenamiento = getSharedPreferences("entrenamiento", Context.MODE_PRIVATE);
        String fechaEntrenamiento = sharedPreferencesEntrenamiento.getString("fechaEntrenamiento", null);

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");
        collectionReference_entrenamientos = collectionReference_equipos.document(nombreEquipo).collection("Entrenamientos");
        collectionReference_ejercicios = collectionReference_entrenamientos.document(fechaEntrenamiento).collection("Ejercicios");

        //Creamos la query para consultar la base de datos
        Query query = collectionReference_ejercicios;

        //Creamos el objeto FirestoreRecyclerOptions de tipo Ejercicios y le asignamos la query
        FirestoreRecyclerOptions<Ejercicio> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Ejercicio>().setQuery(query, Ejercicio.class).build();

        //Creamos, asignamos el adaptador del RecyclerView, asignandole el objeto FirestoreRecyclerOptions y controlamos cuando se hace click sobre cualquier elemento
        adaptadorEjercicios = new AdaptadorEjercicios(firestoreRecyclerOptions, new AdaptadorEjercicios.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                //Creamos un objeto Ejercicio para obtener sus datos almacenados y obtenemos el título del ejercicio
                Ejercicio ejercicio = documentSnapshot.toObject(Ejercicio.class);
                String tituloEjercicio = ejercicio.getTituloEjercicio();

                //Inicializamos la referencia al documento con el título del mismo y llamamos al método que muestra su descripción
                documentReference_ejercicio = collectionReference_ejercicios.document(tituloEjercicio);
                mostrarDescripcion(tituloEjercicio);
            }

            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                //Creamos un String para almacenar el ID del ejercicio y creamos una referencia al documento con el ID obtenido
                String idEjercicio = documentSnapshot.getId();
                DocumentReference documentReference_ejercicio = collectionReference_ejercicios.document(idEjercicio);

                //Creamos un AlertDialog que se muestra en la actividad actual, le asignamos un título y un mensaje
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardEntrenamientos.this);
                builder.setTitle("Eliminar ejercicio");
                builder.setMessage("¿Está seguro que desea eliminar este ejercicio?");

                //Método que controla el click sobre uno de sus botones de control
                builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Eliminamos el documento de la base de datos
                        documentReference_ejercicio.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Creamos y mostramos un mensaje emergente informando que el ejercicio ha sido eliminado correctamente
                                Toast.makeText(getApplicationContext(), "Ejercicio eliminado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Creamos y mostramos un mensaje emergente informando que ha ocurrido un error al eliminar el ejercicio
                                Toast.makeText(getApplicationContext(), "Ha habido un error eliminando el ejercicio", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                //Asginamos el texto "Cancelar" a otro botón de control del AlertDialog, lo creamos y lo mostramos
                builder.setNegativeButton("Cancelar", null);
                builder.create().show();
            }
        });

        //Método al hacer click en el FloatingActionButton para crear un nuevo ejercicio
        floatingActionButton_nuevoEjercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos y comenzamos la actividad para crear un nuevo ejercicio
                startActivity(new Intent(getApplicationContext(), NuevoEjercicio.class));
            }
        });

        //Asignamos el adaptador al RecyclerView y le notificamos de cualquier cambio posible
        adaptadorEjercicios.notifyDataSetChanged();
        recyclerView_ejercicios.setAdapter(adaptadorEjercicios);
    }

    //Método que muestra la descripción del ejercicio
    private void mostrarDescripcion(String tituloEjercicio) {
        //Creamos un objeto MaterialAlertDialogBuilder, que crea un Dialog en forma de CardView
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        //Creamos el inflater para convertir el XML con el Dialog personalizado en una vista para el Dashboard
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.custom_descripcion_ejercicio, null);

        //Asignamos nuestro Dialog personalizado para que se muestre este y no el predeterminado de Android Studio
        builder.setView(dialogView);

        //Método que controla el click sobre uno de sus botones de control, el de "Cerrar"
        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {

            //Método OnClick del botón
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cerramos el MaterialAlertDialogBuilder
                dialog.dismiss();
            }
        });

        //Método que crea la ventana emergente del AlertDialog
        AlertDialog dialog = builder.create();

        //Modificamos lo que muestra el Dialog cuando se carga en pantalla, en este caso actualizamos el Dialog con los datos del ejercicio seleccionado
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                documentReference_ejercicio.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //Creamos e inicializamos los TextViews del AlertDialog
                        TextView textView_tituloEjercicio = dialog.findViewById(R.id.textViewTituloEjercicioDialog);
                        TextView textView_duracionEjercicio = dialog.findViewById(R.id.textViewDuracionEjercicioDialog);
                        TextView textView_descripcionEjercicio = dialog.findViewById(R.id.textViewDescripcionEjercicioDialog);

                        //Creamos los Strings para almacenar la descripción y duración del ejercicio
                        String descripcionEjercicio = documentSnapshot.getString("descripcionEjercicio");
                        String duracionEjercicio = String.valueOf(documentSnapshot.get("duracionEjercicio"));

                        //Asignamos los Strings a los TextViews del AlertDialog
                        textView_tituloEjercicio.setText(tituloEjercicio);
                        textView_duracionEjercicio.setText(duracionEjercicio + " minutos");
                        textView_descripcionEjercicio.setText(descripcionEjercicio);
                    }
                });
            }
        });

        //Mostramos el AlertDialog
        dialog.show();
    }

    //Método para controlar el inicio de la actividad y la actividad del adaptador
    @Override
    public void onStart() {
        super.onStart();
        adaptadorEjercicios.startListening();
    }

    //Método para controlar el fin de la actividad y la actividad del adaptador
    @Override
    public void onStop() {
        super.onStop();
        adaptadorEjercicios.stopListening();
    }
}