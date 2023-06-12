package com.hugocg21.bemanager.Entrenamientos.Ejercicios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.hugocg21.bemanager.R;

public class EjerciciosFragment extends Fragment {
    public EjerciciosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ejercicios, container, false);
    }
}