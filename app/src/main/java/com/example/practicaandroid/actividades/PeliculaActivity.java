package com.example.practicaandroid.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practicaandroid.R;

import com.example.practicaandroid.ui.Favoritos.DatabaseHandler;
import com.example.practicaandroid.ui.Peliculas.Cast;
import com.example.practicaandroid.ui.Peliculas.DetallePelicula;
import com.example.practicaandroid.ui.Peliculas.ListaPeliculas;
import com.example.practicaandroid.ui.Peliculas.PeliculasAdapter;
import com.example.practicaandroid.ui.Peliculas.PeliculasService;
import com.example.practicaandroid.ui.Peliculas.Reparto;
import com.example.practicaandroid.ui.Peliculas.RepartoAdapter;
import com.example.practicaandroid.ui.Peliculas.Result;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PeliculaActivity extends AppCompatActivity {

    RecyclerView reparto;
    RecyclerView pelis_similares;
    TextView tv_titulo;
    TextView tv_genero;
    TextView tv_lenguaje;
    TextView tv_studio;
    Button trailer;
    TextView tv_descripcion;
    TextView tv_fecha;
    RatingBar valoracion;
    ImageView portada;
    MaterialFavoriteButton favorito;
    String url;
    Button button_back;
    List<Result> lista_similares;
    List<Cast> lista_actores;
    ProgressBar carga;

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
        trailer = findViewById(R.id.button);
        button_back = findViewById(R.id.buttonBack);
        favorito = (MaterialFavoriteButton) findViewById(R.id.checkBox2);
        lista_similares = new ArrayList<>();
        lista_actores = new ArrayList<>();
        carga = (ProgressBar) findViewById(R.id.progressLoadingMovies);
        carga.setVisibility(View.VISIBLE);
        carga.setIndeterminate(true);

        reparto.setHasFixedSize(false);
        pelis_similares.setHasFixedSize(false);

        String id_pelicula = getIntent().getStringExtra("id");
        String api_key = getIntent().getStringExtra("api_key");


        favorito.setFavorite(false);

        DatabaseHandler db = new DatabaseHandler(this);
        if(db.esPeliculaFavorita(Integer.valueOf(id_pelicula)) == true){
            favorito.setFavorite(true);
        }
        else{
            favorito.setFavorite(false);
        }
        db.close();



        //Crea el adaptador, pasándole como parámetro los datos
        final PeliculasAdapter adaptador_similares = new PeliculasAdapter(lista_similares, new PeliculasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result p) {

                int id_pelicula = p.getId();
                Intent intentDetalle = new Intent(getApplicationContext(), PeliculaActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_pelicula));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });



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

        Call<DetallePelicula> pelicula = peli_service.getDetallePelicula(id_pelicula, api_key, getString(R.string.lang));

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

                        for(int i = 0; i < pelicula_extraida.getProductionCompanies().size(); i++) {
                            if(i == pelicula_extraida.getProductionCompanies().size() - 1)
                                tv_studio.setText(tv_studio.getText() + " " + pelicula_extraida.getProductionCompanies().get(i).getName());
                            else
                                tv_studio.setText(tv_studio.getText() + " " + pelicula_extraida.getProductionCompanies().get(i).getName() + ", ");
                        }

                        valoracion.setNumStars(5);
                        valoracion.setStepSize((float) 0.5);
                        valoracion.setRating(pelicula_extraida.getVoteAverage().floatValue() / 2);

                        tv_descripcion.setText(pelicula_extraida.getOverview());
                        tv_fecha.setText(pelicula_extraida.getReleaseDate());
                        Picasso.get().load("https://image.tmdb.org/t/p/w500" + pelicula_extraida.getPosterPath()).into(portada);
                        portada.setVisibility(View.VISIBLE);
                        url = pelicula_extraida.getHomepage();


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

        PeliculasService service_similares = retrofit.create(PeliculasService.class);
        Call<ListaPeliculas> call_peliculas_similares = service_similares.getPeliculasSimilares(id_pelicula, api_key, getString(R.string.lang));

        call_peliculas_similares.enqueue(new Callback<ListaPeliculas>() {
            @Override
            public void onResponse(Call<ListaPeliculas> call, Response<ListaPeliculas> response) {
                switch(response.code()) {
                    case 200:

                        ListaPeliculas lista_extraida = response.body();
                        lista_similares = lista_extraida.getResults();
                        //Asocia a recylerView el adaptador
                        adaptador_similares.swap(lista_similares);
                        adaptador_similares.notifyDataSetChanged();
                        pelis_similares.setAdapter(adaptador_similares);
                        carga.setVisibility(View.GONE);

                        break;
                    case 401:
                        Log.d("Case 401", "Me salgo por 401, codigo: " + response.code());
                        break;
                    default:
                        Log.d("Default", "Me salgo por default, codigo: " + response.code() + " id peli: " + id_pelicula);
                        break;
                }
            }

            @Override
            public void onFailure(Call<ListaPeliculas> call, Throwable t) {

            }
        });

        //Crea el adaptador, pasándole como parámetro los datos
        final RepartoAdapter adaptador_actores = new RepartoAdapter(lista_actores, new RepartoAdapter.OnItemClickListener() {
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

        Call<Reparto> call_reparto = peli_service.getReparto(id_pelicula, api_key, getString(R.string.lang));

        call_reparto.enqueue(new Callback<Reparto>() {
            @Override
            public void onResponse(Call<Reparto> call, Response<Reparto> response) {
                switch(response.code()) {
                    case 200:

                        Reparto reparto_extraido = response.body();
                        lista_actores = reparto_extraido.getCast();
                        adaptador_actores.swap(lista_actores);
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
                    toast.show();
                }else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        favorito.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {

                        Log.d("FavoriteChanged", "Has pulsado: " + favorite);

                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        if(favorite == true){
                            db.agregarPelicula(Integer.valueOf(id_pelicula));
                        }else{
                            db.deletePelicula(Integer.valueOf(id_pelicula));
                        }
                        db.close();
                    }
                });


    }
}