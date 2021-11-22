package com.example.proyectoappnativa.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Entidades.Postulation;
import com.example.proyectoappnativa.Entidades.User;
import com.example.proyectoappnativa.R;
import com.example.proyectoappnativa.utils.Utils;
import com.google.api.Context;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterApplications extends RecyclerView.Adapter<AdapterApplications.ApplicationsViewHolder> implements View.OnClickListener {

    ArrayList<User> applications;
    private View.OnClickListener listener;
    Activity activity;

    public AdapterApplications(ArrayList<User> applications, Activity activity) {
        this.applications = applications;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdapterApplications.ApplicationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.applications_list_layout, null, false);
        view.setOnClickListener(this);
        return new AdapterApplications.ApplicationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterApplications.ApplicationsViewHolder holder, int position) {
        User user = applications.get(position);
        holder.textName.setText(user.getName());
        holder.textDescription.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return applications.size();
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

    public class ApplicationsViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textDescription;

        public ApplicationsViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.nameTV);
            textDescription = (TextView) itemView.findViewById(R.id.cityTextView);

        }
    }
}
