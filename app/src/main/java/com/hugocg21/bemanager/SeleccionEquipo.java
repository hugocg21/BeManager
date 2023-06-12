package com.hugocg21.bemanager;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hugocg21.bemanager.Adaptadores.AdaptadorEquipos;
import com.hugocg21.bemanager.Clases.Equipo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeleccionEquipo extends AppCompatActivity {
    private GridView gridView; //Creamos el GridView
    private AdaptadorEquipos adaptadorEquipos; //Creamos el adaptador personalizado
    private List<Equipo> listaEquipos; //Creamos la lista de equipos
    private FirebaseFirestore database; //Creamos la variable de la base de datos
    private FirebaseAuth auth; //Creamos la variable de autenticación
    private FirebaseUser usuarioLogueado; //Creamos una variable para almacenar el usuario que es logueo

    //Creamos las referencias a las colecciones de equipos y usuario
    private CollectionReference collectionReference_equipos, collectionReference_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Creamos un String y le asignamos el correo del usuario logueado actual obtenido
        usuarioLogueado = auth.getCurrentUser();
        String correo = usuarioLogueado.getEmail();

        //Inicializamos las referencias a las colecciones
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");

        //Inicializamos el GridView y el ArrayList de la lista de equipos
        gridView = findViewById(R.id.gridViewEquiposGridLayout);
        listaEquipos = new ArrayList<>();

        //Inicializamos el adaptador de los equipos, dandole el estilo personalizado y la lista de equipos
        adaptadorEquipos = new AdaptadorEquipos(this, R.layout.custom_equipo, listaEquipos);

        //Asignamos el adaptador al GridView
        gridView.setAdapter(adaptadorEquipos);

        //Recogemos la referencia a la colección de los equipos y no hay problemas, entra aquí
        collectionReference_equipos.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                //Si hay algún error, entramos en este if
                if (error != null) {
                    //Mostramos por consola este mensaje
                    Log.e(TAG, "Error al obtener y mostrar los equipos: ", error);
                    return;
                }

                //Como no hay errores, vaciamos la lista
                listaEquipos.clear();

                //Por cada documento en la colección llenamos la lista
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    //Creamos un objeto de tipo Equipo, cogiendo los datos de cada documento y pasandolos a tipo Eqipo
                    Equipo equipo = documentSnapshot.toObject(Equipo.class);

                    //Añadimos el objeto a la lista
                    listaEquipos.add(equipo);
                }

                //Notificamos al adaptador que los datos han cambiado
                adaptadorEquipos.notifyDataSetChanged();
            }
        });
    }
}