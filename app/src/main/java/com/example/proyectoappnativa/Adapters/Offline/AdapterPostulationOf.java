package com.example.proyectoappnativa.Adapters.Offline;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Adapters.AdapterPostulation;
import com.example.proyectoappnativa.Models.Offline.PostulationOff;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterPostulationOf extends RecyclerView.Adapter<AdapterPostulationOf.PostulationOffViewHolder> implements View.OnClickListener {


    ArrayList<PostulationOff> listPostulationsOff;
    private View.OnClickListener listener;
    Activity activity;
    private List<PostulationOff> originalItems;


    public AdapterPostulationOf(ArrayList<PostulationOff> postulationsData, Activity activity) {
        this.listPostulationsOff = postulationsData;
        this.activity = activity;
        this.originalItems = new ArrayList<>();
        originalItems.addAll(listPostulationsOff);
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onClick(v);
        }
    }

    public void filter(final String strSearch){
        if(strSearch.length() == 0){
            listPostulationsOff.clear();
            listPostulationsOff.addAll(originalItems);
        }else{
            listPostulationsOff.clear();
            listPostulationsOff.addAll(originalItems.stream()
                    .filter(postulation -> postulation.getName().toLowerCase().contains(strSearch))
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



    @NonNull
    @Override
    public PostulationOffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postulation_list_layout, null, false);
        ViewGroup.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        view.setOnClickListener(this);
        return new AdapterPostulationOf.PostulationOffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostulationOffViewHolder holder, int position) {
        PostulationOff postulation = listPostulationsOff.get(position);
        holder.txtNombre.setText(postulation.getName());
        holder.txtDescription.setText(postulation.getDescription());
    }

    @Override
    public int getItemCount() {
        return listPostulationsOff.size();
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

    private void keywordToChip(List<String> keywords, ChipGroup chipGroup) {
        for (String keyword : keywords) {
            Chip chip = new Chip(activity);
            chip.setText(keyword);
            chipGroup.addView(chip);
        }
    }


    public class PostulationOffViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDescription;
        ImageView ivFoto;
        ChipGroup chipGroup;

        public PostulationOffViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.idNombre);
            ivFoto = (ImageView) itemView.findViewById(R.id.idImage);
            txtDescription = (TextView) itemView.findViewById(R.id.idDescription);
            chipGroup = (ChipGroup) itemView.findViewById(R.id.chipGroup);
        }
    }

}
