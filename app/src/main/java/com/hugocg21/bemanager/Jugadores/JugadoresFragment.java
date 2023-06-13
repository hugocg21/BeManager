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
    private AdaptadorJugadores adaptadorJugadores; //Cremos el adaptador
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

        //Creamos el objeto SharedPreferences y un String para obtener y almacenar el nombre del equipo del que queremos mostarar los jugadoes
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("datos", Context.MODE_PRIVATE);
        String nombreEquipo = sharedPreferences.getString("nombreEquipo", null);

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");
        collectionReference_jugadores = collectionReference_equipos.document(nombreEquipo).collection("Jugadores");

        //Creamos la query para consultar la base de datos
        Query query = collectionReference_jugadores;

        FirestoreRecyclerOptions<Jugador> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Jugador>().setQuery(query, Jugador.class).build();

        adaptadorJugadores = new AdaptadorJugadores(firestoreRecyclerOptions, new AdaptadorJugadores.OnItemClickListener() {
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                String idJugador = documentSnapshot.getId();
                DocumentReference documentReference_jugador = collectionReference_jugadores.document(idJugador);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Eliminar jugador");
                builder.setMessage("¿Está seguro que desea eliminar a este jugador?");
                builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        documentReference_jugador.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Jugador eliminado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Ha habido un error eliminando el jugador", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancelar", null);
                builder.create().show();
            }
        });

        adaptadorJugadores.notifyDataSetChanged();
        recyclerView_jugadores.setAdapter(adaptadorJugadores);

        imageView_filtroJugadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarFiltroDialog();
            }
        });

        imageView_ordenarJugadoresAscendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ordernarJugadoresAscendente();
            }
        });

        imageView_ordenarJugadoresDescendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ordernarJugadoresDescendiente();
            }
        });

        return view;
    }

    private void ordernarJugadoresDescendiente() {
        Query query;
        if (filtroActual != null) {
            query = collectionReference_jugadores.orderBy(filtroActual, Query.Direction.DESCENDING);
        } else {
            query = collectionReference_jugadores.orderBy("nombreJugador", Query.Direction.ASCENDING);
        }

        FirestoreRecyclerOptions<Jugador> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Jugador>()
                .setQuery(query, Jugador.class).build();
        adaptadorJugadores.actualizarOpciones(firestoreRecyclerOptions);

        imageView_ordenarJugadoresDescendiente.setVisibility(View.INVISIBLE);
        imageView_ordenarJugadoresAscendiente.setVisibility(View.VISIBLE);
    }

    private void ordernarJugadoresAscendente() {
        Query query;
        if (filtroActual != null) {
            query = collectionReference_jugadores.orderBy(filtroActual, Query.Direction.ASCENDING);
        } else {
            query = collectionReference_jugadores.orderBy("nombreJugador", Query.Direction.ASCENDING);
        }

        FirestoreRecyclerOptions<Jugador> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Jugador>()
                .setQuery(query, Jugador.class).build();
        adaptadorJugadores.actualizarOpciones(firestoreRecyclerOptions);

        imageView_ordenarJugadoresAscendiente.setVisibility(View.INVISIBLE);
        imageView_ordenarJugadoresDescendiente.setVisibility(View.VISIBLE);
    }

    private void mostrarFiltroDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ordenar jugadores");
        String[] opcionesOrder = {"Dorsal", "Nombre", "Posición"};
        builder.setItems(opcionesOrder, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String opcionElegida = opcionesOrder[i];
                if (opcionElegida.equals("Dorsal")) {
                    filtroActual = "dorsalJugador";
                } else if (opcionElegida.equals("Nombre")) {
                    filtroActual = "nombreJugador";
                } else {
                    filtroActual = "posicionJugadorNumero";
                }

                ordernarJugadores(filtroActual);
            }
        });

        builder.show();
    }

    private void ordernarJugadores(String filtroActual) {
        Query query = collectionReference_jugadores.orderBy(filtroActual);
        FirestoreRecyclerOptions<Jugador> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Jugador>().setQuery(query, Jugador.class).build();
        adaptadorJugadores.actualizarOpciones(firestoreRecyclerOptions);

        imageView_ordenarJugadoresAscendiente.setVisibility(View.INVISIBLE);
        imageView_ordenarJugadoresDescendiente.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        adaptadorJugadores.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptadorJugadores.stopListening();
    }
}