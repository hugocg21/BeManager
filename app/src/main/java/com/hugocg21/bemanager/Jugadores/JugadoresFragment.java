package com.hugocg21.bemanager.Jugadores;

import android.annotation.SuppressLint;
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
import com.hugocg21.bemanager.Adaptadores.AdaptadorJugadores;
import com.hugocg21.bemanager.Clases.Jugador;
import com.hugocg21.bemanager.R;

import java.util.Objects;

public class JugadoresFragment extends Fragment {
    private RecyclerView recyclerView_jugadores; //Creamos el RecyclerView del Fragment de los jugadores
    private FirebaseFirestore database; //Creamos el objeto de la base de datos
    private FirebaseAuth auth; //Creamos el objeto de la autenticación de usuario
    private FirebaseUser usuarioLogueado; //Creamos el objeto de usuario

    //Creamos las referencias a las colecciones jugadores, equipos y usuarios
    private CollectionReference collectionReference_jugadores, collectionReference_equipos, collectionReference_usuario;
    private AdaptadorJugadores adaptadorJugadores; //Creamos el adaptador
    private ImageView imageView_filtroJugadores, imageView_ordenarJugadoresAscendiente, imageView_ordenarJugadoresDescendiente; //Creamos los ImageViews
    private String filtroActual; //Creamos un String para almacenar el filtro actual

    //Constructor del Fragment de los jugadores
    public JugadoresFragment() {
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflamos la vista del Fragment y asignamos el RecyclerView como Layout principal
        View view = inflater.inflate(R.layout.fragment_jugadores, container, false);
        recyclerView_jugadores = view.findViewById(R.id.recyclerViewJugadoresFragmentJugadores);
        recyclerView_jugadores.setLayoutManager(new LinearLayoutManager(getContext()));

        //Inicializamos los ImageViews
        imageView_filtroJugadores = view.findViewById(R.id.imageViewFiltroFragmentJugadores);
        imageView_ordenarJugadoresAscendiente = view.findViewById(R.id.imageViewOrdenarFragmentJugadoresAscendiente);
        imageView_ordenarJugadoresDescendiente = view.findViewById(R.id.imageViewOrdenarFragmentJugadoresDescendiente);

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
        collectionReference_jugadores = collectionReference_equipos.document(nombreEquipo).collection("Jugadores");

        //Creamos la query para consultar la base de datos
        Query query = collectionReference_jugadores;

        //Creamos el objeto FirestoreRecyclerOptions de tipo Jugador y le asignamos la query
        FirestoreRecyclerOptions<Jugador> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Jugador>().setQuery(query, Jugador.class).build();

        //Creamos, asignamos el adaptador del RecyclerView, asignandole el objeto FirestoreRecyclerOptions y controlamos cuando se hace click sobre cualquier elemento
        adaptadorJugadores = new AdaptadorJugadores(firestoreRecyclerOptions, new AdaptadorJugadores.OnItemClickListener() {
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                //Creamos un String para almacenar el ID del jugador y creamos una referencia al documento con el ID obtenido
                String idJugador = documentSnapshot.getId();
                DocumentReference documentReference_jugador = collectionReference_jugadores.document(idJugador);

                //Creamos un AlertDialog que se muestra en la actividad actual, le asignamos un título y un mensaje
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Eliminar jugador");
                builder.setMessage("¿Está seguro que desea eliminar a este jugador?");

                //Método que controla el click sobre uno de sus botones de control
                builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Eliminamos el documento de la base de datos
                        documentReference_jugador.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Creamos y mostramos un mensaje emergente informando que el jugador ha sido eliminado correctamente
                                Toast.makeText(getContext(), "Jugador eliminado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Creamos y mostramos un mensaje emergente informando que ha ocurrido un error al eliminar el jugador
                                Toast.makeText(getContext(), "Ha habido un error eliminando el jugador", Toast.LENGTH_SHORT).show();
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
        adaptadorJugadores.notifyDataSetChanged();
        recyclerView_jugadores.setAdapter(adaptadorJugadores);

        imageView_filtroJugadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarFiltroDialog();
            }
        });

        //Método al hacer click en el ImageView de ordenar a los jugadores de manera Acendiente
        imageView_ordenarJugadoresAscendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Llamamos al método para ordenar a los jugadores de manera ascendiente
                ordernarJugadoresAscendente();
            }
        });

        //Método al hacer click en el ImageView de ordenar a los jugadores de manera Descendiente
        imageView_ordenarJugadoresDescendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Llamámos al método para ordenar a los jugadores de manera descendiente
                ordernarJugadoresDescendiente();
            }
        });

        //Devolvemos el view del fragment
        return view;
    }

    //Método que ordena a los jugadores de manera descendiente según la fecha del entrenamiento
    private void ordernarJugadoresDescendiente() {
        //Creamos la query con la condición de ordenar de manera descendiente, comprobando el filtro actual
        Query query;
        if (filtroActual != null) {
            //Si el filtro tiene un valor, ordenamos por ese filtro
            query = collectionReference_jugadores.orderBy(filtroActual, Query.Direction.DESCENDING);
        } else {
            //Si no hay ningún filtro seleccionado, ordenamos por el nombre del jugador
            query = collectionReference_jugadores.orderBy("nombreJugador", Query.Direction.ASCENDING);
        }

        //Creamos el objeto FirestoreRecyclerOptions de tipo Jugador y le asignamos la query nueva con la condición de ordenar y actualizamos las opciones del filtro
        FirestoreRecyclerOptions<Jugador> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Jugador>()
                .setQuery(query, Jugador.class).build();
        adaptadorJugadores.actualizarOpciones(firestoreRecyclerOptions);

        //Cambiamos la visibilidad de los ImageView para ocultar el de ordenar de manera descendiente y mostrar el ascendiente
        imageView_ordenarJugadoresDescendiente.setVisibility(View.INVISIBLE);
        imageView_ordenarJugadoresAscendiente.setVisibility(View.VISIBLE);
    }

    //Método que ordena a los jugadores de manera ascendiente según la fecha del entrenamiento
    private void ordernarJugadoresAscendente() {
        //Creamos la query con la condición de ordenar de manera descendiente, comprobando el filtro actual
        Query query;
        if (filtroActual != null) {
            //Si el filtro tiene un valor, ordenamos por ese filtro
            query = collectionReference_jugadores.orderBy(filtroActual, Query.Direction.ASCENDING);
        } else {
            //Si no hay ningún filtro seleccionado, ordenamos por el nombre del jugador
            query = collectionReference_jugadores.orderBy("nombreJugador", Query.Direction.ASCENDING);
        }

        //Creamos el objeto FirestoreRecyclerOptions de tipo Jugador y le asignamos la query nueva con la condición de ordenar y actualizamos las opciones del filtro
        FirestoreRecyclerOptions<Jugador> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Jugador>()
                .setQuery(query, Jugador.class).build();
        adaptadorJugadores.actualizarOpciones(firestoreRecyclerOptions);

        //Cambiamos la visibilidad de los ImageView para ocultar el de ordenar de manera descendiente y mostrar el ascendiente
        imageView_ordenarJugadoresAscendiente.setVisibility(View.INVISIBLE);
        imageView_ordenarJugadoresDescendiente.setVisibility(View.VISIBLE);
    }

    //Método que crea y muetra el Dialog del filtro
    private void mostrarFiltroDialog() {
        //Creamos un AlertDialog que se muestra en la actividad actual, le asignamos un título y las opciones del filtro
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ordenar jugadores");
        String[] opcionesOrder = {"Dorsal", "Nombre", "Posición"};

        //Asignamos las opciones de filtrado al AlertDialog
        builder.setItems(opcionesOrder, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Creamos un String y recogemos la opción elegida en el filtro
                String opcionElegida = opcionesOrder[i];
                if (opcionElegida.equals("Dorsal")) {
                    //Si el usuario eligio el dorsal del jugador, le asginamos la cadena al String
                    filtroActual = "dorsalJugador";
                } else if (opcionElegida.equals("Nombre")) {
                    //Si el usuario eligio el nombre del jugador, le asginamos la cadena al String
                    filtroActual = "nombreJugador";
                } else {
                    //Si el usuario eligio la posición del jugador, le asginamos la cadena al String
                    filtroActual = "posicionJugadorNumero";
                }

                //Llamámos al método para ordenar a los jugadores pasándole el filtro seleccionado
                ordernarJugadores(filtroActual);
            }
        });

        //Mostramos el AlertDialog
        builder.show();
    }

    //Método que ordena a los jugadores según el filtro que reciba
    private void ordernarJugadores(String filtroActual) {
        //Creamos la query para ordenar según el filtro deseado
        Query query = collectionReference_jugadores.orderBy(filtroActual);

        //Creamos el objeto FirestoreRecyclerOptions de tipo Jugador y le asignamos la query nueva con la condición de ordenar y actualizamos las opciones del filtro
        FirestoreRecyclerOptions<Jugador> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Jugador>().setQuery(query, Jugador.class).build();
        adaptadorJugadores.actualizarOpciones(firestoreRecyclerOptions);

        //Cambiamos la visibilidad de los ImageView para ocultar el de ordenar de manera descendiente y mostrar el ascendiente
        imageView_ordenarJugadoresAscendiente.setVisibility(View.INVISIBLE);
        imageView_ordenarJugadoresDescendiente.setVisibility(View.VISIBLE);
    }

    //Método para controlar el inicio de la actividad y la actividad del adaptador
    @Override
    public void onStart() {
        super.onStart();
        adaptadorJugadores.startListening();
    }

    //Método para controlar el fin de la actividad y la actividad del adaptador
    @Override
    public void onStop() {
        super.onStop();
        adaptadorJugadores.stopListening();
    }
}