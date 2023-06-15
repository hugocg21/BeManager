package com.hugocg21.bemanager.Entrenamientos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hugocg21.bemanager.Adaptadores.AdaptadorEntrenamientos;
import com.hugocg21.bemanager.Clases.Entrenamiento;
import com.hugocg21.bemanager.R;

import java.util.Objects;

public class EntrenamientosFragment extends Fragment {
    private RecyclerView recyclerView_entrenamientos; //Creamos el RecyclerView del Fragment de los entrenamientos
    private FirebaseFirestore database; //Creamos el objeto de la base de datos
    private FirebaseAuth auth; //Creamos el objeto de la autenticación de usuario
    private FirebaseUser usuarioLogueado; //Creamos el objeto de usuario

    //Creamos las referencias a las colecciones entrenamientos, equipos y usuarios
    private CollectionReference collectionReference_entrenamientos, collectionReference_equipos, collectionReference_usuario;
    private AdaptadorEntrenamientos adaptadorEntrenamientos; //Creamos el adaptador
    private ImageView imageView_ordenarEntrenamientosAscendiente, imageView_ordenarEntrenamientosDescendiente; //Creamos los ImageViews

    //Constructor del Fragment de los entrenaminetos
    public EntrenamientosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflamos la vista del Fragment y asignamos el RecyclerView como Layout principal
        View view = inflater.inflate(R.layout.fragment_entrenamientos, container, false);
        recyclerView_entrenamientos = view.findViewById(R.id.recyclerViewEntrenamientosFragmentEntrenamientos);
        recyclerView_entrenamientos.setLayoutManager(new LinearLayoutManager(getContext()));

        //Inicializamos los ImageViews
        imageView_ordenarEntrenamientosAscendiente = view.findViewById(R.id.imageViewOrdenarFragmentEntrenamientosAscendiente);
        imageView_ordenarEntrenamientosDescendiente = view.findViewById(R.id.imageViewOrdenarFragmentEntrenamientosDescendiente);

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Creamos un String y le asignamos el correo del usuario logueado actual obtenido
        usuarioLogueado = auth.getCurrentUser();
        String correo = Objects.requireNonNull(usuarioLogueado).getEmail();

        //Creamos el objeto SharedPreferences y un String para obtener y almacenar el nombre del equipo al que queremos añadir el entrenamiento
        SharedPreferences sharedPreferencesEquipo = getContext().getSharedPreferences("datos", Context.MODE_PRIVATE);
        String nombreEquipo = sharedPreferencesEquipo.getString("nombreEquipo", null);

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");
        collectionReference_entrenamientos = collectionReference_equipos.document(nombreEquipo).collection("Entrenamientos");

        //Creamos la query para consultar la base de datos
        Query query = collectionReference_entrenamientos;

        //Creamos el objeto FirestoreRecyclerOptions de tipo Entrenamiento y le asignamos la query
        FirestoreRecyclerOptions<Entrenamiento> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Entrenamiento>().setQuery(query, Entrenamiento.class).build();

        //Creamos, asignamos el adaptador del RecyclerView, asignandole el objeto FirestoreRecyclerOptions y controlamos cuando se hace click sobre cualquier elemento
        adaptadorEntrenamientos = new AdaptadorEntrenamientos(firestoreRecyclerOptions, new AdaptadorEntrenamientos.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                //Creamos un objeto Entrenamiento para obtener sus datos almacenados y obtenemos la fecha (día) del entrenamiento
                Entrenamiento entrenamiento = documentSnapshot.toObject(Entrenamiento.class);
                String fechaEntrenamiento = entrenamiento.getFechaEntrenamiento();

                //Creamos el editor de SharedPreferences para guardar la fecha del entrenamiento
                SharedPreferences.Editor editor = getContext().getSharedPreferences("entrenamiento", Context.MODE_PRIVATE).edit();
                editor.putString("fechaEntrenamiento", fechaEntrenamiento);
                editor.apply();

                //Creamos y comenzamos la actividad del dashboard del entrenamiento y finalizamos la actividad
                startActivity(new Intent(getContext(), DashboardEntrenamientos.class));
                getActivity().finish();
            }

            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                //Creamos un String para almacenar el ID del entrenamiento y creamos una referencia al documento con el ID obtenido
                String idEntrenamiento = documentSnapshot.getId();
                DocumentReference documentReference_entrenamiento = collectionReference_entrenamientos.document(idEntrenamiento);

                //Creamos un AlertDialog que se muestra en la actividad actual, le asignamos un título y un mensaje
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Eliminar entrenamiento");
                builder.setMessage("¿Está seguro que desea eliminar este entrenamiento?");

                //Método que controla el click sobre uno de sus botones de control
                builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Eliminamos el documento de la base de datos
                        documentReference_entrenamiento.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Creamos y mostramos un mensaje emergente informando que el entrenamiento ha sido eliminado correctamente
                                Toast.makeText(getContext(), "Entrenamiento eliminado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Creamos y mostramos un mensaje emergente informando que ha ocurrido un error al eliminar el entrenamiento
                                Toast.makeText(getContext(), "Ha habido un error eliminando el entrenamiento", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                //Asginamos el texto "Cancelar" a otro botón de control del AlertDialog, lo creamos y lo mostramos
                builder.setNegativeButton("Cancelar", null);
                builder.create().show();
            }
        });

        //Asignamos el adaptador al RecyclerView y le notificamos de cualquier cambio posible
        adaptadorEntrenamientos.notifyDataSetChanged();
        recyclerView_entrenamientos.setAdapter(adaptadorEntrenamientos);

        //Método al hacer click en el ImageView de ordenar a los entrenamientos de manera Acendiente
        imageView_ordenarEntrenamientosAscendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Llamamos al método para ordenar a los entrenamientos de manera ascendiente
                ordernarentrenamientosAscendente();
            }
        });

        //Método al hacer click en el ImageView de ordenar a los entrenamientos de manera Descendiente
        imageView_ordenarEntrenamientosDescendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Llamámos al método para ordenar a los entrenamientos de manera descendiente
                ordernarentrenamientosDescendiente();
            }
        });

        //Devolvemos el view del fragment
        return view;
    }

    //Método que ordena a los entrenamientos de manera descendiente según la fecha del entrenamiento
    private void ordernarentrenamientosDescendiente() {
        //Creamos la query con la condición de ordenar por la fecha de manera descendiente
        Query query = collectionReference_entrenamientos.orderBy("fechaEntrenamiento", Query.Direction.DESCENDING);

        //Creamos el objeto FirestoreRecyclerOptions de tipo Entrenamiento y le asignamos la query nueva con la condición de ordenar y actualizamos las opciones del filtro
        FirestoreRecyclerOptions<Entrenamiento> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Entrenamiento>()
                .setQuery(query, Entrenamiento.class).build();
        adaptadorEntrenamientos.actualizarOpciones(firestoreRecyclerOptions);

        //Cambiamos la visibilidad de los ImageView para ocultar el de ordenar de manera descendiente y mostrar el ascendiente
        imageView_ordenarEntrenamientosDescendiente.setVisibility(View.INVISIBLE);
        imageView_ordenarEntrenamientosAscendiente.setVisibility(View.VISIBLE);
    }

    //Método que ordena a los entrenamientos de manera ascendiente según la fecha del entrenamiento
    private void ordernarentrenamientosAscendente() {
        //Creamos la query con la condición de ordenar por la fecha de manera ascendiente
        Query query = collectionReference_entrenamientos.orderBy("fechaEntrenamiento", Query.Direction.ASCENDING);

        //Creamos el objeto FirestoreRecyclerOptions de tipo Entrenamiento y le asignamos la query nueva con la condición de ordenar y actualizamos las opciones del filtro
        FirestoreRecyclerOptions<Entrenamiento> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Entrenamiento>()
                .setQuery(query, Entrenamiento.class).build();
        adaptadorEntrenamientos.actualizarOpciones(firestoreRecyclerOptions);

        //Cambiamos la visibilidad de los ImageView para ocultar el de ordenar de manera ascendiente y mostrar el descendiente
        imageView_ordenarEntrenamientosAscendiente.setVisibility(View.INVISIBLE);
        imageView_ordenarEntrenamientosDescendiente.setVisibility(View.VISIBLE);
    }

    //Método para controlar el inicio de la actividad y la actividad del adaptador
    @Override
    public void onStart() {
        super.onStart();
        adaptadorEntrenamientos.startListening();
    }

    //Método para controlar el fin de la actividad y la actividad del adaptador
    @Override
    public void onStop() {
        super.onStop();
        adaptadorEntrenamientos.stopListening();
    }
}