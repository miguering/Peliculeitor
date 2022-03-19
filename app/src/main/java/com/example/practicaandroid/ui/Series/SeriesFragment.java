package com.example.practicaandroid.ui.Series;

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
import com.example.practicaandroid.actividades.SerieActivity;
import com.example.practicaandroid.ui.Peliculas.ListaPeliculas;
import com.example.practicaandroid.ui.Peliculas.PeliculasService;
import com.example.practicaandroid.ui.Series.ListaSeries;
import com.example.practicaandroid.ui.Series.SeriesAdapter;
import com.example.practicaandroid.ui.Series.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeriesFragment extends Fragment {

    static final String api_key = "07c6c816ba6f5c1ff4c9c804b4216fd8";

    RecyclerView rv_populares;
    RecyclerView rv_toprated;
    RecyclerView rv_busqueda;
    List<Result> series_populares;
    List<Result> series_top;
    List<Result> series_busqueda;
    Button borrar_texto;

    ProgressBar barra_carga;
    EditText cuadro_busqueda;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_media_final, container, false);


        //RecyclerViews y items de la vista
        rv_busqueda = root.findViewById(R.id.recycler_view_busqueda);
        rv_toprated = root.findViewById(R.id.recycler_view);
        rv_populares = root.findViewById(R.id.recycler_view2);
        barra_carga = root.findViewById(R.id.progressBar);
        barra_carga.setVisibility(View.VISIBLE);
        cuadro_busqueda = (EditText) root.findViewById(R.id.editText);
        borrar_texto = (Button) root.findViewById(R.id.btn_clear);

        rv_busqueda.setVisibility(View.INVISIBLE);
        series_populares = new ArrayList<>();
        series_top = new ArrayList<>();
        series_busqueda = new ArrayList<>();

        rv_populares.setHasFixedSize(false);
        rv_toprated.setHasFixedSize(false);

        //Crea el adaptador, pasándole como parámetro los datos
        final SeriesAdapter adaptador_populares = new SeriesAdapter(series_populares, new SeriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result p) {

                int id_serie = p.getId();
                Intent intentDetalle = new Intent(getContext(), SerieActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_serie));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });

        //Crea el adaptador, pasándole como parámetro los datos
        final SeriesAdapter adaptador_top = new SeriesAdapter(series_top, new SeriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result p) {

                int id_serie = p.getId();
                Intent intentDetalle = new Intent(root.getContext(), SerieActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_serie));
                intentDetalle.putExtra("api_key", api_key);
                startActivity(intentDetalle);

            }
        });

        //Crea el adaptador, pasándole como parámetro los datos
        final busquedaAdapter adaptador_busqueda = new busquedaAdapter(series_busqueda, new busquedaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Result p) {

                int id_serie = p.getId();
                Intent intentDetalle = new Intent(root.getContext(), SerieActivity.class);
                intentDetalle.putExtra("id", String.valueOf(id_serie));
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

        Call<ListaSeries> call_series_populares = peli_service.getSeriesPopulares(api_key, getString(R.string.lang));

        call_series_populares.enqueue(new Callback<ListaSeries>() {
            @Override
            public void onResponse(Call<ListaSeries> call, Response<ListaSeries> response) {
                switch(response.code()) {
                    case 200:

                        ListaSeries lista_extraida = response.body();
                        series_populares = lista_extraida.getResults();
                        adaptador_populares.swap(series_populares);
                        adaptador_populares.notifyDataSetChanged();
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

        Call<ListaSeries> call_series_top = peli_service.getSeriesTop(api_key, getString(R.string.lang));

        call_series_top.enqueue(new Callback<ListaSeries>() {
            @Override
            public void onResponse(Call<ListaSeries> call, Response<ListaSeries> response) {
                switch(response.code()) {
                    case 200:

                        ListaSeries lista_extraida = response.body();
                        series_top = lista_extraida.getResults();
                        adaptador_top.swap(series_top);
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
            public void onFailure(Call<ListaSeries> call, Throwable t) {

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
                    Call<ListaSeries> call_busqueda = peli_service.getBusquedaSeries(api_key, getString(R.string.lang), cuadro_busqueda.getText().toString());

                    call_busqueda.enqueue(new Callback<ListaSeries>() {
                        @Override
                        public void onResponse(Call<ListaSeries> call, Response<ListaSeries> response) {
                            switch(response.code()) {
                                case 200:

                                    ListaSeries lista_extraida = response.body();
                                    series_busqueda = lista_extraida.getResults();
                                    adaptador_busqueda.swap(series_busqueda);
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
                        public void onFailure(Call<ListaSeries> call, Throwable t) {

                        }
                    });

                    rv_busqueda.setVisibility(View.VISIBLE);
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