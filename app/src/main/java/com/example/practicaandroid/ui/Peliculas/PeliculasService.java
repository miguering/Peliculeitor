package com.example.practicaandroid.ui.Peliculas;


import com.example.practicaandroid.ui.Series.DetalleCastSerie;
import com.example.practicaandroid.ui.Series.DetalleSerie;
import com.example.practicaandroid.ui.Series.ListaSeries;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PeliculasService {
    @GET("movie/popular")
    Call<ListaPeliculas> getPeliculasPopulares(@Query("api_key") String api_key, @Query("language") String language);

    @GET("movie/top_rated")
    Call<ListaPeliculas> getPeliculasValoradas(@Query("api_key") String api_key, @Query("language") String language);

    @GET("movie/{id}")
    Call<DetallePelicula> getDetallePelicula(@Path("id") String id, @Query("api_key") String api_key, @Query("language") String language);

    @GET("movie/{id}/similar")
    Call<ListaPeliculas> getPeliculasSimilares(@Path("id") String id, @Query("api_key") String api_key, @Query("language") String language);

    @GET("movie/{id}/casts")
    Call<Reparto> getReparto(@Path("id") String id, @Query("api_key") String api_key, @Query("language") String language);

    @GET("person/{id}")
    Call<DetalleActor> getDetalleActor(@Path("id") String id, @Query("api_key") String api_key, @Query("language") String language);

    @GET("tv/popular")
    Call<ListaSeries> getSeriesPopulares(@Query("api_key") String api_key, @Query("language") String language);

    @GET("tv/top_rated")
    Call<ListaSeries> getSeriesTop(@Query("api_key") String api_key, @Query("language") String language);

    @GET("tv/{id}")
    Call<DetalleSerie> getDetalleSerie(@Path("id") String id, @Query("api_key") String api_key, @Query("language") String language);

    @GET("tv/{id}/similar")
    Call<ListaSeries> getSeriesSimilares(@Path("id") String id, @Query("api_key") String api_key, @Query("language") String language);

    @GET("tv/{id}/credits")
    Call<Reparto> getRepartoSerie(@Path("id") String id, @Query("api_key") String api_key, @Query("language") String language);

    @GET("search/movie")
    Call<ListaPeliculas> getBusquedaPeliculas(@Query("api_key") String api_key, @Query("language") String language, @Query("query") String query);

    @GET("search/tv")
    Call<ListaSeries> getBusquedaSeries(@Query("api_key") String api_key, @Query("language") String language, @Query("query") String query);

    @GET("discover/movie")
    Call<ListaPeliculas> getPeliculasActor(@Query("api_key") String api_key, @Query("language") String language, @Query("with_people") String id);

    @GET("person/{id}/tv_credits")
    Call<DetalleCastSerie> getSeriesActor(@Path("id") String id, @Query("api_key") String api_key, @Query("language") String language);

}
