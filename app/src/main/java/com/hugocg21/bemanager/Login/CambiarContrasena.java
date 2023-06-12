package com.hugocg21.bemanager.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.hugocg21.bemanager.R;

public class CambiarContrasena extends AppCompatActivity {
    Button button_enviarCodigo, button_volver; //Buttons para enviar el código de reseteo de la contraseña y para volver a la actividad anterior
    EditText editText_correo; //EditText donde pedimos el correo del usuario
    FirebaseAuth auth; //Variable para autenticar al usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);

        //Inicializamos el EditText
        editText_correo = findViewById(R.id.editTextCorreoRecuperarPassword);

        //Inicializamos los Buttons
        button_enviarCodigo = findViewById(R.id.buttonRecuperarPassword);
        button_volver = findViewById(R.id.buttonVolverRecuperarPassword);

        //Obtenemos la instancia de la autenticación de Firebase
        auth = FirebaseAuth.getInstance();

        //Método al hacer click en el botón de enviar el código
        button_enviarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un String para guardar el correo escrito en el EditText
                String correoRecuperar = editText_correo.getText().toString();

                //Método que envia un correo para recuperar la contraseña del correo especificado
                auth.sendPasswordResetEmail(correoRecuperar).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Si la tarea ha sido realizada correctamente, entra en el bucle
                        if (task.isSuccessful()) {
                            //Creamos y mostramos un mensaje emergente de que el correo ha sido enviado correctamente
                            Toast.makeText(getApplicationContext(), "Correo enviado correctamente a " + correoRecuperar, Toast.LENGTH_SHORT).show();

                            //Volvemos a la pantalla del Login
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        } else {
                            //Si la tarea no se pudo realizar, creamos y mostramos un mensaje emergente indicando que ha habido un error al enviar el correo
                            Toast.makeText(getApplicationContext(), "Ha habido un problema enviando el correo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //Método para volver al Login
        button_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cerramos el activity de cambiar la contraseña
                finish();
            }
        });
    }
}