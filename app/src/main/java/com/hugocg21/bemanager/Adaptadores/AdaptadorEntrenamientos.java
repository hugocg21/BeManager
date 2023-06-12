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
import com.hugocg21.bemanager.Clases.Entrenamiento;
import com.hugocg21.bemanager.Clases.Jugador;
import com.hugocg21.bemanager.R;

public class AdaptadorEntrenamientos extends FirestoreRecyclerAdapter<Entrenamiento, AdaptadorEntrenamientos.ViewHolderEntrenamientos> {
    private OnItemClickListener listener;

    public AdaptadorEntrenamientos(@NonNull FirestoreRecyclerOptions<Entrenamiento> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderEntrenamientos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_entrenamiento, parent, false);
        return new ViewHolderEntrenamientos(view, listener);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderEntrenamientos holder, int position, @NonNull Entrenamiento entrenamiento) {
        holder.bindData(entrenamiento);
    }

    public void actualizarOpciones(FirestoreRecyclerOptions<Entrenamiento> firestoreRecyclerOptions) {
        this.updateOptions(firestoreRecyclerOptions);
        notifyDataSetChanged();
    }

    public class ViewHolderEntrenamientos extends RecyclerView.ViewHolder {
        ImageView imageView_eliminarEntrenamiento;
        TextView textView_fechaEntrenamiento, textView_horaEntrenamiento;

        public ViewHolderEntrenamientos(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            imageView_eliminarEntrenamiento = itemView.findViewById(R.id.imageViewEliminarEntrenamiento);

            textView_fechaEntrenamiento = itemView.findViewById(R.id.textViewFechaEntrenamiento);
            textView_horaEntrenamiento = itemView.findViewById(R.id.textViewHoraEntrenamiento);

            imageView_eliminarEntrenamiento.setOnClickListener(new View.OnClickListener() {
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

        public void bindData(Entrenamiento entrenamiento) {
            textView_fechaEntrenamiento.setText(entrenamiento.getFechaEntrenamiento());
            textView_horaEntrenamiento.setText(entrenamiento.getHoraEntrenamiento());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }
}
