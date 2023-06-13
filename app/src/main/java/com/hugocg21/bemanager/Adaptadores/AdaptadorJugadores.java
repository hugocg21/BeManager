package com.hugocg21.bemanager.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hugocg21.bemanager.Clases.Jugador;
import com.hugocg21.bemanager.R;

public class AdaptadorJugadores extends FirestoreRecyclerAdapter<Jugador, AdaptadorJugadores.ViewHolderJugadores> {
    private OnItemClickListener listener; //Creamos el OnClickListener

    //Constructor de la clase AdaptadorJugadores que define como es el adaptador
    public AdaptadorJugadores(@NonNull FirestoreRecyclerOptions<Jugador> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    //Método que crea e infla el View con el archivo XML personalizado
    @NonNull
    @Override
    public ViewHolderJugadores onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_jugador, parent, false);
        return new ViewHolderJugadores(view, listener);
    }

    //Método que obtiene y bindea los datos del jugador en la vista
    @Override
    protected void onBindViewHolder(@NonNull ViewHolderJugadores holder, int position, @NonNull Jugador jugador) {
        holder.bindData(jugador);
    }

    //Método para actualizar las opciones del filtro del adaptador
    public void actualizarOpciones(FirestoreRecyclerOptions<Jugador> firestoreRecyclerOptions) {
        this.updateOptions(firestoreRecyclerOptions);
        notifyDataSetChanged();
    }

    //Clase ViewHolderJugadores que modifica los datos de la vista
    public class ViewHolderJugadores extends RecyclerView.ViewHolder {
        ImageView imageView_eliminarJugador; //Creamos el ImageView de eliminar al jugador

        //Creamos los TextViews del dorsal, nombre completo (nombre + apellidos), equipo y posición del jugador
        TextView textView_dorsalJugador, textView_nombreCompletoJugador, textView_equipoJugador, textView_posicionJugador;

        //Constructor de la clase ViewHolderJugadores que define como es un objeto ViewHolderJugadores
        public ViewHolderJugadores(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            //Inicializamos el ImageView de la vista
            imageView_eliminarJugador = itemView.findViewById(R.id.imageViewEliminarJugador);

            //Inicializamos los TextViews de la vista
            textView_nombreCompletoJugador = itemView.findViewById(R.id.textViewNombreApellidosJugador);
            textView_equipoJugador = itemView.findViewById(R.id.textViewEquipoJugador);
            textView_posicionJugador = itemView.findViewById(R.id.textViewPosicionJugador);
            textView_dorsalJugador = itemView.findViewById(R.id.textViewDorsalJugador);

            //Método al hacer click sobre el ImageView para eliminar al jugador
            imageView_eliminarJugador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Obtenemos la posicion y referencia de la vista seleccionada y eliminamos al jugador
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }

        //Método para bindear los datos del jugador con la vista
        public void bindData(Jugador jugador) {
            //Asignamos los datos del jugador a los TextViews de la vista
            textView_nombreCompletoJugador.setText(jugador.getNombreJugador() + " " + jugador.getApellidosJugador());
            textView_equipoJugador.setText(jugador.getEquipoJugador());
            textView_posicionJugador.setText(jugador.getPosicionJugador());

            int dorsalJugador = jugador.getDorsalJugador();
            textView_dorsalJugador.setText(String.valueOf(dorsalJugador));
        }
    }

    //Interfaz OnClickListener para controlar los clicks
    public interface OnItemClickListener {
        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }

}
