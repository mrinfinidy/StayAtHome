package com.example.stayathome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GrownTreesAdapter extends RecyclerView.Adapter<GrownTreesAdapter.GrownTreesViewHolder> {

    private ArrayList<TreeListItem> grownVTrees;

    public static class GrownTreesViewHolder extends RecyclerView.ViewHolder {
        public ImageView treeListImg;
        public TextView treeListName;
        public TextView plantabilityInfo;

        public GrownTreesViewHolder(@NonNull View itemView) {
            super(itemView);
            treeListImg = itemView.findViewById(R.id.treeListImg);
            treeListName = itemView.findViewById(R.id.treeListName);
            plantabilityInfo = itemView.findViewById(R.id.plantabilityInfo);
        }
    }

    public GrownTreesAdapter(ArrayList<TreeListItem> grownVTrees) {
        this.grownVTrees = grownVTrees;
    }

    @NonNull
    @Override
    public GrownTreesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tree_list_item, parent, false);
        return new GrownTreesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GrownTreesViewHolder holder, int position) {
        TreeListItem currentItem = grownVTrees.get(position);

        holder.treeListImg.setImageResource(currentItem.getTreeListImg());
        holder.treeListName.setText(currentItem.getTreeListName());
        holder.plantabilityInfo.setText(currentItem.getPlantable());
    }

    @Override
    public int getItemCount() {
        return grownVTrees.size();
    }
}
