package com.example.proyectoappnativa.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoappnativa.R;

import java.util.ArrayList;

import com.example.proyectoappnativa.Entidades.Postulation;
import com.example.proyectoappnativa.utils.Utils;

public class AdapterPostulation extends RecyclerView.Adapter<AdapterPostulation.PostulationViewHolder> implements View.OnClickListener {

    ArrayList<Postulation> listPostulations;
    private View.OnClickListener listener;

    public AdapterPostulation(ArrayList<Postulation> listPostulations) {
        this.listPostulations = listPostulations;
    }

    @NonNull
    @Override
    public PostulationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postulation_list_layout, null, false);
        view.setOnClickListener(this);
        return new PostulationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostulationViewHolder holder, int position) {
        holder.txtNombre.setText(listPostulations.get(position).getName());
        if(Utils.portrait == true){
            holder.txtDescription.setText(listPostulations.get(position).getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return listPostulations.size();
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

    public class PostulationViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtDescription;

        public PostulationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.idNombre);
            if(Utils.portrait == true){
                txtDescription = (TextView) itemView.findViewById(R.id.idDescription);
            }
        }
    }
}
