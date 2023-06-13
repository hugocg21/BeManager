package com.hugocg21.bemanager;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hugocg21.bemanager.Adaptadores.AdaptadorEquipos;
import com.hugocg21.bemanager.Clases.Equipo;
import com.hugocg21.bemanager.Equipo.DashboardEquipo;
import com.hugocg21.bemanager.Equipo.NuevoEquipo;
import com.hugocg21.bemanager.Login.Login;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    TextView textView_nombreLogueado, textView_correoLogueadoHeader, textView_nombreUsuarioLogueadoHeader; //Creamos el TextView del usuario logueado
    private GridView gridView; //Creamos el GridView del Dashboard
    private AdaptadorEquipos adaptadorEquipos; //Creamos el adaptador para los CardView de los equipos
    private DrawerLayout drawerLayout; //Creamos el DrawerLayout del menú lateral
    private ActionBarDrawerToggle drawerToggle; //Creamos la barra del DrawerLayout
    private NavigationView navigationView; //Creamos el NavigationView del Dashboard
    private FirebaseFirestore database; //Creamos la variable de la base de datos
    private FirebaseAuth auth; //Creamos la variable de autenticación
    private FirebaseUser usuarioLogueado; //Creamos una variable para almacenar el usuario que es logueo
    private CollectionReference collectionReference_equipos, collectionReference_usuario; //Creamos referencias a las colecciones de Equipo y Usuarios de la base de datos
    private List<Equipo> listaEquipos; //Creamos la lista de los equipos
    private Button cerrarSesion, anadirEquipo; //Creamos los Buttons del NavigationView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Creamos, definimos y asignamos la Toolbar de la actividad
        Toolbar toolbar = findViewById(R.id.toolbarDashboard);

        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        //Inicializamos el DrawerLayout, lo inflamos y lo asignamos al Dashboard
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //Inicializamos el NavigationView del Dashboard
        navigationView = findViewById(R.id.navigationViewDashboard);
        textView_correoLogueadoHeader = navigationView.findViewById(R.id.textViewCorreoHeader);
        textView_nombreUsuarioLogueadoHeader = navigationView.findViewById(R.id.textViewUsuarioHeader);
        cerrarSesion = navigationView.findViewById(R.id.buttonCerrarSesionNavigationViewDashboard);
        anadirEquipo = navigationView.findViewById(R.id.buttonAnadirNuevoEquipoNavigationViewDashboard);

        //Inicializamos el TextView del nombre de usuario logueado
        textView_nombreLogueado = findViewById(R.id.textViewNombreLogueadoDashboard);

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Creamos un String y le asignamos el correo del usuario logueado actual obtenido
        usuarioLogueado = auth.getCurrentUser();
        String correo = Objects.requireNonNull(usuarioLogueado).getEmail();

        //Inicializamos las referencias de los Equipos y Usuarios de la base de datos
        collectionReference_usuario = database.collection("Usuarios");
        collectionReference_equipos = collectionReference_usuario.document(Objects.requireNonNull(correo)).collection("Equipos");

        //Llamamos al métod que se encarga de asignar el nombre de usuario en el NavigationView y en el Dashboard
        actualizarInfoUsuarioHeaderDashboard();

        //Inicializamos la lista de equipos
        listaEquipos = new ArrayList<>();

        //Obtenemos los equipos creados en la base de datos
        obtenerEquipos();

        //Inicializamos el GridView del Dashboard
        gridView = findViewById(R.id.gridViewEquiposDashboard);

        //Inicializamos el adaptador de los equipos del GridView, pasandole el CardView personalizado
        adaptadorEquipos = new AdaptadorEquipos(this, R.layout.custom_equipo, listaEquipos);

        //Adaptamos los equipos de la lista con el CardView al GridView
        adaptarEquiposGrid();

        //Asignamos el adaptador al GridView
        gridView.setAdapter(adaptadorEquipos);

        //Método al hacer click en cada equipo del GridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Creamos un objeto de tipo Equipo para obtener el nombre del equipo sobre el que se hizo click
                Equipo equipo = listaEquipos.get(i);
                String nombreEquipo = equipo.getNombreEquipo();

                //Creamos el editor de SharedPreferences para guardar el nombre del equipo
                SharedPreferences.Editor editor = getSharedPreferences("datos", MODE_PRIVATE).edit();
                editor.putString("nombreEquipo", nombreEquipo);
                editor.apply();

                // Crea el Intent para iniciar el siguiente Activity
                startActivity(new Intent(getApplicationContext(), DashboardEquipo.class));
            }
        });

        //Método al hacer click en el botón de añadir un equipo
        anadirEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un Intent para abrir la actividad de nuevo equipo e iniciamos la actividad
                startActivity(new Intent(getApplicationContext(), NuevoEquipo.class));
            }
        });

        //Método al hacer clcik en el botón de cerrar sesión
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un archivo de SharedPreferences para almacenar los datos del usuario
                SharedPreferences.Editor editor = getSharedPreferences("datos", MODE_PRIVATE).edit();

                //Modificamos el booleano "recordar" para no recordar la sesión
                editor.putBoolean("recordar", false);
                editor.apply();

                //Creamos un Intent para abrir la actividad de Login e iniciamos la actividad
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    //Método para adaptar los equipos al GridView
    private void adaptarEquiposGrid() {
        //Recogemos la referencia a la colección de los equipos y no hay problemas, entra aquí
        collectionReference_equipos.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                //Si hay algún error, entramos en este if
                if (error != null) {
                    //Mostramos por consola este mensaje
                    Log.e(TAG, "Error fetching teams: ", error);
                    return;
                }

                //Como no hay errores, vaciamos la lista
                listaEquipos.clear();

                //Por cada documento en la colección llenamos la lista
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Equipo equipo = documentSnapshot.toObject(Equipo.class); //Creamos un objeto de tipo Equipo, cogiendo los datos de cada documento y pasandolos a tipo Eqipo
                    listaEquipos.add(equipo); //Añadimos el objeto a la lista
                }

                //Notificamos al adaptador que los datos han cambiado
                adaptadorEquipos.notifyDataSetChanged();
            }
        });
    }

    //Método para obtener los equipos de la base de datos
    private void obtenerEquipos() {
        //Recogemos la referencia a la colección de los equipos y no hay problemas, entra aquí
        collectionReference_equipos.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Vaciamos la lista de los equipos
                        listaEquipos.clear();

                        //Por cada documento en la colección llenamos la lista
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Equipo equipo = documentSnapshot.toObject(Equipo.class); //Creamos un objeto de tipo Equipo, cogiendo los datos de cada documento y pasandolos a tipo Eqipo
                            listaEquipos.add(equipo); //Añadimos el objeto anterior a la lista
                        }

                        //Notificamos al adaptador que los datos han cambiado
                        adaptadorEquipos.notifyDataSetChanged();
                    }
                })

                //Si hay problemas, entramos aquí
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Creamos y mostramos un mensaje emergente de que ha habido algún error extrayendo los equipos
                        Toast.makeText(Dashboard.this, "Failed to fetch teams: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Actualizamos la información del usuario en la cabecera del NavigationView
    private void actualizarInfoUsuarioHeaderDashboard() {
        //Si el String del usuario logueado es distinto a null, entramos aquí
        if (usuarioLogueado != null) {
            String correo = usuarioLogueado.getEmail(); //Creamos un String y le asignamos el correo del usuario loguedo

            //Nos situamos en la colección de los usuarios, en el documento del correo del usuario logueado
            database.collection("Usuarios").document(correo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    //Si el documento existe, entramos aquí
                    if (documentSnapshot.exists()) {
                        String nombreUsuario = documentSnapshot.getString("nombreUsuario"); //Creamos un String y recogemos el nombre del usurio que le corresponde a ese correo
                        textView_nombreLogueado.setText("¡Bienvenido " + nombreUsuario + "!"); //Mostramos en el TextView del Dashboard el nombre del usuario logueado
                        textView_correoLogueadoHeader.setText(correo); //Mostramos en el TextView del NavigationView el correo del usuario logueado
                        textView_nombreUsuarioLogueadoHeader.setText(nombreUsuario); //Mostramos en el TextView del NavigationView el nombre del usuario logueado
                    }
                }
            });

            //Si el documento no existe, entramos aquí
        } else {
            textView_nombreLogueado.setText(""); //Como no existe ningún nombre de usuario, no mostramos nada en el TextView del Dashboard
        }

    }

    //Método de Firebase que obtiene el usuario que está logueado y actualiza la información del header del Dashboard
    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            //Guardamos el usuario que está logueado en la variable
            usuarioLogueado = firebaseAuth.getCurrentUser();

            //Llamamos al método para actualizar la información del header
            actualizarInfoUsuarioHeaderDashboard();
        }
    };

    //Método para controlar el inicio de la actividad
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener); //Llamamos al listener de la autenticación
    }

    //Método para controlar el fin de la actividad
    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener); //Eliminaal listener de la autenticación
    }

    //Método que controla el click sobre el botón del retroceso de Android cuando es realizado con el NavigationView desplegado
    @Override
    public void onBackPressed() {
        //Si el NavigationView está desplegado, entramos aquí
        if (drawerLayout.isDrawerOpen(navigationView)) {
            //Cerramos el NavigationView
            drawerLayout.closeDrawer(navigationView);
        } else {
            //Movimiento de retroceso predeterminado
            super.onBackPressed();
        }
    }
}