package com.questers.earthquake_alert.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.questers.earthquake_alert.R;
import com.questers.earthquake_alert.model.Feature;

import java.util.List;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder> {

    private List<Feature> earthquakes;
    private OnEarthquakeClickListener listener;

    public interface OnEarthquakeClickListener {
        void onEarthquakeClick(Feature feature);
    }

    public EarthquakeAdapter(List<Feature> earthquakes, OnEarthquakeClickListener listener) {
        this.earthquakes = earthquakes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EarthquakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.earthquake_item, parent, false);
        return new EarthquakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarthquakeViewHolder holder, int position) {
        Feature earthquake = earthquakes.get(position);
        holder.bind(earthquake, listener);
    }

    @Override
    public int getItemCount() {
        return earthquakes.size();
    }

    static class EarthquakeViewHolder extends RecyclerView.ViewHolder {

        private TextView intensityTextView;
        private TextView timeTextView;
        private TextView locationTextView;

        public EarthquakeViewHolder(@NonNull View itemView) {
            super(itemView);
            intensityTextView = itemView.findViewById(R.id.intensityTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
        }

        public void bind(Feature earthquake, OnEarthquakeClickListener listener) {
            intensityTextView.setText("Intensidad: " + earthquake.properties.mag); // Asegúrate de que "mag" sea la propiedad correcta
            timeTextView.setText("Hora: " + earthquake.properties.time); // Convierte el tiempo a un formato legible si es necesario
            locationTextView.setText("Lugar: " + earthquake.properties.place);

            itemView.setOnClickListener(v -> listener.onEarthquakeClick(earthquake));
        }
    }
}
