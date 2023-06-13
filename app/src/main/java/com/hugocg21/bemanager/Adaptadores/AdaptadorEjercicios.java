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
import com.hugocg21.bemanager.Clases.Ejercicio;
import com.hugocg21.bemanager.R;

public class AdaptadorEjercicios extends FirestoreRecyclerAdapter<Ejercicio, AdaptadorEjercicios.ViewHolderEjercicios> {
    private OnItemClickListener listener; //Creamos el OnClickListener

    //Constructor de la clase Adaptadores que define como es el adaptador
    public AdaptadorEjercicios(@NonNull FirestoreRecyclerOptions<Ejercicio> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    //Método que crea e infla el View con el archivo XML personalizado
    @NonNull
    @Override
    public ViewHolderEjercicios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_ejercicio, parent, false);
        return new ViewHolderEjercicios(view, listener);
    }

    //Método que obtiene y bindea los datos del jugador en la vista
    @Override
    protected void onBindViewHolder(@NonNull ViewHolderEjercicios holder, int position, @NonNull Ejercicio ejercicio) {
        holder.bindData(ejercicio);
    }

    //Clase ViewHolderEjercicios que modifica los datos de la vista
    public class ViewHolderEjercicios extends RecyclerView.ViewHolder {
        private ImageView imageView_eliminarEjercicio; //Creamos el ImageView de eliminar el ejercicio
        private TextView textView_tituloEjercicio, textView_duracionEjercicio; //Creamos los TextViews del título y duración del ejercicio

        //Constructor de la clase ViewHolderEjercicios que define como es un objeto ViewHolderEjercicios
        public ViewHolderEjercicios(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            //Inicializamos el ImageView de la vista
            imageView_eliminarEjercicio = itemView.findViewById(R.id.imageViewEliminarEjercicio);

            //Inicializamos los TextViews de la vista
            textView_tituloEjercicio = itemView.findViewById(R.id.textViewTituloEjercicio);
            textView_duracionEjercicio = itemView.findViewById(R.id.textViewDuracionEjercicio);

            //Método al hacer click sobre el ImageView para eliminar el ejercicio
            imageView_eliminarEjercicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Obtenemos la posicion y referencia de la vista seleccionada y eliminamos el ejercicio
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            //Método al hacer click sobre una vista de un ejercicio
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Obtenemos la posicion y referencia de la vista seleccionada
                    int posicion = getAdapterPosition();
                    if (posicion != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getSnapshots().getSnapshot(posicion), posicion);
                    }
                }
            });
        }

        //Método para bindear los datos del ejercicio con la vista
        public void bindData(Ejercicio ejercicio) {
            //Asignamos los datos del ejercicio a los TextViews de la vista
            textView_tituloEjercicio.setText(ejercicio.getTituloEjercicio());
            textView_duracionEjercicio.setText(String.valueOf(ejercicio.getDuracionEjercicio()) + " minutos");
        }
    }

    //Interfaz OnClickListener para controlar los clicks
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }
}
