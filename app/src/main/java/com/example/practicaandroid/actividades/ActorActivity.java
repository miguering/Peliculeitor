package com.example.practicaandroid.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.practicaandroid.R;
import com.example.practicaandroid.ui.Peliculas.ListaPeliculas;
import com.example.practicaandroid.ui.Peliculas.PeliculasAdapter;
import com.example.practicaandroid.ui.Peliculas.Reparto;
import com.example.practicaandroid.ui.Peliculas.RepartoAdapter;
import com.example.practicaandroid.ui.Peliculas.Result;
import com.example.practicaandroid.ui.Peliculas.DetalleActor;
import com.example.practicaandroid.ui.Peliculas.PeliculasService;
import com.example.practicaandroid.ui.Series.Cast;
import com.example.practicaandroid.ui.Series.DetalleCastSerie;
import com.example.practicaandroid.ui.Series.ListaSeries;
import com.example.practicaandroid.ui.Series.SeriesActorAdapter;
import com.example.practicaandroid.ui.Series.SeriesAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActorActivity extends AppCompatActivity {

    ImageView foto;
    TextView nombre;
    TextView fecha_nacimiento;
    TextView lugar_nacimiento;
    TextView popularidad;
    TextView mote;
    TextView aka;
    TextView biografia;
    List<Result> lista_pelis;
    List<com.example.practicaandroid.ui.Series.Cast> lista_series;
    RecyclerView rv_peliculas_aparece;
    RecyclerView rv_series_aparece;
    ProgressBar barra_carga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_actor);

        foto = (ImageView) findViewById(R.id.imageView3);
        nombre = (TextView) findViewById(R.id.textView2);
        fecha_nacimiento = (TextView) findViewById(R.id.fecha_nacimiento);
        lugar_nacimiento = (TextView) findViewById(R.id.lugar_nacimiento);
        popularidad = (TextView) findViewById(R.id.popularidad);
        mote = (TextView) findViewById(R.id.mote);
        aka = (TextView) findViewById(R.id.aka);
        biografia = (TextView) findViewById(R.id.biografia);
        barra_carga = (ProgressBar) findViewById(R.id.progressLoadingActors);

        rv_peliculas_aparece = (RecyclerView) findViewById(R.id.moviesActors);
        rv_series_aparece = (RecyclerView) findViewById(R.id.seriesActors);

        barra_carga.setVisibility(View.VISIBLE);
        String id_actor = getIntent().getStringExtra("id");
        String api_key = getIntent().getStringExtra("api_key");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculasService peli_service = retrofit.create(PeliculasService.class);

        Call<DetalleActor> call_actor = peli_service.getDetalleActor(id_actor, api_key, getString(R.string.lang));

        call_actor.enqueue(new Callback<DetalleActor>() {
            @Override
            public void onResponse(Call<DetalleActor> call, Response<DetalleActor> response) {
                switch(response.code()) {

                    case 200:
                        DetalleActor actor = response.body();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("Case 200", "Antes de los set texts");
                        nombre.setText(actor.getName());
                        fecha_nacimiento.setText(actor.getBirthday());
                        lugar_nacimiento.setText(actor.getPlaceOfBirth());
                        popularidad.setText(String.valueOf(actor.getPopularity()));
                        mote.setText(actor.getKnownForDepartment());

                        for(int i = 0; i < actor.getAlsoKnownAs().size(); i++) {
                            if(i == actor.getAlsoKnownAs().size() - 1)
                                aka.setText(aka.getText() + " " + actor.getAlsoKnownAs().get(i));
                            else
                                aka.setText(aka.getText() + " " + actor.getAlsoKnownAs().get(i) + ", ");
                        }
                        biografia.setText(actor.getBiography());
                        Picasso.get().load("https://image.tmdb.org/t/p/w500" + actor.getProfilePath()).into(foto);


                        break;
                    case 401:
                        Log.d("401: ", "Código: " + response.code());

                        break;
                    default:
                        Log.d("Default: ", "Código: " + response.code());
                        break;
                }
            }

            @Override
            public void onFailure(Call<DetalleActor> call, Throwable t) {

            }
        });

        final PeliculasAdapter adaptador_pelis = new PeliculasAdapter(lista_pelis, new PeliculasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result p) {

                int id_pelicula = p.getId();
                Intent intentDetalle = new Intent(getApplicationContext(), PeliculaActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_pelicula));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });

        final SeriesActorAdapter adaptador_series = new SeriesActorAdapter(lista_series, new SeriesActorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cast p) {

                int id_serie = p.getId();
                Intent intentDetalle = new Intent(getApplicationContext(), SerieActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_serie));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });



        //Fija un layout linear al recyclerview
        rv_peliculas_aparece.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        rv_series_aparece.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        //Pone una división entre items
        rv_peliculas_aparece.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        rv_series_aparece.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        // Pone la animación por defecto
        rv_peliculas_aparece.setItemAnimator(new DefaultItemAnimator());
        rv_series_aparece.setItemAnimator(new DefaultItemAnimator());


        Call<ListaPeliculas> call_peliculas = peli_service.getPeliculasActor(api_key, getString(R.string.lang), id_actor);

        call_peliculas.enqueue(new Callback<ListaPeliculas>() {
            @Override
            public void onResponse(Call<ListaPeliculas> call, Response<ListaPeliculas> response) {
                switch(response.code()) {
                    case 200:

                        ListaPeliculas lista_extraida = response.body();
                        lista_pelis = lista_extraida.getResults();
                        //Asocia a recylerView el adaptador
                        adaptador_pelis.swap(lista_pelis);
                        adaptador_pelis.notifyDataSetChanged();
                        rv_peliculas_aparece.setAdapter(adaptador_pelis);

                        break;
                    case 401:
                        Log.d("Case 401", "Me salgo por 401, codigo: " + response.code());
                        break;
                    default:
                        Log.d("Default", "Me salgo por default, codigo: " + response.code() + " id actor: " + id_actor);
                        break;
                }
            }

            @Override
            public void onFailure(Call<ListaPeliculas> call, Throwable t) {

            }
        });

        Call<DetalleCastSerie> call_series = peli_service.getSeriesActor(id_actor, api_key, getString(R.string.lang));
        Log.d("call:" , call_series.toString());
        Log.d("Url: " , call_series.request().url().toString());
        Log.d("Url2: " , call_series.request().url().query());

        call_series.enqueue(new Callback<DetalleCastSerie>() {
            @Override
            public void onResponse(Call<DetalleCastSerie> call, Response<DetalleCastSerie> response) {
                switch(response.code()) {
                    case 200:

                        DetalleCastSerie lista_extraida = response.body();
                        lista_series = lista_extraida.getCast();
                        //Asocia a recylerView el adaptador
                        adaptador_series.swap(lista_series);
                        adaptador_series.notifyDataSetChanged();
                        rv_series_aparece.setAdapter(adaptador_series);
                        barra_carga.setVisibility(View.GONE);

                        break;
                    case 401:
                        Log.d("Case 401", "Me salgo por 401, codigo: " + response.code());
                        break;
                    default:
                        Log.d("Default", "Me salgo por default, codigo: " + response.code() + " id actor: " + id_actor);
                        break;
                }
            }

            @Override
            public void onFailure(Call<DetalleCastSerie> call, Throwable t) {

            }
        });


    }
}
