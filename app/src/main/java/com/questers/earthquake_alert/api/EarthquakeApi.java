package com.questers.earthquake_alert.api;

import com.questers.earthquake_alert.model.EarthquakeResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EarthquakeApi {
    @GET("earthquakes/feed/v1.0/summary/all_day.geojson") // URL de la API
    Call<EarthquakeResponse> getEarthquakes();
}
