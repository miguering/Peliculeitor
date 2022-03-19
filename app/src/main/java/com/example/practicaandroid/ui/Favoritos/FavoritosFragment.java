package com.example.practicaandroid.ui.Favoritos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicaandroid.R;
import com.example.practicaandroid.actividades.PeliculaActivity;
import com.example.practicaandroid.actividades.SerieActivity;
import com.example.practicaandroid.ui.Peliculas.DetallePelicula;
import com.example.practicaandroid.ui.Peliculas.PeliculasAdapter;
import com.example.practicaandroid.ui.Peliculas.PeliculasService;
import com.example.practicaandroid.ui.Peliculas.Result;
import com.example.practicaandroid.ui.Series.DetalleSerie;
import com.example.practicaandroid.ui.Series.SeriesAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FavoritosFragment extends Fragment {

    static final String api_key = "07c6c816ba6f5c1ff4c9c804b4216fd8";
    RecyclerView rv_series;
    RecyclerView rv_peliculas;
    List<DetallePelicula> lista_pelis;
    List<DetalleSerie> lista_series;
    List<Integer> id_pelis;
    List<Integer> id_series;
    ProgressBar barra_carga;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favoritos, container, false);


        rv_peliculas = (RecyclerView) root.findViewById(R.id.recycler_view);
        rv_series = (RecyclerView) root.findViewById(R.id.recycler_view2);
        barra_carga = (ProgressBar) root.findViewById(R.id.progressBar);




        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        barra_carga.setVisibility(View.VISIBLE);
        lista_pelis = new ArrayList<>();
        lista_series = new ArrayList<>();

        rv_peliculas.setHasFixedSize(false);
        rv_series.setHasFixedSize(false);

        //Crea el adaptador, pasándole como parámetro los datos
        final PeliculasFavoritasAdapter adaptador_pelis = new PeliculasFavoritasAdapter(lista_pelis, new PeliculasFavoritasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DetallePelicula p) {

                int id_pelicula = p.getId();
                Intent intentDetalle = new Intent(getContext(), PeliculaActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_pelicula));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });

        //Crea el adaptador, pasándole como parámetro los datos
        final SeriesFavoritasAdapter adaptador_series = new SeriesFavoritasAdapter(lista_series, new SeriesFavoritasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DetalleSerie p) {

                int id_serie = p.getId();
                Intent intentDetalle = new Intent(getContext(), SerieActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_serie));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });


        rv_series.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rv_peliculas.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        //Pone una división entre items
        rv_series.addItemDecoration(
                new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        rv_peliculas.addItemDecoration(
                new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        rv_series.setItemAnimator(new DefaultItemAnimator());
        rv_peliculas.setItemAnimator(new DefaultItemAnimator());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculasService peli_service = retrofit.create(PeliculasService.class);

        DatabaseHandler db = new DatabaseHandler(getContext());

        id_pelis = db.getAllIDMovies();
        id_series = db.getAllIDSeries();
        db.close();

        if(id_pelis.size() == 0 && id_series.size() == 0)
            barra_carga.setVisibility(View.GONE);


        Call<DetallePelicula> pelicula;

        for(int i = 0; i < id_pelis.size(); i++){
            pelicula = peli_service.getDetallePelicula(String.valueOf(id_pelis.get(i)), api_key, getString(R.string.lang));

            pelicula.enqueue(new Callback<DetallePelicula>() {
                @Override
                public void onResponse(Call<DetallePelicula> call, Response<DetallePelicula> response) {
                    switch(response.code()) {
                        case 200:
                            adaptador_pelis.add((DetallePelicula) response.body());
                            barra_carga.setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onFailure(Call<DetallePelicula> call, Throwable t) {

                }
            });
        }





        for(int i = 0; i < id_series.size(); i++){
            Call<DetalleSerie> serie = peli_service.getDetalleSerie(String.valueOf(id_series.get(i)), api_key, getString(R.string.lang));

            serie.enqueue(new Callback<DetalleSerie>() {
                @Override
                public void onResponse(Call<DetalleSerie> call, Response<DetalleSerie> response) {
                    switch(response.code()) {
                        case 200:
                            adaptador_series.add((DetalleSerie) response.body());
                            barra_carga.setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onFailure(Call<DetalleSerie> call, Throwable t) {

                }
            });
        }

        //Asocia a recylerView el adaptador
        adaptador_pelis.swap(lista_pelis);

        //Asocia a recylerView el adaptador
        adaptador_series.swap(lista_series);

        rv_peliculas.setAdapter(adaptador_pelis);
        rv_series.setAdapter(adaptador_series);

        adaptador_pelis.notifyDataSetChanged();
        adaptador_series.notifyDataSetChanged();

        if(lista_pelis.size() > 0)
            rv_peliculas.setVisibility(View.VISIBLE);
        if(lista_series.size() > 0)
            rv_series.setVisibility(View.VISIBLE);

    }
}