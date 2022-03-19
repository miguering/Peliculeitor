package com.example.practicaandroid.ui.Favoritos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.practicaandroid.R;
import com.example.practicaandroid.ui.Peliculas.DetallePelicula;
import com.example.practicaandroid.ui.Series.DetalleSerie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SeriesFavoritasAdapter extends RecyclerView.Adapter<SeriesFavoritasAdapter.SeriesFavoritasAdapterViewHolder>{

    /*Arraylist donde almaceno los datos que se van a mostrar en el RecylerView*/
    private List<DetalleSerie> datos;

    private final OnItemClickListener listener;

    public void swap(List<DetalleSerie> datos1) {
        datos = datos1;
    }

    public void add(DetalleSerie s){
        datos.add(s);
        this.notifyDataSetChanged();
    }

    /* Defino un interface con el OnItemClickListener*/
    public interface OnItemClickListener {
        void onItemClick(DetalleSerie item);
    }

    /* Incluyo el Viewholder en el Adapter */
    public static class SeriesFavoritasAdapterViewHolder
            extends RecyclerView.ViewHolder {
        /* Como atributos se incluyen los elementos que van a referenciar a los elementos de la vista*/
        private TextView nombreSerie;
        private RatingBar puntuacionSerie;
        private ImageView caratula;

        /*constructor con parámetro de la vista*/
        public SeriesFavoritasAdapterViewHolder(View itemView) {
            super(itemView);
            // Asocia los atributos a los widgets del layout de la vista
            nombreSerie = (TextView)itemView.findViewById(R.id.tv_name);
            puntuacionSerie = (RatingBar)itemView.findViewById(R.id.ratingBar);
            caratula = (ImageView)itemView.findViewById(R.id.imageView);
        }

        /*Muestra los datos de jugador en el item*/
        public void bindPeliculas(DetalleSerie j, final SeriesFavoritasAdapter.OnItemClickListener listener) {
            nombreSerie.setText(j.getName());
            puntuacionSerie.setNumStars(5);
            puntuacionSerie.setStepSize((float) 0.5);
            puntuacionSerie.setRating(j.getVoteAverage().floatValue() / 2);
            Picasso.get().load("https://image.tmdb.org/t/p/w500" + j.getPosterPath()).into(caratula);



            /* Coloco el listener a la vista*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(j);
                }
            });
        }
    }


    /* Constructor con parámetros*/
    public SeriesFavoritasAdapter(List<DetalleSerie> datos, SeriesFavoritasAdapter.OnItemClickListener listener) {
        this.datos = datos;
        this.listener = listener;
    }

    @Override
    public SeriesFavoritasAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        /*Crea la vista de un item y la "pinta"*/
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_movie_item_roundedcorners, viewGroup, false);
        /* Crea un objeto de la clase CocheViewHolder, pasándole la vista anteriormente creada*/
        SeriesFavoritasAdapterViewHolder listapeliculasVH = new SeriesFavoritasAdapterViewHolder(itemView);
        /* devuelve la vissta*/
        return listapeliculasVH;
    }

    @Override
    public void onBindViewHolder(SeriesFavoritasAdapterViewHolder viewHolder, int pos) {
        DetalleSerie j = datos.get(pos);
        /* Llama a bindCoche, para que "pinte" los datos del coche que se le pasa como parámetro*/
        viewHolder.bindPeliculas(j,listener);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

}
