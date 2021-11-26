package com.example.proyectoappnativa.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.proyectoappnativa.Models.User;
import com.example.proyectoappnativa.Firebase.fireService;
import com.example.proyectoappnativa.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterApplications extends RecyclerView.Adapter<AdapterApplications.ApplicationsViewHolder> implements View.OnClickListener {

    ArrayList<User> applications;
    private View.OnClickListener listener;
    Activity activity;
    String idUser;
    fireService firebase = new fireService();
    Task<DocumentSnapshot> data;


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
        Glide.with(activity).load(user.getImageURL()).into(holder.ivPhoto);
        idUser = user.getId();
        holder.user = user;
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
/*
    // Dismiss
    public void dismissApplications(int pos){
        applications.remove(pos);
        this.notifyItemRemoved(pos);
    }
*/
    public class ApplicationsViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textDescription;
        CircleImageView ivPhoto;
        Button btnUsers;
        User user;

        public ApplicationsViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.nameTV);
            textDescription = (TextView) itemView.findViewById(R.id.cityTextView);
            ivPhoto = (CircleImageView) itemView.findViewById(R.id.iconApplications);
            btnUsers = (Button) itemView.findViewById(R.id.btnApplications);


            btnUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity, user.getId(), Toast.LENGTH_SHORT).show();
                    sendNotificationTo(user.getId());
                }
            });

        }
    }

    private void getInfoPostulation(){

    }

    private void sendNotificationTo(String id){
        RequestQueue myRequest = Volley.newRequestQueue(activity.getApplicationContext());
        JSONObject json = new JSONObject();
        data = firebase.getInfoUser(id);
        data.addOnCompleteListener(documentSnapshot -> {
            String token;
            token = documentSnapshot.getResult().getString("token");
            try{

                json.put("to", token);
                JSONObject notification = new JSONObject();
                notification.put("titulo", "Postulación");
                notification.put("detalle", "Has sido aceptado");

                json.put("data", notification);

                String URL = "https://fcm.googleapis.com/fcm/send";

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json,null, null){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> header = new HashMap<>();
                        header.put("content-type", "application/json");
                        header.put("authorization", "key=AAAAlAHvnnc:APA91bEtbYrtvpxTOE5rDBgwLnQrVJKoXFVsNpl1cMoL200upzbU_kbPZpOM2LPfPT5Oew6uL-7G6B7MsvPFJzAfTzsllB26rSAdtQ0aDSwm1O1nD5o3tbNk54215OVKj5VDkzLQpe2i");
                        return header;
                    }
                };
                myRequest.add(request);
            }catch (JSONException e){
                e.printStackTrace();
            }
        });
    }

}
