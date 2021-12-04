package com.example.proyectoappnativa.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.Models.PeoppleAcepted;
import com.example.proyectoappnativa.Models.Postulation;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPeoppleAcepted extends RecyclerView.Adapter<AdapterPeoppleAcepted.PeoppleAceptedViewHolder> implements View.OnClickListener {

    ArrayList<PeoppleAcepted> applications;
    private View.OnClickListener listener;
    Activity activity;
    String idUser;
    fireService firebase = new fireService();
    Task<DocumentSnapshot> data;
    Postulation postulation = new Postulation();

    public AdapterPeoppleAcepted(ArrayList<PeoppleAcepted> applications, Activity activity) {
        this.applications = applications;
        this.activity = activity;
    }


    @NonNull
    @Override
    public AdapterPeoppleAcepted.PeoppleAceptedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peopple_acepted, null, false);
        view.setOnClickListener(this);
        return new AdapterPeoppleAcepted.PeoppleAceptedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeoppleAceptedViewHolder holder, int position) {
        PeoppleAcepted peopple = applications.get(position);
        holder.textName.setText(peopple.getName());
        holder.textPhone.setText(peopple.getPhone());
        holder.textPostulation.setText(peopple.getPostulation());
        if(peopple.getImageURL() != null){
            Glide.with(activity).load(peopple.getImageURL()).into(holder.imageView);
        }
        //Glide.with(activity).load(user.getImageURL()).into(holder.ivPhoto);
        //idUser = user.getId();
        //holder.user = user;

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v, holder.getAdapterPosition(), peopple.getPhone());
                return false;
            }
        });

    }

    public void showPopupMenu(View view, int position, String numero) {
        PopupMenu popup = new PopupMenu(activity, view, Gravity.CENTER);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.call_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String call = "tel:" + numero;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(call));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, activity.getString(R.string.permiseToCall), Toast.LENGTH_SHORT).show();
                    requestPermission();
                }else{
                    activity.startActivity(intent);
                }

                return false;
            }
        });
        popup.show();
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 1);
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


    public class PeoppleAceptedViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPostulation, textPhone;
        CircleImageView ivPhoto;
        User user;
        View mView;
        CircleImageView imageView;

        public PeoppleAceptedViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.namePeopple);
            textPostulation = (TextView) itemView.findViewById(R.id.tvPostulation);
            textPhone = (TextView) itemView.findViewById(R.id.tvPhone);
            imageView = (CircleImageView) itemView.findViewById(R.id.ivPeoppleAcepted);
            mView = itemView;
            //ivPhoto = (CircleImageView) itemView.findViewById(R.id.iconApplications);
        }
    }
}
