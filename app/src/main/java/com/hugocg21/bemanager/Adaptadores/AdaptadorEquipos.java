package com.hugocg21.bemanager.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hugocg21.bemanager.Clases.Equipo;
import com.hugocg21.bemanager.R;

import java.util.List;

public class AdaptadorEquipos extends ArrayAdapter<Equipo> {
    private Context context; //Recogemos el contexto desde el que se llama a la clase
    private int idGridView; //Variable que recoge el id del item del GridView
    private List<Equipo> listaEquipos; //Lista de tipo Equipo que guarda los equipos del usuario

    //Constructor de la clase que recibe los atributos anteriores
    public AdaptadorEquipos(Context context, int idGridView, List<Equipo> listaEquipos) {
        super(context, idGridView, listaEquipos);
        this.context = context;
        this.idGridView = idGridView;
        this.listaEquipos = listaEquipos;
    }

    //Método que obtiene la vista que representa un elemento de la lista
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView; //Variable que recibe el contexto de la vista

        //Si la variable es null
        if (view == null) {
            //Inflamos el diseño del XML a una vista en pantalla
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(idGridView, parent, false);
        }

        Equipo equipo = listaEquipos.get(position); //Creamos un objeto de tipo Equipo y guardamos un equipo de la lista de equipos, dependiendo de la posicion

        TextView textView_nombreEquipo = view.findViewById(R.id.textViewNombreEquipo); //Recogemos la referencia del TextView de la vista

        textView_nombreEquipo.setText(equipo.getNombreEquipo()); //Asigamos a ese TextView el nombre del equipo

        return view; //Devolvemos la vista
    }
}
