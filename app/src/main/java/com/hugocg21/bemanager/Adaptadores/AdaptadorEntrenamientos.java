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
import com.hugocg21.bemanager.R;

public class AdaptadorEntrenamientos extends FirestoreRecyclerAdapter<Entrenamiento, AdaptadorEntrenamientos.ViewHolderEntrenamientos> {
    private OnItemClickListener listener; //Creamos el OnClickListener

    //Constructor de la clase AdaptadorEntrenamientos que define como es el adaptador
    public AdaptadorEntrenamientos(@NonNull FirestoreRecyclerOptions<Entrenamiento> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    //Método que crea e infla el View con el archivo XML personalizado
    @NonNull
    @Override
    public ViewHolderEntrenamientos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_entrenamiento, parent, false);
        return new ViewHolderEntrenamientos(view, listener);
    }

    //Método que obtiene y bindea los datos del entrenamiento en la vista
    @Override
    protected void onBindViewHolder(@NonNull ViewHolderEntrenamientos holder, int position, @NonNull Entrenamiento entrenamiento) {
        holder.bindData(entrenamiento);
    }

    //Método para actualizar las opciones del filtro del adaptador
    public void actualizarOpciones(FirestoreRecyclerOptions<Entrenamiento> firestoreRecyclerOptions) {
        this.updateOptions(firestoreRecyclerOptions);
        notifyDataSetChanged();
    }

    //Clase ViewHolderEntrenamientos que modifica los datos de la vista
    public class ViewHolderEntrenamientos extends RecyclerView.ViewHolder {
        ImageView imageView_eliminarEntrenamiento; //Creamos el ImageView de eliminar el entrenamiento
        TextView textView_fechaEntrenamiento, textView_horaEntrenamiento; //Creamos los TextViews de la fecha y hora del entrenamiento

        //Constructor de la clase ViewHolderEntrenamientos que define como es un objeto ViewHolderEntrenamientos
        public ViewHolderEntrenamientos(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            //Inicializamos el ImageView de la vista
            imageView_eliminarEntrenamiento = itemView.findViewById(R.id.imageViewEliminarEntrenamiento);

            //Inicializamos los TextViews de la vista
            textView_fechaEntrenamiento = itemView.findViewById(R.id.textViewFechaEntrenamiento);
            textView_horaEntrenamiento = itemView.findViewById(R.id.textViewHoraEntrenamiento);

            //Método al hacer click sobre el ImageView para eliminar el entrenamiento
            imageView_eliminarEntrenamiento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Obtenemos la posicion y referencia de la vista seleccionada y eliminamos el entrenamiento
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            //Método al hacer click sobre una vista de un entrenamiento
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

        //Método para bindear los datos del entrenamiento con la vista
        public void bindData(Entrenamiento entrenamiento) {
            //Asignamos los datos del entrenamiento a los TextViews de la vista
            textView_fechaEntrenamiento.setText(entrenamiento.getFechaEntrenamiento());
            textView_horaEntrenamiento.setText(entrenamiento.getHoraEntrenamiento());
        }
    }

    //Interfaz OnClickListener para controlar los clicks
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }
}
