package com.hugocg21.bemanager.Entrenamientos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    RecyclerView recyclerView_entrenamientos;
    FirebaseAuth auth;
    FirebaseFirestore database;
    FirebaseUser usuarioLogueado;
    CollectionReference collectionReference_entrenamientos, collectionReference_equipos, collectionReference_usuario;
    AdaptadorEntrenamientos adaptadorEntrenamientos;

    public EntrenamientosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrenamientos, container, false);

        recyclerView_entrenamientos = view.findViewById(R.id.recyclerViewEntrenamientosFragmentEntrenamientos);
        recyclerView_entrenamientos.setLayoutManager(new LinearLayoutManager(getContext()));

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Creamos un String y le asignamos el correo del usuario logueado actual obtenido
        usuarioLogueado = auth.getCurrentUser();
        String correo = Objects.requireNonNull(usuarioLogueado).getEmail();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("datos", Context.MODE_PRIVATE);
        String nombreEquipo = sharedPreferences.getString("nombreEquipo", null);

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");
        collectionReference_entrenamientos = collectionReference_equipos.document(nombreEquipo).collection("Entrenamientos");

        Query query = collectionReference_entrenamientos;

        FirestoreRecyclerOptions<Entrenamiento> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Entrenamiento>().setQuery(query, Entrenamiento.class).build();

        adaptadorEntrenamientos = new AdaptadorEntrenamientos(firestoreRecyclerOptions, new AdaptadorEntrenamientos.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Entrenamiento entrenamiento = documentSnapshot.toObject(Entrenamiento.class); // Corrección: utiliza toObject() para obtener el objeto Jugador
                String fechaEntrenamiento = entrenamiento.getFechaEntrenamiento(); // Obtén el nombre del jugador

                SharedPreferences.Editor editor = getContext().getSharedPreferences("entrenamiento", Context.MODE_PRIVATE).edit();
                editor.putString("fechaEntrenamiento", fechaEntrenamiento);
                editor.apply();

                startActivity(new Intent(getContext(), DashboardEntrenamientos.class));
                getActivity().finish();
            }

            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                String idEntrenamiento = documentSnapshot.getId();
                DocumentReference documentReference_entrenamiento = collectionReference_entrenamientos.document(idEntrenamiento);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Eliminar entrenamiento");
                builder.setMessage("¿Está seguro que desea eliminar este entrenamiento?");
                builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        documentReference_entrenamiento.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Entrenamiento eliminado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Ha habido un error eliminando el entrenamiento", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancelar", null);
                builder.create().show();
            }
        });

        adaptadorEntrenamientos.notifyDataSetChanged();
        recyclerView_entrenamientos.setAdapter(adaptadorEntrenamientos);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adaptadorEntrenamientos.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptadorEntrenamientos.stopListening();
    }
}