package com.example.practicaandroid.ui.Peliculas;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.practicaandroid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RepartoAdapter extends RecyclerView.Adapter<RepartoAdapter.RepartoViewHolder>{

    /*Arraylist donde almaceno los datos que se van a mostrar en el RecylerView*/
    private List<Cast> datos;

    private final OnItemClickListener listener;

    public void swap(List<Cast> datos1) {
        datos = datos1;
    }

    /* Defino un interface con el OnItemClickListener*/
    public interface OnItemClickListener {
        void onItemClick(Cast item);
    }

    /* Incluyo el Viewholder en el Adapter */
    public static class RepartoViewHolder
            extends RecyclerView.ViewHolder {
        /* Como atributos se incluyen los elementos que van a referenciar a los elementos de la vista*/
        private TextView nombreActor;
        private TextView nombrePersonaje;
        private CircleImageView foto;

        /*constructor con parámetro de la vista*/
        public RepartoViewHolder(View itemView) {
            super(itemView);
            // Asocia los atributos a los widgets del layout de la vista
            nombreActor = (TextView)itemView.findViewById(R.id.nombre_actor);
            nombrePersonaje = (TextView)itemView.findViewById(R.id.nombre_personaje);
            foto = (CircleImageView) itemView.findViewById(R.id.foto_actor);
        }

        /*Muestra los datos de jugador en el item*/
        public void bindReparto(Cast j, final OnItemClickListener listener) {
            Log.d("Nombre actor: " , j.getName());
            Log.d("Nombre personaje: " , j.getCharacter());
            nombreActor.setText(j.getName());
            nombrePersonaje.setText(j.getCharacter().toString());
            Picasso.get().load("https://image.tmdb.org/t/p/w500" + j.getProfilePath()).into(foto);



            /* Coloco el listener a la vista*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(j);
                }
            });
        }
    }


    /* Constructor con parámetros*/
    public RepartoAdapter(List<Cast> datos, OnItemClickListener listener) {
        this.datos = datos;
        this.listener = listener;
    }

    @Override
    public RepartoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        /*Crea la vista de un item y la "pinta"*/
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.actor_movie, viewGroup, false);
        /* Crea un objeto de la clase CocheViewHolder, pasándole la vista anteriormente creada*/
        RepartoViewHolder listaactoresVH = new RepartoViewHolder(itemView);
        /* devuelve la vissta*/
        return listaactoresVH;
    }

    @Override
    public void onBindViewHolder(RepartoViewHolder viewHolder, int pos) {
        Cast j = datos.get(pos);
        /* Llama a bindCoche, para que "pinte" los datos del coche que se le pasa como parámetro*/
        viewHolder.bindReparto(j,listener);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

}
