package com.hugocg21.bemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.hugocg21.bemanager.Login.Login;

public class SplashScreen extends AppCompatActivity {
    ImageView imageView_logoSplashScreen; //Creamos el ImageView del SplashScreen
    boolean recordarSesion; //Creamos el booleano para recordar el usuario
    Animation animacionSplashScreen; //Creamos la animación del SplashScreen
    SharedPreferences sharedPreferences; //Creamos el objeto SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("datos", MODE_PRIVATE); //Inicializamos el SharedPreferences con los datos guardados del usuario
        recordarSesion = sharedPreferences.getBoolean("recordar", false); //Inicializamos el booleano con true si quiere recordar el usuario o false si no

        imageView_logoSplashScreen = findViewById(R.id.imageViewlogoSplashScreen); //Inicializamos el ImageView del logo del SplashScreen
        animacionSplashScreen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_animation); //Inicializamos la animación del SplashScreen
        imageView_logoSplashScreen.startAnimation(animacionSplashScreen); //Comenzamos la animación del SplashScreen

        //Si el booleano de recordar está en true, entramos en el if
        if (recordarSesion) {
            //Comenzamos el tiempo de espera del SplashScreen
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), Dashboard.class)); //Creamos un Intent y comenzamos la actividad del Dashboard
                    finish(); //Finalizamos la actividad del SplashScreen
                }
            }, 4000); //Ponemos 4 segundos de timer

        } else { //Si no quiere recordar el usuario
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), Login.class)); //Creamos un Intent y comenzamos la actividad del Login
                    finish(); //Finalizamos la actividad del SplashScreen
                }
            }, 4000); //Ponemos 4 segundos de timer
        }
    }
}