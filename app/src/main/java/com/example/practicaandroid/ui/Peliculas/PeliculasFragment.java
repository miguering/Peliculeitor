package com.example.practicaandroid.ui.Peliculas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicaandroid.R;
import com.example.practicaandroid.actividades.PeliculaActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PeliculasFragment extends Fragment {

    static final String api_key = "07c6c816ba6f5c1ff4c9c804b4216fd8";

    RecyclerView rv_populares;
    RecyclerView rv_toprated;
    RecyclerView rv_busqueda;
    List<Result> pelis_populares;
    List<Result> pelis_top;
    List<Result> pelis_busqueda;
    ProgressBar barra_carga;
    EditText cuadro_busqueda;
    Button borrar_texto;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_media_final, container, false);

        //RecyclerViews y items de la vista
        rv_busqueda = (RecyclerView) root.findViewById(R.id.recycler_view_busqueda);
        rv_toprated = (RecyclerView) root.findViewById(R.id.recycler_view);
        rv_populares = (RecyclerView) root.findViewById(R.id.recycler_view2);
        barra_carga = (ProgressBar) root.findViewById(R.id.progressBar);
        barra_carga.setVisibility(View.VISIBLE);
        cuadro_busqueda = (EditText) root.findViewById(R.id.editText);
        borrar_texto = (Button) root.findViewById(R.id.btn_clear);

        rv_busqueda.setVisibility(View.INVISIBLE);
        pelis_populares = new ArrayList<>();
        pelis_top = new ArrayList<>();
        pelis_busqueda = new ArrayList<>();


        rv_populares.setHasFixedSize(false);
        rv_toprated.setHasFixedSize(false);

        //Crea el adaptador, pasándole como parámetro los datos
        final PeliculasAdapter adaptador_populares = new PeliculasAdapter(pelis_populares, new PeliculasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result p) {

                int id_pelicula = p.getId();
                Intent intentDetalle = new Intent(getContext(), PeliculaActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_pelicula));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });

        //Crea el adaptador, pasándole como parámetro los datos
        final PeliculasAdapter adaptador_top = new PeliculasAdapter(pelis_top, new PeliculasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result p) {

                int id_pelicula = p.getId();
                Intent intentDetalle = new Intent(root.getContext(), PeliculaActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_pelicula));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });

        //Crea el adaptador, pasándole como parámetro los datos
        final busquedaAdapter adaptador_busqueda = new busquedaAdapter(pelis_busqueda, new busquedaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result p) {

                int id_pelicula = p.getId();
                Intent intentDetalle = new Intent(getContext(), PeliculaActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_pelicula));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });



        //Asocia a recylerView el adaptador
        rv_populares.setAdapter(adaptador_populares);
        rv_toprated.setAdapter(adaptador_top);
        rv_busqueda.setAdapter(adaptador_busqueda);

        //Fija un layout linear al recyclerview
        rv_populares.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rv_toprated.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rv_busqueda.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));


        //Pone una división entre items
        rv_populares.addItemDecoration(
                new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        rv_toprated.addItemDecoration(
                new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        rv_busqueda.addItemDecoration(
                new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        // Pone la animación por defecto
        rv_populares.setItemAnimator(new DefaultItemAnimator());
        rv_toprated.setItemAnimator(new DefaultItemAnimator());
        rv_busqueda.setItemAnimator(new DefaultItemAnimator());


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculasService peli_service = retrofit.create(PeliculasService.class);

        Call<ListaPeliculas> peliculas_populares = peli_service.getPeliculasPopulares(api_key, getString(R.string.lang));

        peliculas_populares.enqueue(new Callback<ListaPeliculas>() {
            @Override
            public void onResponse(Call<ListaPeliculas> call, Response<ListaPeliculas> response) {
                switch(response.code()) {
                    case 200:

                        ListaPeliculas lista_extraida = response.body();
                        pelis_populares = lista_extraida.getResults();
                        adaptador_populares.swap(pelis_populares);
                        adaptador_populares.notifyDataSetChanged();
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

        Call<ListaPeliculas> peliculas_top = peli_service.getPeliculasValoradas(api_key, getString(R.string.lang));

        peliculas_top.enqueue(new Callback<ListaPeliculas>() {
            @Override
            public void onResponse(Call<ListaPeliculas> call, Response<ListaPeliculas> response) {
                switch(response.code()) {
                    case 200:

                        ListaPeliculas lista_extraida = response.body();
                        pelis_top = lista_extraida.getResults();
                        adaptador_top.swap(pelis_top);
                        adaptador_top.notifyDataSetChanged();
                        barra_carga.setVisibility(View.GONE);
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

        cuadro_busqueda.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(cuadro_busqueda.getText().toString().equals("")){
                    borrar_texto.setVisibility(View.GONE);
                    rv_busqueda.setVisibility(View.INVISIBLE);
                }
                else{
                    borrar_texto.setVisibility(View.VISIBLE);
                    Call<ListaPeliculas> call_busqueda = peli_service.getBusquedaPeliculas(api_key, getString(R.string.lang), cuadro_busqueda.getText().toString());

                    call_busqueda.enqueue(new Callback<ListaPeliculas>() {
                        @Override
                        public void onResponse(Call<ListaPeliculas> call, Response<ListaPeliculas> response) {
                            switch(response.code()) {
                                case 200:

                                    ListaPeliculas lista_extraida = response.body();
                                    pelis_busqueda = lista_extraida.getResults();
                                    adaptador_busqueda.swap(pelis_busqueda);
                                    adaptador_busqueda.notifyDataSetChanged();
                                    rv_busqueda.setVisibility(View.VISIBLE);
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
        });

        borrar_texto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cuadro_busqueda.setText("");
                borrar_texto.setVisibility(View.GONE);
            }
        });

        return root;
    }


}