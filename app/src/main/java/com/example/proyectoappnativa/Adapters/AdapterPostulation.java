package com.example.proyectoappnativa.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.R;

import java.util.ArrayList;

import com.example.proyectoappnativa.Entidades.Postulation;
import com.example.proyectoappnativa.utils.Utils;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPostulation extends RecyclerView.Adapter<AdapterPostulation.PostulationViewHolder> implements View.OnClickListener {

    ArrayList<Postulation> listPostulations;
    private View.OnClickListener listener;
    Activity activity;

    public AdapterPostulation(ArrayList<Postulation> listPostulations, Activity activity) {
        this.listPostulations = listPostulations;
        this.activity = activity;
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

        if(Utils.portrait == true){
            holder.txtDescription.setText(postulation.getDescription());
        }
        Glide.with(activity).load(listPostulations.get(position).getImage()).into(holder.ivFoto);
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
        ImageView ivFoto;


        public PostulationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.idNombre);
            ivFoto = (ImageView) itemView.findViewById(R.id.idImage);
            if(Utils.portrait == true){
                txtDescription = (TextView) itemView.findViewById(R.id.idDescription);
            }
        }
    }


}
