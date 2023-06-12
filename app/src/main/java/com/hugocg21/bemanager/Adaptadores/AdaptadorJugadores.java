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
    private OnItemClickListener listener;

    public AdaptadorJugadores(@NonNull FirestoreRecyclerOptions<Jugador> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderJugadores onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_jugador, parent, false);
        return new ViewHolderJugadores(view, listener);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderJugadores holder, int position, @NonNull Jugador jugador) {
        holder.bindData(jugador);
    }

    public void actualizarOpciones(FirestoreRecyclerOptions<Jugador> firestoreRecyclerOptions) {
        this.updateOptions(firestoreRecyclerOptions);
        notifyDataSetChanged();
    }

    public class ViewHolderJugadores extends RecyclerView.ViewHolder {
        ImageView imageView_eliminarJugador;
        TextView textView_dorsalJugador, textView_nombreCompletoJugador, textView_equipoJugador, textView_posicionJugador;

        public ViewHolderJugadores(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            imageView_eliminarJugador = itemView.findViewById(R.id.imageViewEliminarJugador);

            textView_nombreCompletoJugador = itemView.findViewById(R.id.textViewNombreApellidosJugador);
            textView_equipoJugador = itemView.findViewById(R.id.textViewEquipoJugador);
            textView_posicionJugador = itemView.findViewById(R.id.textViewPosicionJugador);
            textView_dorsalJugador = itemView.findViewById(R.id.textViewDorsalJugador);

            imageView_eliminarJugador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }

        public void bindData(Jugador jugador) {
            textView_nombreCompletoJugador.setText(jugador.getNombreJugador() + " " + jugador.getApellidosJugador());
            textView_equipoJugador.setText(jugador.getEquipoJugador());
            textView_posicionJugador.setText(jugador.getPosicionJugador());

            int dorsalJugador = jugador.getDorsalJugador();
            textView_dorsalJugador.setText(String.valueOf(dorsalJugador));
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }

}
