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
import com.hugocg21.bemanager.Clases.Entrenamiento;
import com.hugocg21.bemanager.R;

public class AdaptadorEjercicios extends FirestoreRecyclerAdapter<Ejercicio, AdaptadorEjercicios.ViewHolderEjercicios> {
    private OnItemClickListener listener;

    public AdaptadorEjercicios(@NonNull FirestoreRecyclerOptions<Ejercicio> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderEjercicios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_ejercicio, parent, false);
        return new ViewHolderEjercicios(view, listener);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderEjercicios holder, int position, @NonNull Ejercicio ejercicio) {
        holder.bindData(ejercicio);
    }

    public class ViewHolderEjercicios extends RecyclerView.ViewHolder {
        private ImageView imageView_eliminarEjercicio;
        private TextView textView_tituloEjercicio, textView_duracionEjercicio;

        public ViewHolderEjercicios(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            imageView_eliminarEjercicio = itemView.findViewById(R.id.imageViewEliminarEjercicio);

            textView_tituloEjercicio = itemView.findViewById(R.id.textViewTituloEjercicio);
            textView_duracionEjercicio = itemView.findViewById(R.id.textViewDuracionEjercicio);

            imageView_eliminarEjercicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int posicion = getAdapterPosition();
                    if (posicion != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getSnapshots().getSnapshot(posicion), posicion);
                    }
                }
            });
        }

        public void bindData(Ejercicio ejercicio) {
            textView_tituloEjercicio.setText(ejercicio.getTituloEjercicio());
            textView_duracionEjercicio.setText(String.valueOf(ejercicio.getDuracionEjercicio()) + " minutos");
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }
}
