package com.example.stayathome;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stayathome.ui.ChooseProject;

import java.util.ArrayList;

//this class connects the ViewHolder and ArrayList to display strings stored in locations
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    ArrayList<String> locations;
    private TextView previousSelection;
    private Context context;

    public MainAdapter(Context context, ArrayList<String> locations) {
        this.context = context;
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

        //highlight and remember location selected by user (so it can be used when user presses confirm)
        private void selectItem(TextView locationName) {

            if (previousSelection != null) {
                previousSelection.setBackground(ContextCompat.getDrawable(context, R.drawable.light_brown));
            }

            locationName.setBackground(ContextCompat.getDrawable(context, R.drawable.light_green));
            previousSelection = locationName;

            String location = locationName.getText().toString();

            ChooseProject.selectedLocation = location;
        }
    }
}
