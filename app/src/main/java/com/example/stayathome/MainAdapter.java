package com.example.stayathome;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stayathome.ui.ChooseProject;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    ArrayList<String> locations;
    private TextView previousSelection;

    public MainAdapter(ArrayList<String> locations) {
        this.locations = locations;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        holder.locationName.setText(locations.get(position));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView locationName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.location_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem(locationName);
                }
            });
        }
        private void selectItem(TextView locationName) {

            if (previousSelection != null) {
                previousSelection.setBackgroundColor(Color.WHITE);
            }
            locationName.setBackgroundColor(Color.LTGRAY);
            previousSelection = locationName;

            String location = locationName.getText().toString();

            ChooseProject.selectedLocation = location;
        }
    }
}
