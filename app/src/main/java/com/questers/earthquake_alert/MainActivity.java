package com.questers.earthquake_alert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.questers.earthquake_alert.RecyclerView.EarthquakeAdapter;
import com.questers.earthquake_alert.api.EarthquakeApi;
import com.questers.earthquake_alert.model.EarthquakeResponse;
import com.questers.earthquake_alert.model.Feature;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RecyclerView alertsRecyclerView;
    private EarthquakeApi earthquakeApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar RecyclerView
        alertsRecyclerView = findViewById(R.id.alertsRecyclerView);
        alertsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar Retrofit para obtener los datos de terremotos
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://earthquake.usgs.gov/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        earthquakeApi = retrofit.create(EarthquakeApi.class);
        fetchEarthquakeData();
    }

    private void fetchEarthquakeData() {
        Call<EarthquakeResponse> call = earthquakeApi.getEarthquakes();
        call.enqueue(new Callback<EarthquakeResponse>() {
            @Override
            public void onResponse(Call<EarthquakeResponse> call, Response<EarthquakeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Feature> earthquakes = response.body().features;
                    displayEarthquakesOnMap(earthquakes);
                    setupRecyclerView(earthquakes);
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.code() + " - " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EarthquakeResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error fetching data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEarthquakesOnMap(List<Feature> earthquakes) {
        if (mMap == null) {
            Toast.makeText(this, "El mapa no está listo", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            for (Feature feature : earthquakes) {
                List<Double> coordinates = feature.geometry.coordinates;
                LatLng location = new LatLng(coordinates.get(1), coordinates.get(0));
                mMap.addMarker(new MarkerOptions().position(location).title(feature.properties.place));
            }
            // Ajustar la cámara una vez después de añadir todos los marcadores
            if (!earthquakes.isEmpty()) {
                List<Double> firstCoordinates = earthquakes.get(0).geometry.coordinates;
                LatLng firstLocation = new LatLng(firstCoordinates.get(1), firstCoordinates.get(0));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 5));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al mostrar terremotos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView(List<Feature> earthquakes) {
        EarthquakeAdapter adapter = new EarthquakeAdapter(earthquakes, feature -> {
            // Al hacer clic en un elemento, mover el mapa a la ubicación del sismo
            List<Double> coordinates = feature.geometry.coordinates;
            LatLng location = new LatLng(coordinates.get(1), coordinates.get(0));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        });
        alertsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
