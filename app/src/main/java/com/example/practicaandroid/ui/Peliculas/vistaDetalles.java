package com.example.practicaandroid.ui.Peliculas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.practicaandroid.R;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class vistaDetalles extends AppCompatActivity {

    RecyclerView reparto;
    RecyclerView pelis_similares;
    TextView tv_titulo;
    TextView tv_genero;
    TextView tv_lenguaje;
    TextView tv_studio;

    TextView tv_descripcion;
    TextView tv_fecha;
    RatingBar valoracion;
    ImageView portada;

    List<Result> lista_similares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vista_media);
        tv_titulo = findViewById(R.id.textView2);
        tv_descripcion = findViewById(R.id.fecha_nacimiento);
        reparto = findViewById(R.id.recycler_view2);
        pelis_similares = findViewById(R.id.recycler_viewCompanies);
        tv_studio = findViewById(R.id.biografia);
        tv_genero = findViewById(R.id.genre);
        tv_fecha = findViewById(R.id.release);
        valoracion = (RatingBar) findViewById(R.id.ratingBar2);
        portada = findViewById(R.id.imageView3);

        lista_similares = new ArrayList<>();


        String id_pelicula = getIntent().getStringExtra("id");
        String api_key = getIntent().getStringExtra("api_key");

        //Crea el adaptador, pasándole como parámetro los datos
        final PeliculasAdapter adaptador_similares = new PeliculasAdapter(lista_similares, new PeliculasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result p) {

                int id_pelicula = p.getId();
                Intent intentDetalle = new Intent(getApplicationContext(), vistaDetalles.class);
                intentDetalle.putExtra("id", String.valueOf(id_pelicula));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });

        //Asocia a recylerView el adaptador
        pelis_similares.setAdapter(adaptador_similares);

        //Fija un layout linear al recyclerview
        pelis_similares.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        //Pone una división entre items
        pelis_similares.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        // Pone la animación por defecto
        pelis_similares.setItemAnimator(new DefaultItemAnimator());


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculasService peli_service = retrofit.create(PeliculasService.class);

        Call<DetallePelicula> pelicula = peli_service.getDetallePelicula(id_pelicula, api_key, "es");

        pelicula.enqueue(new Callback<DetallePelicula>() {
            @Override
            public void onResponse(Call<DetallePelicula> call, Response<DetallePelicula> response) {
                switch(response.code()) {
                    case 200:

                        DetallePelicula pelicula_extraida = response.body();
                        tv_titulo.setText(pelicula_extraida.getTitle());
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        for(int i = 0; i < pelicula_extraida.getGenres().size(); i++) {
                            if(i == pelicula_extraida.getGenres().size() - 1)
                                tv_genero.setText(tv_genero.getText() + " " + pelicula_extraida.getGenres().get(i).getName());
                            else
                                tv_genero.setText(tv_genero.getText() + " " + pelicula_extraida.getGenres().get(i).getName() + ", ");
                        }

                        tv_descripcion.setText(pelicula_extraida.getOverview());
                        tv_fecha.setText(pelicula_extraida.getReleaseDate());
                        Picasso.get().load("https://image.tmdb.org/t/p/w500" + pelicula_extraida.getPosterPath()).into(portada);
                        portada.setVisibility(View.VISIBLE);

                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<DetallePelicula> call, Throwable t) {

            }
        });


        Call<ListaPeliculas> call_peliculas_similares = peli_service.getPeliculasSimilares(id_pelicula, api_key, "es");

        call_peliculas_similares.enqueue(new Callback<ListaPeliculas>() {
            @Override
            public void onResponse(Call<ListaPeliculas> call, Response<ListaPeliculas> response) {
                switch(response.code()) {
                    case 200:

                        ListaPeliculas lista_extraida = response.body();
                        lista_similares = lista_extraida.getResults();
                        adaptador_similares.swap(lista_similares);
                        adaptador_similares.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<ListaPeliculas> call, Throwable t) {

            }
        });


    }
}