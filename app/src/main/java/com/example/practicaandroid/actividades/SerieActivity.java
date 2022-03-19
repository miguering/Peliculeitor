package com.example.practicaandroid.actividades;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicaandroid.R;
import com.example.practicaandroid.ui.Favoritos.DatabaseHandler;
import com.example.practicaandroid.ui.Peliculas.Cast;
import com.example.practicaandroid.ui.Peliculas.Reparto;
import com.example.practicaandroid.ui.Peliculas.RepartoAdapter;
import com.example.practicaandroid.ui.Series.DetalleSerie;
import com.example.practicaandroid.ui.Peliculas.PeliculasAdapter;
import com.example.practicaandroid.ui.Peliculas.PeliculasService;
import com.example.practicaandroid.ui.Series.ListaSeries;
import com.example.practicaandroid.ui.Series.Result;
import com.example.practicaandroid.ui.Series.DetalleSerie;
import com.example.practicaandroid.ui.Series.SeriesAdapter;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SerieActivity extends AppCompatActivity {

    TextView tv_titulo;
    TextView tv_temporadas;
    TextView tv_episodios;
    TextView tv_estado;
    TextView tv_estudio;
    TextView tv_genero;
    TextView tv_lanzamiento;
    TextView tv_descripcion;
    ImageView foto;
    RecyclerView series_similares;
    RecyclerView reparto;
    RatingBar valoracion;
    ProgressBar progressLoading;
    Button boton_atras;
    Button trailer;
    MaterialFavoriteButton favorito;
    String url;
    List<Result> lista_similares;
    List<Cast> lista_reparto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_media_serie);



        tv_titulo = (TextView) findViewById(R.id.textView2);
        tv_temporadas = (TextView) findViewById(R.id.textView25);
        tv_episodios = (TextView) findViewById(R.id.textView27);
        tv_estado = (TextView) findViewById(R.id.textView29);
        tv_estudio = (TextView) findViewById(R.id.textView9);
        tv_genero = (TextView) findViewById(R.id.genre);
        tv_descripcion = (TextView) findViewById(R.id.textView8);
        tv_lanzamiento = (TextView) findViewById(R.id.release);
        foto = (ImageView) findViewById(R.id.imageView3);
        boton_atras = (Button) findViewById(R.id.buttonBack);
        trailer = (Button) findViewById(R.id.button);
        series_similares = (RecyclerView) findViewById(R.id.recycler_viewCompanies);
        reparto = (RecyclerView) findViewById(R.id.recycler_viewActores);
        valoracion = (RatingBar) findViewById(R.id.ratingBar2);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoading);
        favorito = findViewById(R.id.checkBox2);

        lista_similares = new ArrayList<>();
        lista_reparto = new ArrayList<>();

        progressLoading.setVisibility(View.VISIBLE);

        reparto.setHasFixedSize(false);
        series_similares.setHasFixedSize(false);


        String id_serie = getIntent().getStringExtra("id");
        String api_key = getIntent().getStringExtra("api_key");

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        if(db.esSerieFavorita(Integer.valueOf(id_serie))){
            favorito.setFavorite(true);
        }
        else{
            favorito.setFavorite(false);
        }
        db.close();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculasService peli_service = retrofit.create(PeliculasService.class);

        Call<DetalleSerie> call_serie = peli_service.getDetalleSerie(id_serie, api_key, getString(R.string.lang));

        call_serie.enqueue(new Callback<DetalleSerie>() {
            @Override
            public void onResponse(Call<DetalleSerie> call, Response<DetalleSerie> response) {
                switch(response.code()) {
                    case 200:

                        DetalleSerie serie_extraida = response.body();
                        tv_titulo.setText(serie_extraida.getName());
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        tv_genero.setText("");
                        for(int i = 0; i < serie_extraida.getGenres().size(); i++) {
                            if(i == serie_extraida.getGenres().size() - 1)
                                tv_genero.setText(tv_genero.getText() + " " + serie_extraida.getGenres().get(i).getName());
                            else
                                tv_genero.setText(tv_genero.getText() + " " + serie_extraida.getGenres().get(i).getName() + ", ");
                        }

                        tv_estudio.setText("");

                        for(int i = 0; i < serie_extraida.getProductionCompanies().size(); i++) {
                            if(i == serie_extraida.getProductionCompanies().size() - 1)
                                tv_estudio.setText(tv_estudio.getText() + " " + serie_extraida.getProductionCompanies().get(i).getName());
                            else
                                tv_estudio.setText(tv_estudio.getText() + " " + serie_extraida.getProductionCompanies().get(i).getName() + ", ");
                        }

                        valoracion.setNumStars(5);
                        valoracion.setStepSize((float) 0.5);
                        valoracion.setRating(serie_extraida.getVoteAverage().floatValue() / 2);
                        Picasso.get().load("https://image.tmdb.org/t/p/w500" + serie_extraida.getPosterPath()).into(foto);
                        url = serie_extraida.getHomepage();

                        tv_episodios.setText(String.valueOf(serie_extraida.getNumberOfEpisodes()));
                        tv_temporadas.setText(String.valueOf(serie_extraida.getNumberOfSeasons()));

                        tv_estado.setText(serie_extraida.getStatus());
                        tv_descripcion.setText(serie_extraida.getOverview());

                        tv_lanzamiento.setText(serie_extraida.getFirstAirDate());

                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<DetalleSerie> call, Throwable t) {

            }
        });

        //Crea el adaptador, pasándole como parámetro los datos
        final SeriesAdapter adaptador_similares= new SeriesAdapter(lista_similares, new SeriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result p) {

                int id_serie = p.getId();
                Intent intentDetalle = new Intent(getApplicationContext(), SerieActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_serie));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });

        //Asocia a recylerView el adaptador
        series_similares.setAdapter(adaptador_similares);

        //Fija un layout linear al recyclerview
        series_similares.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        //Pone una división entre items
        series_similares.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        // Pone la animación por defecto
        series_similares.setItemAnimator(new DefaultItemAnimator());

        Call<ListaSeries> call_series_similares = peli_service.getSeriesSimilares(id_serie, api_key, getString(R.string.lang));

        call_series_similares.enqueue(new Callback<ListaSeries>() {
            @Override
            public void onResponse(Call<ListaSeries> call, Response<ListaSeries> response) {
                switch(response.code()) {
                    case 200:

                        ListaSeries lista_extraida = response.body();
                        lista_similares = lista_extraida.getResults();
                        adaptador_similares.swap(lista_similares);
                        adaptador_similares.notifyDataSetChanged();
                        progressLoading.setVisibility(View.GONE);

                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<ListaSeries> call, Throwable t) {

            }
        });


        //Crea el adaptador, pasándole como parámetro los datos
        final RepartoAdapter adaptador_actores = new RepartoAdapter(lista_reparto, new RepartoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cast p) {

                int id_actor = p.getId();
                Intent intentDetalle = new Intent(getApplicationContext(), ActorActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_actor));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });

        //Asocia a recylerView el adaptador
        reparto.setAdapter(adaptador_actores);

        //Fija un layout linear al recyclerview
        reparto.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        //Pone una división entre items
        reparto.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        // Pone la animación por defecto
        reparto.setItemAnimator(new DefaultItemAnimator());

        Call<Reparto> call_reparto = peli_service.getRepartoSerie(id_serie, api_key, getString(R.string.lang));

        call_reparto.enqueue(new Callback<Reparto>() {
            @Override
            public void onResponse(Call<Reparto> call, Response<Reparto> response) {
                switch(response.code()) {
                    case 200:

                        Reparto reparto_extraido = response.body();
                        lista_reparto = reparto_extraido.getCast();
                        adaptador_actores.swap(lista_reparto);
                        adaptador_actores.notifyDataSetChanged();


                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<Reparto> call, Throwable t) {

            }
        });

        trailer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(url.length() < 6){
                    Toast toast=Toast.makeText(getApplicationContext(),getString(R.string.url_error),Toast. LENGTH_SHORT);
                    toast. show();
                }else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });

        boton_atras.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        favorito.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {

                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        if(favorite == true){
                            db.agregarSerie(Integer.valueOf(id_serie));
                        }else{
                            db.deleteSerie(Integer.valueOf(id_serie));
                        }
                        db.close();
                    }
                });
    }

}
