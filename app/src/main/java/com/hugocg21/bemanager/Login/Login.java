package com.hugocg21.bemanager.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.hugocg21.bemanager.Dashboard;
import com.hugocg21.bemanager.R;

public class Login extends AppCompatActivity {
    private TextView textView_recuperarContrasena; //Creamos el TextView de recuperar la contraseña
    private EditText editText_correoLogin, editText_contrasenaLogin; //Creamos los EditTexts para introducir las credenciales de acceso
    private Button button_login, button_crearCuenta; //Creamos los Buttons de loguearse y crear cuenta
    private ImageButton imageButton_verContrasena, imageButton_ocultarContrasena; //Creamos la ImageButtons para ver y ocultar la contraseña
    private CheckBox checkBox_recordarSesion; //Creamos el CheckBox para mantener la sesión iniciada
    private FirebaseAuth auth; //Creamos la variable de autenticación
    private ProgressDialog progressDialog; //Creamos el ProgressDialog para la espera de inicio de sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializamos el TextView
        textView_recuperarContrasena = findViewById(R.id.textViewPasswordOlvidadaLogin);

        //Inicializamos los EditTexts
        editText_correoLogin = findViewById(R.id.editTextCorreoLogin);
        editText_contrasenaLogin = findViewById(R.id.editTextPasswordLogin);

        //Inicializamos los ImageButtons
        imageButton_verContrasena = findViewById(R.id.imageButtonVerPasswordLogin);
        imageButton_ocultarContrasena = findViewById(R.id.imageButtonOcultarPasswordLogin);

        //Inicializamos el CheckBox
        checkBox_recordarSesion = findViewById(R.id.checkBoxRecordarSesionIniciadaLogin);

        //Inicializamos los Buttons
        button_login = findViewById(R.id.buttonLogin);
        button_crearCuenta = findViewById(R.id.buttonCrearCuentaLogin);

        //Obtenemos la instancia de la autenticación de Firebase
        auth = FirebaseAuth.getInstance();

        //Inicializamos y creamos el formato del ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Iniciando sesión en CourtCoach");
        progressDialog.setMessage("Se están cargando tus datos");

        //Método al hacer click en el botón de Login
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Llamamos al método de iniciar sesión
                iniciarSesion();
            }
        });

        //Método al hacer click en el botón de crear la cuenta
        button_crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrimos la actividad de registrarse
                startActivity(new Intent(getApplicationContext(), Registro.class));
            }
        });

        //Método al hacer click en el ImageButton de ver la contraseña
        imageButton_verContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cambiamos el tipo del EditText a contraseña visible
                editText_contrasenaLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

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
                editText_contrasenaLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                //Ocultamos el ImageButton de ocultar la contraseña y mostramos el ImageButton de ver la contraseña
                imageButton_ocultarContrasena.setVisibility(View.GONE);
                imageButton_verContrasena.setVisibility(View.VISIBLE);
            }
        });

        //Método al hacer click en el CheckBox de recordar la sesión iniciada
        checkBox_recordarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un String y guardamos el correo electrónico
                String correoUsuarioLogin = editText_correoLogin.getText().toString();

                //Creamos un archivo de SharedPreferences para almacenar los datos del usuario
                SharedPreferences.Editor editor = getSharedPreferences("datos", MODE_PRIVATE).edit();

                //Si el CheckBox esta checkeao, entra en el if
                if (checkBox_recordarSesion.isChecked()) {
                    //Guardamos un booleano true para recordar la sesión y el String del correo
                    editor.putBoolean("recordar", true);
                    editor.putString("usuario", correoUsuarioLogin);
                    editor.apply();
                } else {
                    //Guardamos un booleano false para no recordar la sesión
                    editor.putBoolean("recordar", false);
                    editor.apply();
                }
            }
        });

        //Método al hacer click en el TextView de recuperar la contraseña
        textView_recuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos el Intent para abrir la actividad de recuperar la contraseña
                Intent i = new Intent(getApplicationContext(), CambiarContrasena.class);
                startActivity(i);
            }
        });
    }

    //Método para iniciar sesión en la aplicación
    private void iniciarSesion() {
        //Creamos los Strings para almacenar el correo y la contraseña introducidos los EditTexts
        String correo = editText_correoLogin.getText().toString();
        String password = editText_contrasenaLogin.getText().toString();

        //Si uno de los dos EditTexts de el correo y de la contraseña están vacios, entramos en el if
        if (correo.isEmpty() || password.isEmpty()) {
            //Creamos y mostramos un mensaje emergente indicando que no puede haber ningún campo en blanco para poder loguearse
            Toast.makeText(this, "No puede haber campos en blanco", Toast.LENGTH_SHORT).show();
        } else {
            //Si no hay ningún campo vacio, mostramos el ProgeressDialog
            progressDialog.show();

            //Método para verificar si el correo y la contraseña están registrados en la base de datos
            auth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //Cerramos en ProgressDialog
                    progressDialog.dismiss();

                    //Si la tarea ha sido realizada de manera correcta, entramos en el if
                    if (task.isSuccessful()) {
                        //Creamos y mostramos un mensaje emergente de que el inicio de sesión ha sido correcto
                        Toast.makeText(getApplicationContext(), "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();

                        //Creamos un Intent y abrimos la actividad del Dashboard
                        Intent i = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(i);
                    }
                }
            }).addOnFailureListener(e -> { //Si la tarea no ha sido realizada de manera correcta, entramos aquí
                try {
                    try {
                        throw e;
                    } catch (FirebaseAuthInvalidUserException invalidEmail) {
                        //Reseteamos ambos TextViews y creamos y mostramos un mensaje emergente de que el correo no está registrado
                        editText_correoLogin.setText("");
                        editText_contrasenaLogin.setText("");
                        Toast.makeText(this, "El correo electrónico no está registrado en Firebase", Toast.LENGTH_LONG).show();
                    } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                        //Reseteamos el EditText de la contraseña y creamos y mostramos un mensaje emergente de que la contraseña es incorrecta
                        editText_contrasenaLogin.setText("");
                        Toast.makeText(this, "La contraseña es incorrecta", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    //Creamos y mostramos un mensaje emergente de que ha habido un error al iniciar sesión
                    Toast.makeText(this, "Error al iniciar sesión: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}