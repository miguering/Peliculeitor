package com.example.practicaandroid.ui.Series;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.practicaandroid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ListaSeriesViewHolder>{

    /*Arraylist donde almaceno los datos que se van a mostrar en el RecylerView*/
    private List<Result> datos;

    private final OnItemClickListener listener;

    public void swap(List<Result> datos1) {
        datos = datos1;
    }

    /* Defino un interface con el OnItemClickListener*/
    public interface OnItemClickListener {
        void onItemClick(Result item);
    }

    /* Incluyo el Viewholder en el Adapter */
    public static class ListaSeriesViewHolder
            extends RecyclerView.ViewHolder {
        /* Como atributos se incluyen los elementos que van a referenciar a los elementos de la vista*/
        private TextView nombrePelicula;
        private RatingBar puntuacionSerie;
        private ImageView caratula;

        /*constructor con parámetro de la vista*/
        public ListaSeriesViewHolder(View itemView) {
            super(itemView);
            // Asocia los atributos a los widgets del layout de la vista
            nombrePelicula = (TextView)itemView.findViewById(R.id.tv_name);
            puntuacionSerie = (RatingBar)itemView.findViewById(R.id.ratingBar);
            caratula = (ImageView)itemView.findViewById(R.id.imageView);
        }

        /*Muestra los datos de jugador en el item*/
        public void bindSeries(Result j, final OnItemClickListener listener) {
            nombrePelicula.setText(j.getName());
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
    public SeriesAdapter(List<Result> datos, OnItemClickListener listener) {
        this.datos = datos;
        this.listener = listener;
    }

    @Override
    public ListaSeriesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        /*Crea la vista de un item y la "pinta"*/
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_movie_item_roundedcorners, viewGroup, false);
        /* Crea un objeto de la clase CocheViewHolder, pasándole la vista anteriormente creada*/
        ListaSeriesViewHolder listaSeriesVH = new ListaSeriesViewHolder(itemView);
        /* devuelve la vissta*/
        return listaSeriesVH;
    }

    @Override
    public void onBindViewHolder(ListaSeriesViewHolder viewHolder, int pos) {
        Result j = datos.get(pos);
        /* Llama a bindCoche, para que "pinte" los datos del coche que se le pasa como parámetro*/
        viewHolder.bindSeries(j,listener);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

}
