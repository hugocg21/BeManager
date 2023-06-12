package com.hugocg21.bemanager.Login;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hugocg21.bemanager.Dashboard;
import com.hugocg21.bemanager.R;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {
    //Creamos los EditText de correo, usuario, contraseña y repetir contraseña del registro
    EditText editText_correoRegistro, editText_usuarioRegistro, editText_contrasenaRegistro, editText_repetirContrasenaRegistro;
    Button button_registro, button_volver; //Creamos los botones de registro y volver del registro

    //Creamos los ImageButtons de ver y ocultar las contraseñas del registro
    ImageButton imageButton_verContrasena, imageButton_ocultarContrasena, imageButton_verRepetirContrasena, imageButton_ocultarRepetirContrasena;
    FirebaseFirestore database; //Creamos la variable de la base de datos
    FirebaseAuth auth; //Creamos la variable de autenticación
    ProgressDialog progressDialog; //Creamos el ProgressDialog para la espera de inicio de sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Obtenemos la instancia de la base de datos y de la autenticación de Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Inicializamos los EditTexts
        editText_correoRegistro = findViewById(R.id.editTextCorreoRegistro);
        editText_usuarioRegistro = findViewById(R.id.editTextUsuarioRegistro);
        editText_contrasenaRegistro = findViewById(R.id.editTextPasswordRegistro);
        editText_repetirContrasenaRegistro = findViewById(R.id.editTextRepetirPasswordRegistro);

        //Inicializamos los Buttons
        button_registro = findViewById(R.id.buttonRegistro);
        button_volver = findViewById(R.id.buttonVolverRegistro);

        //Inicializamos los ImageButtons
        imageButton_verContrasena = findViewById(R.id.imageButtonVerPasswordRegistro);
        imageButton_ocultarContrasena = findViewById(R.id.imageButtonOcultarPasswordRegistro);
        imageButton_verRepetirContrasena = findViewById(R.id.imageButtonVerRepetirPasswordRegistro);
        imageButton_ocultarRepetirContrasena = findViewById(R.id.imageButtonOcultarRepetirContrasenaRegistro);

        //Inicializamos y creamos el formato del ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creando tu cuenta en CourtCoach");
        progressDialog.setMessage("Tu cuenta se está creando");

        //Método al hacer click en el ImageButton de ver la contraseña
        imageButton_verContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambiamos el tipo del EditText a contraseña visible
                editText_contrasenaRegistro.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                //Ocultamos el ImageButton de ver la contraseña y mostramos el ImageButton de ocultar la contraseña
                imageButton_verContrasena.setVisibility(View.GONE);
                imageButton_ocultarContrasena.setVisibility(View.VISIBLE);
            }
        });

        //Método al hacer click en el ImageButton de ocultar la contraseña
        imageButton_ocultarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambiamos el tipo del EditText a contraseña no visible
                editText_contrasenaRegistro.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                //Ocultamos el ImageButton de ocultar la contraseña y mostramos el ImageButton de ver la contraseña
                imageButton_ocultarContrasena.setVisibility(View.GONE);
                imageButton_verContrasena.setVisibility(View.VISIBLE);
            }
        });

        //Método al hacer click en el ImageButton de ver la contraseña repetida
        imageButton_verRepetirContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambiamos el tipo del EditText a contraseña visible
                editText_repetirContrasenaRegistro.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                //Ocultamos el ImageButton de ver la contraseña repetida y mostramos el ImageButton de ocultar la contraseña repetida
                imageButton_verRepetirContrasena.setVisibility(View.GONE);
                imageButton_ocultarRepetirContrasena.setVisibility(View.VISIBLE);
            }
        });

        //Método al hacer click en el ImageButton de ocultar la contraseña repetida
        imageButton_ocultarRepetirContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cambiamos el tipo del EditText a contraseña no visible
                editText_repetirContrasenaRegistro.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                //Ocultamos el ImageButton de ocultar la contraseña repetida y mostramos el ImageButton de ver la contraseña repetida
                imageButton_ocultarRepetirContrasena.setVisibility(View.GONE);
                imageButton_verRepetirContrasena.setVisibility(View.VISIBLE);
            }
        });

        //Método al hacer click en el botón de registro
        button_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llamamos al método para registrarse
                registrarse();
            }
        });

        //Método al hacer click en el botón de volver a la actividad de Login
        button_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Terminamos la actividad
                finish();
            }
        });
    }

    //Método para registrar un usuario
    public void registrarse(){
        //Creamos los Strings de correo, usuario, contraseña y contraseña repetida y les asignamos los valores de sus EditTexts respectivos
        String correo = editText_correoRegistro.getText().toString();
        String usuario = editText_usuarioRegistro.getText().toString();
        String password = editText_contrasenaRegistro.getText().toString();
        String repetirPassword = editText_repetirContrasenaRegistro.getText().toString();

        //Si algún campo está en blanco, entramos en el if
        if (correo.isEmpty() && usuario.isEmpty() && password.isEmpty() && repetirPassword.isEmpty()) {
            //Creamos y mostamos un mensaje emergente de que no puede haber campos en blanco
            Toast.makeText(getApplicationContext(), "No puede haber campos en blanco", Toast.LENGTH_SHORT).show();
        } else {
            //Si todos los campos están rellenados, y algún campo tiene un espacio en blanco, entramos en el if
            if (correo.contains(" ") || usuario.contains(" ") || password.contains(" ") || repetirPassword.contains(" ")) {
                //Creamos y mostramos un mensaje emergente de que ningún campo puede tener un espacio en blanco
                Toast.makeText(getApplicationContext(), "Ningún campo puede contener espacios en blanco", Toast.LENGTH_SHORT).show();
            } else {
                //Si todos los campos están rellenados y ninguno tiene espacios en blanco, y las dos contraseñas coinciden, entramos en el if
                if (password.equals(repetirPassword)) {
                    //Mostramos el ProgressDialog de crear cuenta
                    progressDialog.show();

                    //Método que crea el usuario
                    auth.createUserWithEmailAndPassword(correo, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Cerramos el ProgressDialog
                            progressDialog.dismiss();

                            //Si la tarea se ha realizado de manera correcta, entramos en el if
                            if (task.isSuccessful()) {
                                //Creamos un HashMap y guardamos los atributos del usuario(correo, usuario y contraseña)
                                Map<String, Object> Usuario = new HashMap<>();
                                Usuario.put("correoElectronico", correo);
                                Usuario.put("nombreUsuario", usuario);
                                Usuario.put("password", password);

                                //Creamos la colección "Usuarios", con el correo como identificador del documento, y el HashMap como datos
                                database.collection("Usuarios").document(correo).set(Usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Mostramos por consola el ID del usuario creado
                                        String id = database.collection("Usuarios").document(correo).getId();
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + id);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Mostramos por consola el error que da
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });

                                //Creamos y mostramos un mensaje emergente de que la cuenta se ha creado correctamente
                                Toast.makeText(Registro.this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();

                                //Creamos un Intent y abrimos la actividad del Dashboard
                                Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                startActivity(i);
                            }
                        }
                    }).addOnFailureListener(e -> {
                        try {
                            try {
                                throw e;
                            } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                //Creamos y mostramos un mensaje emergente de que la contraseña es demasido débil
                                Toast.makeText(getApplicationContext(), "La contraseña es demasiado débil", Toast.LENGTH_LONG).show();
                            } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                                //Creamos y mostramos un mensaje emergente de que el correo no tiene el formato correcto
                                Toast.makeText(getApplicationContext(), "El correo electrónico no tiene el formato correcto", Toast.LENGTH_LONG).show();
                            } catch (FirebaseAuthUserCollisionException emailExists) {
                                //Creamos y mostramos un mensaje emergente de que el usuario ya existe
                                Toast.makeText(getApplicationContext(), "El correo electrónico ya está registrado", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            //Creamos y mostramos un mensaje emergente de que ha habido un error al crear la cuenta
                            Toast.makeText(getApplicationContext(), "Error al crear usuario: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
    }
}