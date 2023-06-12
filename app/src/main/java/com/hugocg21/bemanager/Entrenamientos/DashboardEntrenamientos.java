package com.hugocg21.bemanager.Entrenamientos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.hugocg21.bemanager.Clases.Entrenamiento;
import com.hugocg21.bemanager.R;

import java.util.Objects;

public class DashboardEntrenamientos extends AppCompatActivity {
    private RecyclerView recyclerView_ejercicios;
    private FloatingActionButton floatingActionButton_nuevoEjercicio;
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private FirebaseUser usuarioLogueado;
    private CollectionReference collectionReference_ejercicios, collectionReference_entrenamientos, collectionReference_equipos, collectionReference_usuario;
    private DocumentReference documentReference_ejercicio;
    private AdaptadorEjercicios adaptadorEjercicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_entrenamientos);

        recyclerView_ejercicios = findViewById(R.id.recyclerViewEjerciciosFragmentEjercicios);
        recyclerView_ejercicios.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        floatingActionButton_nuevoEjercicio = findViewById(R.id.floatingActionButtonNuevoEjercicio);

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Creamos un String y le asignamos el correo del usuario logueado actual obtenido
        usuarioLogueado = auth.getCurrentUser();
        String correo = Objects.requireNonNull(usuarioLogueado).getEmail();

        SharedPreferences sharedPreferencesEquipo = getSharedPreferences("datos", Context.MODE_PRIVATE);
        String nombreEquipo = sharedPreferencesEquipo.getString("nombreEquipo", null);

        SharedPreferences sharedPreferencesEntrenamiento = getSharedPreferences("entrenamiento", Context.MODE_PRIVATE);
        String fechaEntrenamiento = sharedPreferencesEntrenamiento.getString("fechaEntrenamiento", null);

        SharedPreferences sharedPreferencesEjercicio = getSharedPreferences("ejercicio", Context.MODE_PRIVATE);
        String tituloEjercicio = sharedPreferencesEjercicio.getString("tituloEjercicio", null);

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");
        collectionReference_entrenamientos = collectionReference_equipos.document(nombreEquipo).collection("Entrenamientos");
        collectionReference_ejercicios = collectionReference_entrenamientos.document(fechaEntrenamiento).collection("Ejercicios");

        Query query = collectionReference_ejercicios;

        FirestoreRecyclerOptions<Ejercicio> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Ejercicio>().setQuery(query, Ejercicio.class).build();

        adaptadorEjercicios = new AdaptadorEjercicios(firestoreRecyclerOptions, new AdaptadorEjercicios.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Ejercicio ejercicio = documentSnapshot.toObject(Ejercicio.class); // Corrección: utiliza toObject() para obtener el objeto Jugador
                String tituloEjercicio = ejercicio.getTituloEjercicio(); // Obtén el nombre del jugador

                documentReference_ejercicio = collectionReference_ejercicios.document(tituloEjercicio);

                mostrarDescripcion(tituloEjercicio);
            }
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                String idEjercicio = documentSnapshot.getId();
                DocumentReference documentReference_ejercicio = collectionReference_ejercicios.document(idEjercicio);

                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Eliminar ejercicio");
                builder.setMessage("¿Está seguro que desea eliminar este ejercicio?");
                builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        documentReference_ejercicio.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Ejercicio eliminado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Ha habido un error eliminando el ejercicio", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancelar", null);
                builder.create().show();
            }
        });

        floatingActionButton_nuevoEjercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NuevoEjercicio.class));
            }
        });

        adaptadorEjercicios.notifyDataSetChanged();
        recyclerView_ejercicios.setAdapter(adaptadorEjercicios);
    }

    private void mostrarDescripcion(String tituloEjercicio) {
        //Creamos un objeto MaterialAlertDialogBuilder, que crea un Dialog en forma de CardView
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        //Creamos el inflater para convertir el XML con el Dialog personalizado en una vista para el Activity Main
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.custom_descripcion_ejercicio, null);

        //Asignamos nuestro Dialog personalizado para que se muestre este y no el predeterminado de Android Studio
        builder.setView(dialogView);

        //Método que crea un botón de control de la ventana del Dialog, que lo llamamos "Cerrar", que sirve para cerrar el CardView
        //Este botón sirve para mostrar todas las estadísticas del jugador seleccionado, no se puede editar nada
        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {

            //Método OnClick del botón
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Método que crea la ventana emergente del Dialog
        AlertDialog dialog = builder.create();

        //Modificamos lo que muestra el Dialog cuando se carga en pantalla, en este caso actualizamos el Dialog con las estadísticas del jugador seleccionado
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                documentReference_ejercicio.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        TextView textView_tituloEjercicio = dialog.findViewById(R.id.textViewTituloEjercicioDialog);
                        TextView textView_duracionEjercicio = dialog.findViewById(R.id.textViewDuracionEjercicioDialog);
                        TextView textView_descripcionEjercicio = dialog.findViewById(R.id.textViewDescripcionEjercicioDialog);

                        // Obtén los datos del documento
                        String descripcionEjercicio = documentSnapshot.getString("descripcionEjercicio");
                        String duracionEjercicio = String.valueOf(documentSnapshot.get("duracionEjercicio"));

                        // Configura los valores en los TextViews
                        textView_tituloEjercicio.setText(tituloEjercicio);
                        textView_duracionEjercicio.setText(duracionEjercicio + " minutos");
                        textView_descripcionEjercicio.setText(descripcionEjercicio);
                    }
                });
            }
        });

        //Método que muestra el Dialog por pantalla
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        adaptadorEjercicios.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptadorEjercicios.stopListening();
    }
}