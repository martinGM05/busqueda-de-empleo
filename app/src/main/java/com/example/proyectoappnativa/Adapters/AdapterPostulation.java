package com.example.proyectoappnativa.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Models.Offline.PostulationOff;
import com.example.proyectoappnativa.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.proyectoappnativa.Models.Postulation;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class AdapterPostulation extends RecyclerView.Adapter<AdapterPostulation.PostulationViewHolder> implements View.OnClickListener {

    ArrayList<Postulation> listPostulations;
    private View.OnClickListener listener;
    Activity activity;
    private List<Postulation> originalItems;

    public AdapterPostulation(ArrayList<Postulation> listPostulations, Activity activity) {
        this.listPostulations = listPostulations;
        this.activity = activity;
        this.originalItems = new ArrayList<>();
        originalItems.addAll(listPostulations);
    }

    @NonNull
    @Override
    public PostulationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postulation_list_layout, null, false);
        ViewGroup.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        view.setOnClickListener(this);
        return new PostulationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostulationViewHolder holder, int position) {
        Postulation postulation = listPostulations.get(position);
        holder.txtNombre.setText(postulation.getName());
        holder.txtDescription.setText(postulation.getDescription());
        Glide.with(activity).load(listPostulations.get(position).getImage()).into(holder.ivFoto);

        clearChipGroup(holder.chipGroup);
        keywordToChip(postulation.getKeywords(), holder.chipGroup);
    }

    @Override
    public int getItemCount() {
        return listPostulations.size();
    }

    public void filter(final String strSearch){
        if(strSearch.length() == 0){
            listPostulations.clear();
            listPostulations.addAll(originalItems);
        }else{
            listPostulations.clear();
            listPostulations.addAll(originalItems.stream()
                    .filter(postulation -> postulation.getKeywords().stream()
                            .anyMatch(keyword -> keyword.toLowerCase().contains(strSearch.toLowerCase())))
                    .collect(Collectors.toList()));

            /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                listPostulations.clear();
                List<Postulation> collect = originalItems.stream()
                        .filter(item -> item.getName().toLowerCase().contains(strSearch))
                        .collect(Collectors.toList());
                listPostulations.clear();
                listPostulations.addAll(collect);
            }else{
                listPostulations.clear();
                for (Postulation i : originalItems){
                    if(i.getName().toLowerCase().contains(strSearch)){
                        listPostulations.clear();
                         listPostulations.add(i);
                    }
                }
            }*/
        }
        notifyDataSetChanged();
    }

    public void clearChipGroup(ChipGroup chipGroup){
        int count = chipGroup.getChildCount();
        for(int i = 0; i < count; i++){
            chipGroup.removeViewAt(0);
        }
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onClick(v);
        }
    }

    private void keywordToChip(List<String> keywords, ChipGroup chipGroup) {
        for (String keyword : keywords) {
            Chip chip = new Chip(activity);
            chip.setText(keyword);
            chipGroup.addView(chip);
        }
    }

    public class PostulationViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDescription;
        ImageView ivFoto;
        ChipGroup chipGroup;

        public PostulationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.idNombre);
            ivFoto = (ImageView) itemView.findViewById(R.id.idImage);
            txtDescription = (TextView) itemView.findViewById(R.id.idDescription);
            chipGroup = (ChipGroup) itemView.findViewById(R.id.chipGroup);
        }
    }


}
